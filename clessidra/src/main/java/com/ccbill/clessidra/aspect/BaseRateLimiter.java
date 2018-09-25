package com.ccbill.clessidra.aspect;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ccbill.clessidra.annotations.RateLimited;
import com.ccbill.clessidra.exception.RateLimiterException;
import com.ccbill.clessidra.strategy.AbstractLimiterStrategy;
import com.ccbill.clessidra.strategy.LimiterStrategyConclusion;

/**
 * 
 * {@link BaseRateLimiter} holds the core logic of when a method call is
 * intercepted for rate limiting.
 * 
 * @author reubena
 * 
 */
public class BaseRateLimiter implements ApplicationContextAware {

	private Logger logger = Logger.getLogger(BaseRateLimiter.class);

	private ApplicationContext applicationContext;

	public BaseRateLimiter() {
	}

	/**
	 * The core Rate Limiter logic. This method will call the limiter strategy
	 * chain and decide whether to annotated method will run or not.
	 * 
	 * @param joinPoint
	 * @return The object returned by the annotated method
	 * @throws Throwable
	 */
	@SuppressWarnings("unchecked")
	public Object intercept(ProceedingJoinPoint joinPoint) throws Throwable {

		// extract MethodSignature
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		logger.debug("Intercepted a method call on " + methodSignature.toString());

		// find the RateLimited annotation, method gets priority over class
		RateLimited annotation = methodSignature.getMethod().getAnnotation(RateLimited.class);
		if (annotation == null) {
			annotation = (RateLimited) joinPoint.getSourceLocation().getWithinType().getAnnotation(RateLimited.class);
		}

		// resolve method group name based on the methodGrouping in the
		// annotation
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

		// get the limiter strategy chain from the spring context
		AbstractLimiterStrategy limiterStrategyChain = (AbstractLimiterStrategy) applicationContext
				.getBean(annotation.limiterBean());

		// generate a uuid to be used later on if rollback is required
		UUID methodInvocationUUID = UUIDGenerator.generateUUID();

		// run through the limiter strategy chain and get the conclusion
		LimiterStrategyConclusion conclusion = limiterStrategyChain.hasLimitBeenExceededChain(methodGroupName,
				methodInvocationUUID, joinPoint.getArgs());

		// if limit is not exceeded call the method and do a post invocation
		// clean up where needed
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
				limiterStrategyChain.postInvocationCleanupChain(methodGroupName, methodInvocationUUID,
						joinPoint.getArgs());
			}
		}

		// throw an exception containing the conclusion
		throw new RateLimiterException("Method call blocked.", conclusion);
	}

	/**
	 * Spring will automagically set the application context using this method
	 * thanks to {@link ApplicationContextAware}
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
