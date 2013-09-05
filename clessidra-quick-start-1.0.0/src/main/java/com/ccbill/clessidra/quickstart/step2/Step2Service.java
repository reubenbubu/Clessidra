package com.ccbill.clessidra.quickstart.step2;


import java.util.List;

import org.springframework.stereotype.Component;

import com.ccbill.clessidra.annotations.RateLimited;
import com.ccbill.clessidra.enums.MethodGrouping;



@Component
public class Step2Service {

    // added method grouping
    @RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "provisioningGroup")
    public String provisionService() {
        return "Service Provisioned.";
    }



    // added method grouping
    @RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "provisioningGroup")
    public String provisionService(String service) {
        return "Service " + service + " Provisioned.";
    }



    // new ungrouped method along with grouped methods in the same class
    @RateLimited
    public String provisionServices(List services) {
        return "Provisioned " + services.size() + " Services.";
    }


}
