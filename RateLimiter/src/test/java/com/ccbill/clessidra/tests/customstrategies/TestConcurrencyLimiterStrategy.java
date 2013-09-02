package com.ccbill.clessidra.tests.customstrategies;

import org.junit.Ignore;

import com.ccbill.clessidra.strategy.BaseConcurrencyLimiterStrategy;

@Ignore
public class TestConcurrencyLimiterStrategy extends BaseConcurrencyLimiterStrategy {

	public TestConcurrencyLimiterStrategy(int concurrencyLimit) {
		super(concurrencyLimit);
	}

	@Override
	public String getStrategyGroupKey(Object[] args) {
		return args[0].toString();
	}

}
