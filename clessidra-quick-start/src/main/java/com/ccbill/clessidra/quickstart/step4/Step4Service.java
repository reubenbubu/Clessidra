package com.ccbill.clessidra.quickstart.step4;


import java.util.List;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.ccbill.clessidra.annotations.RateLimited;
import com.ccbill.clessidra.enums.MethodGrouping;



@Component
public class Step4Service {

    @RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "provisioningGroup", limiterBean = "invocationRateLimiter")
    public String provisionService() {
        return "Service Provisioned.";
    }



    @RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "provisioningGroup", limiterBean = "invocationRateLimiter")
    public String provisionService(String service) {
        return "Service " + service + " Provisioned.";
    }



    // changed limiterBean to a invocationRateAndConcurrencyChained,
    // which is configured as a chain in the beans.clessidra.quickstart.step4.xml
    @Async
    @RateLimited(limiterBean = "invocationRateAndConcurrencyChained")
    public Future<String> provisionServices(List services) {
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {

        }
        return new AsyncResult<String>("Provisioned " + services.size() + " Services.");
    }


}
