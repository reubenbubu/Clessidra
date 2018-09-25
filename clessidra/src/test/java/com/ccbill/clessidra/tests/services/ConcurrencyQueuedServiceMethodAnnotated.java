package com.ccbill.clessidra.tests.services;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.ccbill.clessidra.annotations.RateLimited;
import com.ccbill.clessidra.annotations.RateLimitedQueue;
import com.ccbill.clessidra.enums.MethodGrouping;

@Ignore
@Component
public class ConcurrencyQueuedServiceMethodAnnotated {

	private Logger logger = Logger.getLogger(this.getClass());

	@Async
	@RateLimited(methodGrouping = MethodGrouping.UNGROUPED, limiterBean = "concurrencyQueuedLimiter")
	@RateLimitedQueue(maxQueueSize = 100)
	public Future<String> testConcurrencyUngrouped() {

		try {
			logger.debug(">> " + Thread.currentThread().getName() + " sleeping for 5 seconds");
			Thread.sleep(5000);
			logger.debug(Thread.currentThread().getName() + " resuming...");

			return new AsyncResult<String>("OK");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Async
    @RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "concurrencyQueuedLimiterTest", limiterBean = "concurrencyQueuedLimiter")
    public Future<String> testConcurrencyGrouped(int i) {
        try {
            logger.debug(">> " + Thread.currentThread().getName() + " sleeping for 5 seconds");
            Thread.sleep(5000);
            logger.debug(Thread.currentThread().getName() + " resuming...");
            return new AsyncResult<String>("OK " + i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Async
    @RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "concurrencyQueuedLimiterTest", limiterBean = "concurrencyQueuedLimiter")
    public Future<String> testConcurrencyGrouped(String s) {
        try {
            logger.debug(">> " + Thread.currentThread().getName() + " sleeping for 5 seconds");
            Thread.sleep(5000);
            logger.debug(Thread.currentThread().getName() + " resuming...");
            return new AsyncResult<String>("OK " + s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
	

}
