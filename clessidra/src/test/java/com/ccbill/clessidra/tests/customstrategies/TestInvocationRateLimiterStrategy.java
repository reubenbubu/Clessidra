package com.ccbill.clessidra.tests.customstrategies;

import org.junit.Ignore;

import com.ccbill.clessidra.strategy.BaseInvocationRateLimiterStrategy;

@Ignore
public class TestInvocationRateLimiterStrategy extends BaseInvocationRateLimiterStrategy {

	public TestInvocationRateLimiterStrategy(int numberOfInvocationsLimit, int perTimePeriodSeconds) {
		super(numberOfInvocationsLimit, perTimePeriodSeconds);
	}

	@Override
	public String getStrategyGroupKey(Object[] args) {

		if (args != null && args.length > 0) {
			return args[0].toString();
		} else {
			return "";
		}

	}

}
