package com.ccbill.clessidra.quickstart.step7;


import com.ccbill.clessidra.strategy.BaseInvocationRateLimiterStrategy;



public class CustomInvocationRateLimiterStrategy extends BaseInvocationRateLimiterStrategy {

    public CustomInvocationRateLimiterStrategy(Integer numberOfInvocationsLimit, Integer perTimePeriodSeconds) {
        super(numberOfInvocationsLimit, perTimePeriodSeconds);
    }



    @Override
    public String getStrategyGroupKey(Object[] args) {

        if (args.length > 0 && args[0] instanceof Number) {
            return args[0].toString();
        }

        return "";

    }

}
