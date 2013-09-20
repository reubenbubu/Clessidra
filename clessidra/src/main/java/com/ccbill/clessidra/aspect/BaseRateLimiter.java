package com.ccbill.clessidra.aspect;


import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ccbill.clessidra.annotations.RateLimited;
import com.ccbill.clessidra.annotations.RateLimitedQueue;
import com.ccbill.clessidra.exception.RateLimiterException;
import com.ccbill.clessidra.queue.LimitedQueueSemaphore;
import com.ccbill.clessidra.queue.SemaphoreImpatienceException;
import com.ccbill.clessidra.queue.SemaphoreQueueFullException;
import com.ccbill.clessidra.strategy.AbstractLimiterStrategy;
import com.ccbill.clessidra.strategy.LimiterStrategyConclusion;



/**
 * 
 * {@link BaseRateLimiter} holds the core logic of when a method call is intercepted for rate
 * limiting.
 * 
 * @author reubena
 * 
 */
public class BaseRateLimiter
		implements ApplicationContextAware {

	private Logger logger = Logger.getLogger(this.getClass());

	private ApplicationContext applicationContext;

	private static ConcurrentHashMap<String, LimitedQueueSemaphore> methodGroupSemaphores = new ConcurrentHashMap<String, LimitedQueueSemaphore>();



	public BaseRateLimiter() {
	}


	/**
	 * The core Rate Limiter logic. This method will call the limiter strategy chain and decide
	 * whether to annotated method will run or not.
	 * 
	 * @param joinPoint
	 * @return The object returned by the annotated method
	 * @throws Throwable
	 */
	public Object intercept(ProceedingJoinPoint joinPoint)
			throws Throwable {

		// extract MethodSignature
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		logger.debug("Intercepted a method call on " + methodSignature.toString());

		// find the RateLimited annotation, method gets priority over class
		RateLimited annotation = methodSignature.getMethod().getAnnotation(RateLimited.class);
		if (annotation == null) {
			annotation = (RateLimited) joinPoint.getSourceLocation().getWithinType().getAnnotation(RateLimited.class);
		}

		// resolve method group name based on the methodGrouping in the annotation
		String methodGroupName = null;
		switch (annotation.methodGrouping()) {
			case GROUPED:
				methodGroupName = annotation.groupName();
				break;
			case UNGROUPED:
				methodGroupName = methodSignature.toString();
				break;
			default:
				methodGroupName = methodSignature.toString();
				break;
		}

		RateLimitedQueue queueAnnotation = methodSignature.getMethod().getAnnotation(RateLimitedQueue.class);
		if (queueAnnotation == null) {
			queueAnnotation = (RateLimitedQueue) joinPoint.getSourceLocation().getWithinType().getAnnotation(RateLimitedQueue.class);
		}

		boolean requestQueueable = (queueAnnotation != null);
		int queueSize = requestQueueable ? queueAnnotation.maxQueueSize() : 0;


		// get the limiter strategy chain from the spring context
		AbstractLimiterStrategy limiterStrategyChain = (AbstractLimiterStrategy) applicationContext.getBean(annotation.limiterBean());

		// generate a uuid to be used later on if rollback is required
		UUID methodInvocationUUID = UUIDGenerator.generateUUID();

		// run through the limiter strategy chain and get the conclusion
		LimiterStrategyConclusion conclusion = null;


		synchronized (BaseRateLimiter.class) {
			if (requestQueueable) {
				conclusion = limiterStrategyChain.hasLimitBeenExceededChain(methodGroupName, methodInvocationUUID, joinPoint.getArgs(), false);
			}
			else {
				conclusion = limiterStrategyChain.hasLimitBeenExceededChain(methodGroupName, methodInvocationUUID, joinPoint.getArgs(), true);
			}
		}

		LimitedQueueSemaphore semaphore = null;

		// Only try to acquire a semaphore if the limit has been exceeded
		if (requestQueueable) {

			if (conclusion.getHasLimitBeenExceeded()) {

				semaphore = getSemaphore(methodGroupName, queueSize);
				logger.debug("Waiting for permit. " + semaphore.toString());
				try {
					// Wait until a permit is acquired
					if (queueAnnotation.maxWaitMillis() > 0) {
						semaphore.impatientAcquire(queueAnnotation.maxWaitMillis());
					}
					else {
						semaphore.acquire();
					}
				}
				catch (SemaphoreQueueFullException e) {
					logger.debug("Method call cannot be queued.", e);
					throw new RateLimiterException("Method call blocked.");
				}
				catch (SemaphoreImpatienceException e) {
					logger.debug("Method call waited in queue for too long.", e);
					throw new RateLimiterException("Method call blocked.");
				}

				logger.debug("Permit acquired. " + semaphore.toString());

				// Only proceed when the strategy/strategies allow it
				do {
					synchronized (BaseRateLimiter.class) {
						conclusion =
								limiterStrategyChain.hasLimitBeenExceededChain(methodGroupName, methodInvocationUUID, joinPoint.getArgs(), false);
					}
					if (conclusion.getHasLimitBeenExceeded()) {
						Thread.sleep(1000);
					}
				}
				while (conclusion.getHasLimitBeenExceeded());

			}
			synchronized (BaseRateLimiter.class) {
				conclusion = limiterStrategyChain.hasLimitBeenExceededChain(methodGroupName, methodInvocationUUID, joinPoint.getArgs(), true);
			}
		}

		// if limit is not exceeded call the method and do a post invocation clean up where needed
		if (!conclusion.getHasLimitBeenExceeded()) {
			logger.trace("About to run method " + methodSignature.toString());
			try {
				Object obj = joinPoint.proceed(joinPoint.getArgs());
				logger.trace("Finished running " + methodSignature.toString());
				return obj;
			}
			catch (Throwable t) {
				logger.debug("Method " + methodSignature.toString() + " did not exit gracefully. " + t);
				throw t;
			}
			finally {

				synchronized (BaseRateLimiter.class) {
					limiterStrategyChain.postInvocationCleanupChain(methodGroupName, methodInvocationUUID, joinPoint.getArgs());
				}

				// Release a permit
				if (requestQueueable) {
					LimitedQueueSemaphore s = getSemaphore(methodGroupName, queueSize);
					s.releaseToQueue();
					logger.debug("Releasing. " + s.toString());

				}
			}
		}

		// throw an exception containing the conclusion
		throw new RateLimiterException("Method call blocked.", conclusion);
	}


	/**
	 * Spring will automagically set the application context using this method thanks to
	 * {@link ApplicationContextAware}
	 */
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}


	/**
	 * Try to get semaphore associated with methodGroup, if not found create it
	 * 
	 * @param methodGroupName
	 *            - The method group name
	 * @param maxQueueSize
	 *            - The size of the queue in case it needs to be created
	 * @return
	 */
	private LimitedQueueSemaphore getSemaphore(String methodGroupName, int maxQueueSize) {
		synchronized (this) {
			LimitedQueueSemaphore semaphore = this.methodGroupSemaphores.get(methodGroupName);
			if (semaphore == null) {
				semaphore = new LimitedQueueSemaphore(0, true, maxQueueSize);
				this.methodGroupSemaphores.put(methodGroupName, semaphore);
			}
			return semaphore;
		}
	}

}
