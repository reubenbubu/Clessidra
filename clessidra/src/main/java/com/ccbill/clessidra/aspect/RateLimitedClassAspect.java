package com.ccbill.clessidra.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.ccbill.clessidra.annotations.RateLimited;

/**
 * {@link RateLimitedClassAspect} takes care of intercepting the
 * {@link RateLimited} annotation at class level. The core logic can be found in
 * {@link BaseRateLimiter}.
 * 
 * @author reubena
 * 
 */
@Aspect
@Component
public class RateLimitedClassAspect extends BaseRateLimiter {

	/**
	 * Forward the intercept to {@link BaseRateLimiter}
	 */
	@Around("within(@com.ccbill.clessidra.annotations.RateLimited *)")
	public Object intercept(ProceedingJoinPoint joinPoint) throws Throwable {
		return super.intercept(joinPoint);
	}

}
