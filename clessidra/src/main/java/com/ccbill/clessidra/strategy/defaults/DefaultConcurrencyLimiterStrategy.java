package com.ccbill.clessidra.strategy.defaults;

import com.ccbill.clessidra.strategy.BaseConcurrencyLimiterStrategy;

/**
 * A useable {@link BaseConcurrencyLimiterStrategy} that groups all the
 * invocations under the same strategy group.
 *
 * @author reubena
 *
 */
public class DefaultConcurrencyLimiterStrategy extends BaseConcurrencyLimiterStrategy {

    public DefaultConcurrencyLimiterStrategy(int concurrencyLimit) {
        super(concurrencyLimit);
    }

    @Override
    public String getStrategyGroupKey(Object[] args) {
        return "";
    }

}
