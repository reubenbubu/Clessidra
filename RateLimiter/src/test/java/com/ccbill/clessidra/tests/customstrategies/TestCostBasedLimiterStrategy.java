package com.ccbill.clessidra.tests.customstrategies;

import org.junit.Ignore;

import com.ccbill.clessidra.strategy.BaseCostBasedLimiterStrategy;

@Ignore
public class TestCostBasedLimiterStrategy extends BaseCostBasedLimiterStrategy {

	public TestCostBasedLimiterStrategy(int costLimit, int perTimePeriodSeconds) {
		super(costLimit, perTimePeriodSeconds);
	}

	@Override
	public String getStrategyGroupKey(Object[] args) {
		return args[0].toString();
	}

	@Override
	public Integer calculateCost(Object[] args) {
		return (Integer) args[1];
	}

}
