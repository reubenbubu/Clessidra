package com.ccbill.clessidra.quickstart.step1;


import org.springframework.stereotype.Component;

import com.ccbill.clessidra.annotations.RateLimited;



@Component
public class Step1Service {

    @RateLimited
    public String provisionService() {
        return "Service Provisioned.";
    }

}
