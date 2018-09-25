package com.ccbill.clessidra.tests;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
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
import com.ccbill.clessidra.tests.services.OverridesServiceMethodAnnotated;

@ContextConfiguration(locations = { "classpath:beans.clessidra.test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class OverridesTests {

	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	@Qualifier("overridesInvocationRateLimiter")
	private LimiterStrategy overridesInvocationRateLimiter;

	@Autowired
	@Qualifier("overridesConcurrencyLimiter")
	private LimiterStrategy overridesConcurrencyLimiter;

	@Autowired
	@Qualifier("overridesCostBasedLimiter")
	private LimiterStrategy overridesCostBasedLimiter;

	@Autowired
	private OverridesServiceMethodAnnotated overridesServiceMethodAnnotated;

	@Test
	public void testUnchainedOverrides1() {

		overridesInvocationRateLimiter.flush();

		int success = 0;
		int fail = 0;

		for (int i = 0; i < 20; i++) {
			try {
				overridesServiceMethodAnnotated.testOverridesInvocationRateUngrouped("1");
				success++;
			} catch (RateLimiterException e) {
				fail++;
				logger.error(e.getConclusion().getDetailedExceededMessage());
			}
		}

		Assert.assertEquals(10, success);
		Assert.assertEquals(10, fail);

		success = 0;
		fail = 0;

		for (int i = 0; i < 60; i++) {
			try {
				overridesServiceMethodAnnotated.testOverridesInvocationRateUngrouped("2");
				success++;
			} catch (RateLimiterException e) {
				fail++;
				logger.error(e.getConclusion().getDetailedExceededMessage());
			}
		}

		Assert.assertEquals(50, success);
		Assert.assertEquals(10, fail);

	}

	@Test
	public void testUnchainedOverrides2() {

		overridesCostBasedLimiter.flush();

		int success = 0;
		int fail = 0;

		for (int i = 0; i < 20; i++) {
			try {
				overridesServiceMethodAnnotated.testOverridesCostBasedUngrouped("1", 6);
				success++;
			} catch (RateLimiterException e) {
				fail++;
				logger.error(e.getConclusion().getDetailedExceededMessage());
			}
		}
		Assert.assertEquals(10, success);
		Assert.assertEquals(10, fail);

		success = 0;
		fail = 0;

		for (int i = 0; i < 20; i++) {
			try {
				overridesServiceMethodAnnotated.testOverridesCostBasedUngrouped("2", 12);
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
	public void testUnchainedOverrides3() throws InterruptedException {

		overridesConcurrencyLimiter.flush();

		int success = 0;
		int fail = 0;

		List<Future<String>> futures = Collections.synchronizedList(new Vector<Future<String>>());

		for (int i = 0; i < 20; i++) {
			futures.add(overridesServiceMethodAnnotated.testOverridesConcurrencyUngrouped("1"));
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

		Assert.assertEquals(5, success);
		Assert.assertEquals(15, fail);

		success = 0;
		fail = 0;

		futures.clear();

		for (int i = 0; i < 20; i++) {
			futures.add(overridesServiceMethodAnnotated.testOverridesConcurrencyUngrouped("2"));
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

		Assert.assertEquals(15, success);
		Assert.assertEquals(5, fail);

	}

}
