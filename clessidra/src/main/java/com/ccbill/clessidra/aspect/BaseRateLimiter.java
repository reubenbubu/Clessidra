package com.ccbill.clessidra.aspect;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ccbill.clessidra.annotations.RateLimited;
import com.ccbill.clessidra.annotations.RateLimitedQueue;
import com.ccbill.clessidra.exception.RateLimiterException;
import com.ccbill.clessidra.strategy.AbstractLimiterStrategy;
import com.ccbill.clessidra.strategy.LimiterStrategyConclusion;

/**
 * 
 * {@link BaseRateLimiter} holds the core logic of when a method call is intercepted for rate limiting.
 * 
 * @author reubena
 * 
 */
public class BaseRateLimiter implements ApplicationContextAware {

	private Logger logger = Logger.getLogger(BaseRateLimiter.class);

	private ApplicationContext applicationContext;

	private static Map<String, LinkedBlockingDeque<Thread>> threadQueue = new ConcurrentHashMap<String, LinkedBlockingDeque<Thread>>();

	public BaseRateLimiter() {

	}

	/**
	 * The core Rate Limiter logic. This method will call the limiter strategy chain and decide whether to annotated method will run or not.
	 * 
	 * @param joinPoint
	 * @return The object returned by the annotated method
	 * @throws Throwable
	 */
	public Object intercept(ProceedingJoinPoint joinPoint) throws Throwable {

		// extract MethodSignature
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		logger.debug("Intercepted a method call on " + methodSignature.toString());

		// find the RateLimited annotation, method gets priority over class
		RateLimited rateLimitedAnnotation = methodSignature.getMethod().getAnnotation(RateLimited.class);
		if (rateLimitedAnnotation == null) {
			rateLimitedAnnotation = (RateLimited) joinPoint.getSourceLocation().getWithinType().getAnnotation(RateLimited.class);
		}

		// resolve method group name based on the methodGrouping in the annotation
		String methodGroupName = null;
		switch (rateLimitedAnnotation.methodGrouping()) {
		case GROUPED:
			methodGroupName = rateLimitedAnnotation.groupName();
			break;
		case UNGROUPED:
			methodGroupName = methodSignature.toString();
			break;
		default:
			methodGroupName = methodSignature.toString();
			break;
		}

		// get the queueing configuration
		RateLimitedQueue rateLimitedQueueAnnotation = methodSignature.getMethod().getAnnotation(RateLimitedQueue.class);
		if (rateLimitedQueueAnnotation == null) {
			rateLimitedQueueAnnotation = (RateLimitedQueue) joinPoint.getSourceLocation().getWithinType().getAnnotation(RateLimitedQueue.class);
		}

		boolean methodCallQueueable = rateLimitedQueueAnnotation != null;
		int queueMaxSize = 0;

		LinkedBlockingDeque<Thread> queue = null;
		
		if (methodCallQueueable) {
		    
		    queueMaxSize = rateLimitedQueueAnnotation.queueSize();
		    
			boolean sleepEternally = false;
			synchronized (BaseRateLimiter.class) {
			
			    queue = this.threadQueue.get(methodGroupName);
			    
			    if (queue == null) {
	                queue = new LinkedBlockingDeque<Thread>(queueMaxSize);
	                threadQueue.put(methodGroupName, queue);
	                logger.debug("Created queue " + methodGroupName);
	            }               
			    
			    if (!queue.isEmpty()) {
			        sleepEternally = addCurrentThreadToQueue(methodGroupName, queueMaxSize, false);
			    }
			}
			if (sleepEternally)
				sleepEternally();
		}

		// get the limiter strategy chain from the spring context
		AbstractLimiterStrategy limiterStrategyChain = (AbstractLimiterStrategy) applicationContext.getBean(rateLimitedAnnotation.limiterBean());

		// generate a uuid to be used later on if rollback is required
		UUID methodInvocationUUID = UUIDGenerator.generateUUID();

		LimiterStrategyConclusion conclusion;
		
		if (methodCallQueueable) {
			do {
				conclusion = limiterStrategyChain.hasLimitBeenExceededChain(methodGroupName, methodInvocationUUID, joinPoint.getArgs());
				if (conclusion.getHasLimitBeenExceeded()) {
					boolean sleepEternally = false;
					synchronized (BaseRateLimiter.class) {
						if (!queue.contains(Thread.currentThread())) {
						    sleepEternally = addCurrentThreadToQueue(methodGroupName, queueMaxSize, true);
						}
					}
					if (sleepEternally)
						sleepEternally();
				}
			} while (conclusion.getHasLimitBeenExceeded());
			
		} else {
			conclusion = limiterStrategyChain.hasLimitBeenExceededChain(methodGroupName, methodInvocationUUID, joinPoint.getArgs());
		}

		// if limit is not exceeded call the method and do a post invocation clean up where needed
		if (!conclusion.getHasLimitBeenExceeded()) {
			logger.trace("About to run method " + methodSignature.toString());
			try {
				Object obj = joinPoint.proceed(joinPoint.getArgs());
				logger.trace("Finished running " + methodSignature.toString());
				return obj;
			} catch (Throwable t) {
				logger.debug("Method " + methodSignature.toString() + " did not exit gracefully. " + t);
				throw t;
			} finally {
				limiterStrategyChain.postInvocationCleanupChain(methodGroupName, methodInvocationUUID, joinPoint.getArgs());
				synchronized (BaseRateLimiter.class) {
					if (queue != null && queue.peek() != null) {
						Thread sleepingThread = queue.poll();
						logger.debug("Waking up " + sleepingThread.getName());
						sleepingThread.interrupt();
					}
				}
			}
		}

		// throw an exception containing the conclusion
		throw new RateLimiterException("Method call blocked.", conclusion);
	}

	/**
	 * Spring will automagically set the application context using this method thanks to {@link ApplicationContextAware}
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	private void sleepEternally() {
		logger.debug("Putting thread " + Thread.currentThread().getName() + " to sleep.");

		try {
			while (true) {
				Thread.sleep(Long.MAX_VALUE);
			}
		} catch (InterruptedException ignore) {
			logger.debug("Awakened thread " + Thread.currentThread().getName() + ".");
		}
	}
	
	private LimiterStrategyConclusion buildQueueFullConclusion(String methodGroupName, int queueMaxSize) {
	
	    LimiterStrategyConclusion conclusion = new LimiterStrategyConclusion(true);
        conclusion.setDetailedExceededMessage("Too many requests queued for method group " + methodGroupName + ", queue size "
                + queueMaxSize + ".");
        conclusion.setGenericExceededMessage("Too many requests queued.");
        conclusion.setHasExceededQueueSize(true);
        
        return conclusion;
	}
	
	private boolean addCurrentThreadToQueue(String methodGroupName, int queueMaxSize, boolean head) throws RateLimiterException {
	 
	    LinkedBlockingDeque<Thread> queue = this.threadQueue.get(methodGroupName);
        
	    // try to add to queue
        boolean added = false;
        if (head){
            try  {
                queue.addFirst(Thread.currentThread());
                added = true;
            } catch (IllegalStateException full) {
            }
        } else {
            added = queue.add(Thread.currentThread());
        }
        if (added) {
            logger.debug("Added " + Thread.currentThread().getName() + " to tail of queue");
            return true;
        } else {
            // stop here, throw RateLimiterException                        
            logger.debug("Queue full, no space for " + Thread.currentThread().getName() + ".");
            throw new RateLimiterException("Too many request queued", buildQueueFullConclusion(methodGroupName, queueMaxSize));
        }
	    
	}

}
