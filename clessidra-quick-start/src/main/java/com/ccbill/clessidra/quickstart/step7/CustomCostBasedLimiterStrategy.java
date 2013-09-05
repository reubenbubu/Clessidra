package com.ccbill.clessidra.quickstart.step7;


import java.util.List;

import com.ccbill.clessidra.strategy.BaseCostBasedLimiterStrategy;



public class CustomCostBasedLimiterStrategy extends BaseCostBasedLimiterStrategy {


    public CustomCostBasedLimiterStrategy(Integer costLimit, Integer perTimePeriodSeconds) {
        super(costLimit, perTimePeriodSeconds);
    }



    @Override
    public String getStrategyGroupKey(Object[] args) {

        // expect a number as the first parameter and return it as the strategy group key
        
        if (args.length > 0 && args[0] instanceof Number) {
            return args[0].toString();
        }
        
        return "";

    }



    @Override
    public Integer calculateCost(Object[] args) {

        // this strategy is expecting to find a list in the second parameter
        // the cost returned will be it's size
        
        if (args.length >= 2) {
            if (args[1] instanceof List) {
                return ((List) args[1]).size();
            }
        }

        return 1;

    }

}
