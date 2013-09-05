package com.ccbill.clessidra.strategy.defaults;

import com.ccbill.clessidra.strategy.BaseCostBasedLimiterStrategy;

/**
 * A useable {@link BaseCostBasedLimiterStrategy} that groups all the invocations under the same strategy group and returns a cost of 1 for every
 * method invocation. Although this limiter strategy is fully usable it has the same effect as {@link DefaultInvocationRateLimiterStrategy} due to the
 * fixed cost of 1. The main purpose of this class is to be show how a cost based limiter strategy would look like. In practice the user should
 * override {@link BaseCostBasedLimiterStrategy} and implement the proper cost calculation.
 * 
 * @author reubena
 * 
 */
public class DefaultCostBasedLimiterStrategy extends BaseCostBasedLimiterStrategy {

	public DefaultCostBasedLimiterStrategy(int costLimit, int perTimePeriodSeconds) {
		super(costLimit, perTimePeriodSeconds);
	}

	public String getStrategyGroupKey(Object[] args) {
		return "";
	}

	public Integer calculateCost(Object[] args) {
		return 1;
	}

}
