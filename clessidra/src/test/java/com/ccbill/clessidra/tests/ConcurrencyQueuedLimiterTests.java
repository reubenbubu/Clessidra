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
import com.ccbill.clessidra.tests.services.ConcurrencyQueuedServiceMethodAnnotated;
import com.ccbill.clessidra.tests.services.ConcurrencyServiceMethodAnnotated;

@ContextConfiguration(locations = { "classpath:beans.clessidra.test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class ConcurrencyQueuedLimiterTests {

	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private ConcurrencyQueuedServiceMethodAnnotated concurrencyQueuedServiceMethodAnnotated;

	@Autowired
	@Qualifier("concurrencyLimiter")
	private LimiterStrategy concurrencyLimiter;

	@Test
	public void concurrencyTest1() throws InterruptedException {

		concurrencyLimiter.flush();

		int success = 0;
		int fail = 0;

		List<Future<String>> futures = Collections.synchronizedList(new Vector<Future<String>>());

		for (int i = 0; i < 200; i++) {
			futures.add(concurrencyQueuedServiceMethodAnnotated.testConcurrencyUngrouped());
		}

		for (Future<String> current : futures) {
			try {
				current.get();
				success++;
			} catch (ExecutionException e) {
				if (e.getCause() instanceof RateLimiterException) {
					fail++;
					RateLimiterException rle = (RateLimiterException) e.getCause();
					logger.error(rle);
				} else {
					logger.error(e);
				}
			}

		}
		
		futures.clear();
		
		for (int i = 0; i < 200; i++) {
			futures.add(concurrencyQueuedServiceMethodAnnotated.testConcurrencyUngrouped());
		}

		for (Future<String> current : futures) {
			try {
				current.get();
				success++;
			} catch (ExecutionException e) {
				if (e.getCause() instanceof RateLimiterException) {
					fail++;
					RateLimiterException rle = (RateLimiterException) e.getCause();
					logger.error(rle);
				} else {
					logger.error(e);
				}
			}

		}

		Assert.assertEquals(240, success);
		Assert.assertEquals(160, fail);

	}


}
