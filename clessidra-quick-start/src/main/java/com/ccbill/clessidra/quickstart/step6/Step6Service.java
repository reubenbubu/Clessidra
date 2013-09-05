package com.ccbill.clessidra.quickstart.step6;


import java.util.List;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.ccbill.clessidra.annotations.RateLimited;
import com.ccbill.clessidra.enums.MethodGrouping;



@Component
public class Step6Service {

    @RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "provisioningGroup", limiterBean = "invocationRateLimiter")
    public String provisionService(Integer customerId) {
        return "Service Provisioned to Customer " + customerId + ".";
    }


    @RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "provisioningGroup", limiterBean = "invocationRateLimiter")
    public String provisionService(Integer customerId, String service) {
        return "Service " + service + " Provisioned to Customer " + customerId + ".";
    }



    // changed the limiterBean to match the new spring configuration
    @Async
    @RateLimited(limiterBean = "costBasedAndConcurrencyChained")
    public Future<String> provisionServices(List services) {
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {

        }
        return new AsyncResult<String>("Provisioned " + services.size() + " Services.");
    }


}
