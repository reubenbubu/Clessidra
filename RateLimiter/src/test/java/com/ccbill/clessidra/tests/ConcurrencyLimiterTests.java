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
import com.ccbill.clessidra.tests.services.ConcurrencyServiceMethodAnnotated;

@ContextConfiguration(locations = { "classpath:beans.ratelimiter.test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class ConcurrencyLimiterTests {

	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private ConcurrencyServiceMethodAnnotated concurrencyServiceMethodAnnotatedAsyncLauncher;

	@Autowired
	@Qualifier("concurrencyLimiter")
	private LimiterStrategy concurrencyLimiter;

	@Test
	public void concurrencyTest1() throws InterruptedException {

		concurrencyLimiter.flush();

		int success = 0;
		int fail = 0;

		List<Future<String>> futures = Collections.synchronizedList(new Vector<Future<String>>());

		for (int i = 0; i < 12; i++) {
			futures.add(concurrencyServiceMethodAnnotatedAsyncLauncher.testConcurrencyUngrouped());
			Thread.sleep(10);
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
		Assert.assertEquals(7, fail);

	}

	@Test
	public void concurrencyTest2() throws InterruptedException {

		concurrencyLimiter.flush();

		int success = 0;
		int fail = 0;

		List<Future<String>> futures = Collections.synchronizedList(new Vector<Future<String>>());

		for (int i = 0; i < 10; i++) {
			futures.add(concurrencyServiceMethodAnnotatedAsyncLauncher.testConcurrencyGrouped(1));
			futures.add(concurrencyServiceMethodAnnotatedAsyncLauncher.testConcurrencyGrouped(""));
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

	}

	@Test
	public void concurrencyTest3() throws InterruptedException {

		concurrencyLimiter.flush();

		int success = 0;
		int fail = 0;

		List<Future<String>> futures = Collections.synchronizedList(new Vector<Future<String>>());

		for (int i = 0; i < 10; i++) {
			futures.add(concurrencyServiceMethodAnnotatedAsyncLauncher.testConcurrencyUngrouped());
			futures.add(concurrencyServiceMethodAnnotatedAsyncLauncher.testConcurrencyGrouped(1));
			futures.add(concurrencyServiceMethodAnnotatedAsyncLauncher.testConcurrencyGrouped(""));
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
		Assert.assertEquals(20, fail);

	}

}
