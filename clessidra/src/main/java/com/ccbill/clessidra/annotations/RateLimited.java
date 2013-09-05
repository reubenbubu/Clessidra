package com.ccbill.clessidra.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccbill.clessidra.enums.MethodGrouping;

/**
 * This annotation is used to mark methods or classes as candidates for rate limiting.
 * 
 * @author reubena
 * 
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {

	/**
	 * Set the {@link MethodGrouping} to combine or isolate methods in the same limiter strategy.
	 */
	MethodGrouping methodGrouping() default MethodGrouping.UNGROUPED;

	/**
	 * Set a groupName to control which methods will be grouped together. All the methods sharing the same group name will share the same limiter
	 * strategy.
	 */
	String groupName() default "";

	/**
	 * The name of the spring bean that holds the limiter strategy or chain of limiter strategies.
	 */
	String limiterBean() default "defaultLimiter";

}
