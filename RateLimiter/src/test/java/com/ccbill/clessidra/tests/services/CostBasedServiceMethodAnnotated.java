package com.ccbill.clessidra.tests.services;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.ccbill.clessidra.annotations.RateLimited;
import com.ccbill.clessidra.enums.MethodGrouping;

@Ignore
@Component
public class CostBasedServiceMethodAnnotated {

	private Logger logger = Logger.getLogger(this.getClass());

	@RateLimited(methodGrouping = MethodGrouping.UNGROUPED, limiterBean = "costBasedLimiter")
	public void testCostBasedUngrouped() {

	}

	@RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "costBasedLimiterTest", limiterBean = "costBasedLimiter")
	public void testCostBasedGrouped(int i) {

	}

	@RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "costBasedLimiterTest", limiterBean = "costBasedLimiter")
	public void testCostBasedGrouped(String s) {

	}

	@Async
	@RateLimited(methodGrouping = MethodGrouping.UNGROUPED, limiterBean = "costBasedLimiter")
	public Future<String> testCostBasedUngroupedAsync() {
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
