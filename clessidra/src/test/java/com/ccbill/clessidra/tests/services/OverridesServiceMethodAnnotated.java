package com.ccbill.clessidra.tests.services;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.ccbill.clessidra.annotations.RateLimited;
import com.ccbill.clessidra.enums.MethodGrouping;

@Component
public class OverridesServiceMethodAnnotated {

	private Logger logger = Logger.getLogger(this.getClass());

	@RateLimited(methodGrouping = MethodGrouping.UNGROUPED, limiterBean = "overridesInvocationRateLimiter")
	public void testOverridesInvocationRateUngrouped(String s) {

	}

	@RateLimited(methodGrouping = MethodGrouping.UNGROUPED, limiterBean = "overridesCostBasedLimiter")
	public void testOverridesCostBasedUngrouped(String s, Integer i) {

	}

	@Async
	@RateLimited(methodGrouping = MethodGrouping.UNGROUPED, limiterBean = "overridesConcurrencyLimiter")
	public Future<String> testOverridesConcurrencyUngrouped(String s) {
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
