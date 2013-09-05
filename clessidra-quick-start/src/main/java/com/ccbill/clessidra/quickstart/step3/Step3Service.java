package com.ccbill.clessidra.quickstart.step3;


import java.util.List;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.ccbill.clessidra.annotations.RateLimited;
import com.ccbill.clessidra.enums.MethodGrouping;



@Component
public class Step3Service {

    // added limiterBean
    @RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "provisioningGroup", limiterBean = "invocationRateLimiter")
    public String provisionService() {
        return "Service Provisioned.";
    }



    // added limiterBean
    @RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "provisioningGroup", limiterBean = "invocationRateLimiter")
    public String provisionService(String service) {
        return "Service " + service + " Provisioned.";
    }



    // added async to simulate concurrent executions
    // added limiterBean
    @Async
    @RateLimited(limiterBean = "concurrencyLimiter")
    public Future<String> provisionServices(List services) {
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {

        }
        return new AsyncResult<String>("Provisioned " + services.size() + " Services.");
    }


}
