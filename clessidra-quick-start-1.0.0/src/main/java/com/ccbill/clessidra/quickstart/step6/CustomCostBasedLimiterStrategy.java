package com.ccbill.clessidra.quickstart.step6;


import java.util.List;

import com.ccbill.clessidra.strategy.BaseCostBasedLimiterStrategy;



public class CustomCostBasedLimiterStrategy extends BaseCostBasedLimiterStrategy {


    public CustomCostBasedLimiterStrategy(Integer costLimit, Integer perTimePeriodSeconds) {
        super(costLimit, perTimePeriodSeconds);
    }



    @Override
    public String getStrategyGroupKey(Object[] args) {

        return "";

    }



    @Override
    public Integer calculateCost(Object[] args) {

        // this strategy is expecting to find a list in the first parameter
        // the cost returned will be it's size

        if (args.length > 0) {
            if (args[0] instanceof List) {
                return ((List) args[0]).size();
            }
        }

        return 1;

    }

}
