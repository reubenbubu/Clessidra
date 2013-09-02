package com.ccbill.clessidra.tests.services;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.ccbill.clessidra.annotations.RateLimited;
import com.ccbill.clessidra.enums.MethodGrouping;

@Ignore
@Service
public class InvocationRateServiceMethodAnnotated {

	private Logger logger = Logger.getLogger(this.getClass());

	@RateLimited(methodGrouping = MethodGrouping.UNGROUPED, limiterBean = "invocationRateLimiter")
	public void testInvocationRateUngrouped() {

	}

	@RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "invocationRateLimiterTest", limiterBean = "invocationRateLimiter")
	public void testInvocationRateGrouped(int i) {

	}

	@RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "invocationRateLimiterTest", limiterBean = "invocationRateLimiter")
	public void testInvocationRateGrouped(String s) {

	}

	@Async
	@RateLimited(methodGrouping = MethodGrouping.UNGROUPED, limiterBean = "invocationRateLimiter")
	public Future<String> testInvocationRateUngroupedAsync() {
		try {
			logger.debug(">> " + Thread.currentThread().getName() + " sleeping for 5 seconds");
			Thread.sleep(5000);
			logger.debug(Thread.currentThread().getName() + " resuming...");
			return new AsyncResult<String>("OK ");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
