package com.ccbill.clessidra.tests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ccbill.clessidra.exception.RateLimiterException;
import com.ccbill.clessidra.interfaces.LimiterStrategy;
import com.ccbill.clessidra.tests.services.InvocationRateServiceClassAnnotated;
import com.ccbill.clessidra.tests.services.InvocationRateServiceMethodAnnotated;

@ContextConfiguration(locations = { "classpath:beans.ratelimiter.test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@Component
public class InvocationRateLimiterTests {

	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private InvocationRateServiceMethodAnnotated invocationRateServiceMethodAnnotated;

	@Autowired
	private InvocationRateServiceClassAnnotated invocationRateServiceClassAnnotated;

	@Autowired
	@Qualifier("invocationRateLimiter")
	private LimiterStrategy invocationRateLimiter;

	@Test
	public void invocationRateTest1() {

		invocationRateLimiter.flush();

		int success = 0;
		int fail = 0;

		for (int i = 0; i < 20; i++) {
			try {
				invocationRateServiceMethodAnnotated.testInvocationRateUngrouped();
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
	public void invocationRateTest2() throws InterruptedException {

		invocationRateLimiter.flush();

		int success = 0;
		int fail = 0;

		for (int a = 0; a < 2; a++) {
			for (int i = 0; i < 15; i++) {
				try {
					invocationRateServiceMethodAnnotated.testInvocationRateUngrouped();
					success++;
				} catch (RateLimiterException e) {
					logger.error(e.getConclusion().getDetailedExceededMessage());
					fail++;
				}

			}
			Thread.sleep(5001);
		}

		Assert.assertEquals(20, success);
		Assert.assertEquals(10, fail);
	}

	@Test
	public void invocationRateTest3() {

		invocationRateLimiter.flush();

		int success = 0;
		int fail = 0;

		for (int i = 0; i < 20; i++) {
			try {
				invocationRateServiceMethodAnnotated.testInvocationRateGrouped(1);
				success++;
			} catch (RateLimiterException e) {
				logger.error(e.getConclusion().getDetailedExceededMessage());
				fail++;
			}

			try {
				invocationRateServiceMethodAnnotated.testInvocationRateGrouped("");
				success++;
			} catch (RateLimiterException e) {
				logger.error(e.getConclusion().getDetailedExceededMessage());
				fail++;
			}

		}

		Assert.assertEquals(10, success);
		Assert.assertEquals(30, fail);
	}

	@Test
	public void invocationRateTest4() {

		invocationRateLimiter.flush();

		int success = 0;
		int fail = 0;

		for (int i = 0; i < 20; i++) {
			try {
				invocationRateServiceClassAnnotated.testInvocationRateGrouped(1);
				success++;
			} catch (RateLimiterException e) {
				logger.error(e.getConclusion().getDetailedExceededMessage());
				fail++;
			}
		}

		Assert.assertEquals(10, success);
		Assert.assertEquals(10, fail);

	}

	@Test
	public void invocationRateTest5() {

		invocationRateLimiter.flush();

		int success = 0;
		int fail = 0;

		for (int i = 0; i < 10; i++) {
			try {
				invocationRateServiceClassAnnotated.testInvocationRateGrouped(1);
				success++;
			} catch (RateLimiterException e) {
				logger.error(e.getConclusion().getDetailedExceededMessage());
				fail++;

			}

			try {
				invocationRateServiceClassAnnotated.testInvocationRateGrouped("");
				success++;
			} catch (RateLimiterException e) {
				logger.error(e.getConclusion().getDetailedExceededMessage());
				fail++;
			}

			try {
				invocationRateServiceClassAnnotated.testInvocationRateGrouped(new Date());
				success++;
			} catch (RateLimiterException e) {
				logger.error(e.getConclusion().getDetailedExceededMessage());
				fail++;
			}

		}

		Assert.assertEquals(10, success);
		Assert.assertEquals(20, fail);

	}

	@Test
	public void invocationRateTest6() throws InterruptedException {
		invocationRateLimiter.flush();

		int success = 0;
		int fail = 0;

		List<Future<String>> futures = new ArrayList<Future<String>>();

		for (int i = 0; i < 20; i++) {
			futures.add(invocationRateServiceMethodAnnotated.testInvocationRateUngroupedAsync());
		}

		for (Future<String> current : futures) {

			try {
				current.get();
				success++;
			} catch (ExecutionException e) {
				if (e.getCause() instanceof RateLimiterException) {
					fail++;
				} else {
					logger.error(e);
				}
			}

		}

		Assert.assertEquals(10, success);
		Assert.assertEquals(10, fail);

	}

}
