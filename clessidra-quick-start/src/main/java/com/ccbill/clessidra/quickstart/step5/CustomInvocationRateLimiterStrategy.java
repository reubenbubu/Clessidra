package com.ccbill.clessidra.quickstart.step5;


import com.ccbill.clessidra.strategy.BaseInvocationRateLimiterStrategy;



public class CustomInvocationRateLimiterStrategy extends BaseInvocationRateLimiterStrategy {

    public CustomInvocationRateLimiterStrategy(Integer numberOfInvocationsLimit, Integer perTimePeriodSeconds) {
        super(numberOfInvocationsLimit, perTimePeriodSeconds);
    }



    @Override
    public String getStrategyGroupKey(Object[] args) {

        // expect a number as the first parameter and return it as the strategy group key

        if (args.length > 0 && args[0] instanceof Number) {
            return args[0].toString();
        }

        return "";

    }

}
