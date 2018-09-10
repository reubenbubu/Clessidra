package com.ccbill.clessidra.strategy.defaults;

import com.ccbill.clessidra.strategy.BaseInvocationRateLimiterStrategy;

/**
 * A useable {@link BaseInvocationRateLimiterStrategy} that groups all the
 * invocations under the same strategy group.
 * 
 * @author reubena
 * 
 */
public class DefaultInvocationRateLimiterStrategy extends BaseInvocationRateLimiterStrategy {

	public DefaultInvocationRateLimiterStrategy(int numberOfInvocationsLimit, int perTimePeriodSeconds) {
		super(numberOfInvocationsLimit, perTimePeriodSeconds);
	}

	public String getStrategyGroupKey(Object[] args) {
		return "";
	}

}
