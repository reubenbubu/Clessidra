package com.ccbill.clessidra.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ccbill.clessidra.exception.RateLimiterException;
import com.ccbill.clessidra.interfaces.LimiterStrategy;
import com.ccbill.clessidra.tests.services.CostBasedServiceMethodAnnotated;

@ContextConfiguration(locations = { "classpath:beans.ratelimiter.test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class CostBasedLimiterTests {

	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private CostBasedServiceMethodAnnotated costBasedServiceMethodAnnotated;

	@Autowired
	@Qualifier("costBasedLimiter")
	private LimiterStrategy costBasedLimiter;

	@Test
	public void testCostBased1() {

		costBasedLimiter.flush();

		int success = 0;
		int fail = 0;

		for (int i = 0; i < 20; i++) {
			try {
				costBasedServiceMethodAnnotated.testCostBasedUngrouped();
				success++;
			} catch (RateLimiterException e) {
				fail++;
				logger.error(e.getConclusion().getDetailedExceededMessage());
			}
		}

		Assert.assertEquals(10, success);
		Assert.assertEquals(10, fail);

	}

	@Test
	public void testCostBased2() throws InterruptedException {

		costBasedLimiter.flush();

		int success = 0;
		int fail = 0;

		for (int a = 0; a < 2; a++) {
			for (int i = 0; i < 15; i++) {
				try {
					costBasedServiceMethodAnnotated.testCostBasedUngrouped();
					success++;
				} catch (RateLimiterException e) {
					fail++;
					logger.error(e.getConclusion().getDetailedExceededMessage());
				}

			}
			Thread.sleep(5001);
		}

		Assert.assertEquals(20, success);
		Assert.assertEquals(10, fail);

	}

	@Test
	public void testCostBased3() {

		costBasedLimiter.flush();

		int success = 0;
		int fail = 0;

		for (int i = 0; i < 20; i++) {
			try {
				costBasedServiceMethodAnnotated.testCostBasedGrouped(1);
				success++;
			} catch (RateLimiterException e) {
				fail++;
				logger.error(e.getConclusion().getDetailedExceededMessage());
			}

			try {
				costBasedServiceMethodAnnotated.testCostBasedGrouped("");
				success++;
			} catch (RateLimiterException e) {
				fail++;
				logger.error(e.getConclusion().getDetailedExceededMessage());
			}

		}

		Assert.assertEquals(10, success);
		Assert.assertEquals(30, fail);

	}

	@Test
	public void invocationRateTest6() throws InterruptedException {
		costBasedLimiter.flush();

		int success = 0;
		int fail = 0;

		List<Future<String>> futures = new ArrayList<Future<String>>();

		for (int i = 0; i < 20; i++) {
			futures.add(costBasedServiceMethodAnnotated.testCostBasedUngroupedAsync());
		}

		for (Future<String> current : futures) {

			try {
				current.get();
				success++;
			} catch (ExecutionException e) {
				if (e.getCause() instanceof RateLimiterException) {
					fail++;
					RateLimiterException rle = (RateLimiterException) e.getCause();
					logger.error(rle.getConclusion().getDetailedExceededMessage());
				} else {
					logger.error(e);
				}
			}

		}

		Assert.assertEquals(10, success);
		Assert.assertEquals(10, fail);

	}

}
