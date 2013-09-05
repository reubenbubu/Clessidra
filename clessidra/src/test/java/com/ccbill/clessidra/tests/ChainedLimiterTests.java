package com.ccbill.clessidra.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ccbill.clessidra.exception.RateLimiterException;
import com.ccbill.clessidra.interfaces.LimiterStrategy;
import com.ccbill.clessidra.tests.customstrategies.TestConcurrencyLimiterStrategy;
import com.ccbill.clessidra.tests.customstrategies.TestCostBasedLimiterStrategy;
import com.ccbill.clessidra.tests.customstrategies.TestInvocationRateLimiterStrategy;
import com.ccbill.clessidra.tests.services.ChainedServiceMethodAnnotated;

@ContextConfiguration(locations = { "classpath:beans.clessidra.test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class ChainedLimiterTests {

	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private ChainedServiceMethodAnnotated chainedServiceMethodAnnotated;

	@Autowired
	@Qualifier("chainedTestLimiterChain")
	private LimiterStrategy chainedTestLimiterChain;

	@Test
	public void testChained1() throws InterruptedException {
		flushChain();

		int success = 0;
		int fail = 0;

		List<Class<? extends LimiterStrategy>> limitingResponsibleStrategies = new ArrayList<Class<? extends LimiterStrategy>>();

		List<Future<String>> futures = new ArrayList<Future<String>>();

		for (int i = 0; i < 20; i++) {
			futures.add(chainedServiceMethodAnnotated.testChainedUngrouped("1", 1, 150));
			Thread.sleep(100);
		}

		for (Future<String> current : futures) {
			try {
				current.get();
				success++;
			} catch (ExecutionException e) {

				if (e.getCause() instanceof RateLimiterException) {
					fail++;
					RateLimiterException rle = (RateLimiterException) e.getCause();
					limitingResponsibleStrategies.add(rle.getConclusion().getStrategyResponsible().getClass());
					logger.error(rle.getConclusion().getDetailedExceededMessage());
				} else {
					logger.error(e);
				}

			}
		}

		Assert.assertEquals(10, success);
		Assert.assertEquals(10, fail);

		Class<? extends LimiterStrategy> expectedResponsibleStrategy = TestInvocationRateLimiterStrategy.class;
		for (Class<? extends LimiterStrategy> current : limitingResponsibleStrategies) {
			Assert.assertEquals(expectedResponsibleStrategy, current);
		}

	}

	@Test
	public void testChained2() throws InterruptedException {
		flushChain();

		int success = 0;
		int fail = 0;

		List<Class<? extends LimiterStrategy>> limitingResponsibleStrategies = new ArrayList<Class<? extends LimiterStrategy>>();

		List<Future<String>> futures = new ArrayList<Future<String>>();

		for (int i = 0; i < 20; i++) {
			futures.add(chainedServiceMethodAnnotated.testChainedUngrouped("1", 1, 13000));
			Thread.sleep(600);
		}

		for (Future<String> current : futures) {
			try {
				current.get();
				success++;
			} catch (ExecutionException e) {

				if (e.getCause() instanceof RateLimiterException) {
					fail++;
					RateLimiterException rle = (RateLimiterException) e.getCause();
					limitingResponsibleStrategies.add(rle.getConclusion().getStrategyResponsible().getClass());
					logger.error(rle.getConclusion().getDetailedExceededMessage());
				} else {
					logger.error(e);
				}

			}
		}

		Assert.assertEquals(10, success);
		Assert.assertEquals(10, fail);

		Class<? extends LimiterStrategy> expectedResponsibleStrategy = TestConcurrencyLimiterStrategy.class;

		for (Class<? extends LimiterStrategy> current : limitingResponsibleStrategies) {
			Assert.assertEquals(expectedResponsibleStrategy, current);
		}

	}

	@Test
	public void testChained3() throws InterruptedException {
		flushChain();

		int success = 0;
		int fail = 0;

		List<Class<? extends LimiterStrategy>> limitingResponsibleStrategies = new ArrayList<Class<? extends LimiterStrategy>>();

		List<Future<String>> futures = new ArrayList<Future<String>>();

		for (int i = 0; i < 20; i++) {
			futures.add(chainedServiceMethodAnnotated.testChainedUngrouped("1", 7, 150));
			Thread.sleep(100);
		}

		for (Future<String> current : futures) {
			try {
				current.get();
				success++;
			} catch (ExecutionException e) {

				if (e.getCause() instanceof RateLimiterException) {
					fail++;
					RateLimiterException rle = (RateLimiterException) e.getCause();
					limitingResponsibleStrategies.add(rle.getConclusion().getStrategyResponsible().getClass());
					logger.error(rle.getConclusion().getDetailedExceededMessage());
				} else {
					logger.error(e);
				}

			}
		}

		Assert.assertEquals(9, success);
		Assert.assertEquals(11, fail);

		Class<? extends LimiterStrategy> expectedResponsibleStrategy = TestCostBasedLimiterStrategy.class;
		for (Class<? extends LimiterStrategy> current : limitingResponsibleStrategies) {
			Assert.assertEquals(expectedResponsibleStrategy, current);
		}

	}

	@Test
	public void testChained4() throws InterruptedException {
		flushChain();

		int success = 0;
		int fail = 0;

		List<Class<? extends LimiterStrategy>> limitingResponsibleStrategies = new ArrayList<Class<? extends LimiterStrategy>>();

		List<Future<String>> futures = new ArrayList<Future<String>>();

		for (int i = 0; i < 10; i++) {
			futures.add(chainedServiceMethodAnnotated.testChainedGrouped1("1", 1, 150));
			Thread.sleep(100);
			futures.add(chainedServiceMethodAnnotated.testChainedGrouped2("1", 1, 150));
			Thread.sleep(100);
		}

		for (Future<String> current : futures) {
			try {
				current.get();
				success++;
			} catch (ExecutionException e) {

				if (e.getCause() instanceof RateLimiterException) {
					fail++;
					RateLimiterException rle = (RateLimiterException) e.getCause();
					limitingResponsibleStrategies.add(rle.getConclusion().getStrategyResponsible().getClass());
					logger.error(rle.getConclusion().getDetailedExceededMessage());
				} else {
					logger.error(e);
				}

			}
		}

		Assert.assertEquals(10, success);
		Assert.assertEquals(10, fail);

		Class<? extends LimiterStrategy> expectedResponsibleStrategy = TestInvocationRateLimiterStrategy.class;
		for (Class<? extends LimiterStrategy> current : limitingResponsibleStrategies) {
			Assert.assertEquals(expectedResponsibleStrategy, current);
		}

	}

	@Test
	public void testChained5() throws InterruptedException {
		flushChain();

		int success = 0;
		int fail = 0;

		List<Class<? extends LimiterStrategy>> limitingResponsibleStrategies = new ArrayList<Class<? extends LimiterStrategy>>();

		List<Future<String>> futures = new ArrayList<Future<String>>();

		for (int i = 0; i < 10; i++) {
			futures.add(chainedServiceMethodAnnotated.testChainedGrouped1("1", 1, 13000));
			Thread.sleep(600);
			futures.add(chainedServiceMethodAnnotated.testChainedGrouped2("1", 1, 13000));
			Thread.sleep(600);

		}

		for (Future<String> current : futures) {
			try {
				current.get();
				success++;
			} catch (ExecutionException e) {

				if (e.getCause() instanceof RateLimiterException) {
					fail++;
					RateLimiterException rle = (RateLimiterException) e.getCause();
					limitingResponsibleStrategies.add(rle.getConclusion().getStrategyResponsible().getClass());
					logger.error(rle.getConclusion().getDetailedExceededMessage());
				} else {
					logger.error(e);
				}

			}
		}

		Assert.assertEquals(10, success);
		Assert.assertEquals(10, fail);

		Class<? extends LimiterStrategy> expectedResponsibleStrategy = TestConcurrencyLimiterStrategy.class;

		for (Class<? extends LimiterStrategy> current : limitingResponsibleStrategies) {
			Assert.assertEquals(expectedResponsibleStrategy, current);
		}

	}

	@Test
	public void testChained6() throws InterruptedException {
		flushChain();

		int success = 0;
		int fail = 0;

		List<Class<? extends LimiterStrategy>> limitingResponsibleStrategies = new ArrayList<Class<? extends LimiterStrategy>>();

		List<Future<String>> futures = new ArrayList<Future<String>>();

		for (int i = 0; i < 10; i++) {
			futures.add(chainedServiceMethodAnnotated.testChainedGrouped1("1", 7, 150));
			Thread.sleep(100);
			futures.add(chainedServiceMethodAnnotated.testChainedGrouped2("1", 7, 150));
			Thread.sleep(100);

		}

		for (Future<String> current : futures) {
			try {
				current.get();
				success++;
			} catch (ExecutionException e) {

				if (e.getCause() instanceof RateLimiterException) {
					fail++;
					RateLimiterException rle = (RateLimiterException) e.getCause();
					limitingResponsibleStrategies.add(rle.getConclusion().getStrategyResponsible().getClass());
					logger.error(rle.getConclusion().getDetailedExceededMessage());
				} else {
					logger.error(e);
				}

			}
		}

		Assert.assertEquals(9, success);
		Assert.assertEquals(11, fail);

		Class<? extends LimiterStrategy> expectedResponsibleStrategy = TestCostBasedLimiterStrategy.class;
		for (Class<? extends LimiterStrategy> current : limitingResponsibleStrategies) {
			Assert.assertEquals(expectedResponsibleStrategy, current);
		}

	}

	@Test
	public void testChained7() throws InterruptedException {
		flushChain();

		int success = 0;
		int fail = 0;

		List<Class<? extends LimiterStrategy>> limitingResponsibleStrategies = new ArrayList<Class<? extends LimiterStrategy>>();

		List<Future<String>> futures = new ArrayList<Future<String>>();

		for (int i = 0; i < 20; i++) {
			futures.add(chainedServiceMethodAnnotated.testChainedGrouped1("1", 1, 150));
			futures.add(chainedServiceMethodAnnotated.testChainedGrouped2("2", 1, 150));
			Thread.sleep(100);
		}

		for (Future<String> current : futures) {
			try {
				current.get();
				success++;
			} catch (ExecutionException e) {

				if (e.getCause() instanceof RateLimiterException) {
					fail++;
					RateLimiterException rle = (RateLimiterException) e.getCause();
					limitingResponsibleStrategies.add(rle.getConclusion().getStrategyResponsible().getClass());
					logger.error(rle.getConclusion().getDetailedExceededMessage());
				} else {
					logger.error(e);
				}

			}
		}

		Assert.assertEquals(20, success);
		Assert.assertEquals(20, fail);

		Class<? extends LimiterStrategy> expectedResponsibleStrategy = TestInvocationRateLimiterStrategy.class;
		for (Class<? extends LimiterStrategy> current : limitingResponsibleStrategies) {
			Assert.assertEquals(expectedResponsibleStrategy, current);
		}

	}

	@Test
	public void testChained8() throws InterruptedException {
		flushChain();

		int success = 0;
		int fail = 0;

		List<Class<? extends LimiterStrategy>> limitingResponsibleStrategies = new ArrayList<Class<? extends LimiterStrategy>>();

		List<Future<String>> futures = new ArrayList<Future<String>>();

		for (int i = 0; i < 20; i++) {
			futures.add(chainedServiceMethodAnnotated.testChainedGrouped1("1", 1, 13000));
			futures.add(chainedServiceMethodAnnotated.testChainedGrouped2("2", 1, 13000));
			Thread.sleep(600);

		}

		for (Future<String> current : futures) {
			try {
				current.get();
				success++;
			} catch (ExecutionException e) {

				if (e.getCause() instanceof RateLimiterException) {
					fail++;
					RateLimiterException rle = (RateLimiterException) e.getCause();
					limitingResponsibleStrategies.add(rle.getConclusion().getStrategyResponsible().getClass());
					logger.error(rle.getConclusion().getDetailedExceededMessage());
				} else {
					logger.error(e);
				}

			}
		}

		Assert.assertEquals(20, success);
		Assert.assertEquals(20, fail);

		Class<? extends LimiterStrategy> expectedResponsibleStrategy = TestConcurrencyLimiterStrategy.class;

		for (Class<? extends LimiterStrategy> current : limitingResponsibleStrategies) {
			Assert.assertEquals(expectedResponsibleStrategy, current);
		}

	}

	@Test
	public void testChained9() throws InterruptedException {
		flushChain();

		int success = 0;
		int fail = 0;

		List<Class<? extends LimiterStrategy>> limitingResponsibleStrategies = new ArrayList<Class<? extends LimiterStrategy>>();

		List<Future<String>> futures = new ArrayList<Future<String>>();

		for (int i = 0; i < 20; i++) {
			futures.add(chainedServiceMethodAnnotated.testChainedGrouped1("1", 7, 150));
			futures.add(chainedServiceMethodAnnotated.testChainedGrouped2("2", 7, 150));
			Thread.sleep(100);

		}

		for (Future<String> current : futures) {
			try {
				current.get();
				success++;
			} catch (ExecutionException e) {

				if (e.getCause() instanceof RateLimiterException) {
					fail++;
					RateLimiterException rle = (RateLimiterException) e.getCause();
					limitingResponsibleStrategies.add(rle.getConclusion().getStrategyResponsible().getClass());
					logger.error(rle.getConclusion().getDetailedExceededMessage());
				} else {
					logger.error(e);
				}

			}
		}

		Assert.assertEquals(18, success);
		Assert.assertEquals(22, fail);

		Class<? extends LimiterStrategy> expectedResponsibleStrategy = TestCostBasedLimiterStrategy.class;
		for (Class<? extends LimiterStrategy> current : limitingResponsibleStrategies) {
			Assert.assertEquals(expectedResponsibleStrategy, current);
		}

	}

	private void flushChain() {

		LimiterStrategy limiterStrategy = chainedTestLimiterChain;

		while (limiterStrategy != null) {
			limiterStrategy.flush();
			limiterStrategy = limiterStrategy.getNextLimiterStrategy();
		}

	}

}
