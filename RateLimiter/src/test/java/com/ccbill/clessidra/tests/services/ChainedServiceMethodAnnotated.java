package com.ccbill.clessidra.tests.services;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.ccbill.clessidra.annotations.RateLimited;
import com.ccbill.clessidra.enums.MethodGrouping;

@Component
public class ChainedServiceMethodAnnotated {

	private Logger logger = Logger.getLogger(this.getClass());

	@Async
	@RateLimited(methodGrouping = MethodGrouping.UNGROUPED, limiterBean = "chainedTestLimiterChain")
	public Future<String> testChainedUngrouped(String strategyGroup, Integer cost, long sleepTime) {
		try {
			logger.debug(">> " + Thread.currentThread().getName() + " sleeping for " + sleepTime + " ms.");
			Thread.sleep(sleepTime);
			logger.debug(Thread.currentThread().getName() + " resuming...");
			return new AsyncResult<String>("OK " + strategyGroup + " " + cost + " " + sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Async
	@RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "chainedTest", limiterBean = "chainedTestLimiterChain")
	public Future<String> testChainedGrouped1(String strategyGroup, Integer cost, long sleepTime) {
		try {
			logger.debug(">> " + Thread.currentThread().getName() + " sleeping for " + sleepTime + " ms.");
			Thread.sleep(sleepTime);
			logger.debug(Thread.currentThread().getName() + " resuming...");
			return new AsyncResult<String>("OK " + strategyGroup + " " + cost + " " + sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Async
	@RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "chainedTest", limiterBean = "chainedTestLimiterChain")
	public Future<String> testChainedGrouped2(String strategyGroup, Integer cost, long sleepTime) {
		try {
			logger.debug(">> " + Thread.currentThread().getName() + " sleeping for " + sleepTime + " ms.");
			Thread.sleep(sleepTime);
			logger.debug(Thread.currentThread().getName() + " resuming...");
			return new AsyncResult<String>("OK " + strategyGroup + " " + cost + " " + sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
