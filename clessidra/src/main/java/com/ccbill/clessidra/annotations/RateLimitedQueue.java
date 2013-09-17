package com.ccbill.clessidra.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccbill.clessidra.enums.MethodGrouping;

/**
 * This presence of this on a method or class annotation allows concurrent requests that are also
 * marked with {@link RateLimited} to be queued instead of discarded immediately 
 * when they exceed the limit allowed. 
 * 
 * @author reubena
 * 
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimitedQueue {

    /**
     * The maximum number of queued requests
     */
    int queueSize();
    
    
}
