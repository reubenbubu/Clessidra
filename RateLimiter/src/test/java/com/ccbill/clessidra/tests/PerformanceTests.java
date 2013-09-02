package com.ccbill.clessidra.tests;

import java.util.Date;
import java.util.Enumeration;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ccbill.clessidra.aspect.BaseRateLimiter;
import com.ccbill.clessidra.interfaces.LimiterStrategy;
import com.ccbill.clessidra.strategy.BaseInvocationRateLimiterStrategy;
import com.ccbill.clessidra.tests.services.OverridesServiceMethodAnnotated;

@ContextConfiguration(locations = { "classpath:beans.ratelimiter.test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class PerformanceTests {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("overridesInvocationRateLimiter")
	private LimiterStrategy invocationRateLimiter;
	
	@Autowired
	private OverridesServiceMethodAnnotated overridesServiceMethodAnnotated;
	
	@Test
	public void invocationRatePerformanceTest() {

		Enumeration loggers = Logger.getRootLogger().getLoggerRepository().getCurrentLoggers();
		
		
		// turn off all the loggers not to hinder performance of test 
		while (loggers.hasMoreElements()) {
			((Logger)loggers.nextElement()).setLevel(Level.OFF);
		}
		
		invocationRateLimiter.flush();
		
		Date date = new Date();
		
		long durationMillis = 5000;
		
		int counter = 0;
		
		while (new Date().before(new Date(date.getTime() + durationMillis))) {
			overridesServiceMethodAnnotated.testOverridesInvocationRateUngrouped("5");
			counter++;
		}

		loggers = Logger.getRootLogger().getLoggerRepository().getCurrentLoggers();
		
		while (loggers.hasMoreElements()) {
			((Logger)loggers.nextElement()).setLevel(Level.DEBUG);
		}
		
		logger.info(counter + " invocations in " + durationMillis + " ms. ");
		logger.info((int)((counter / (double)durationMillis) * 1000) + " invocations per second.");
		
	}

}
