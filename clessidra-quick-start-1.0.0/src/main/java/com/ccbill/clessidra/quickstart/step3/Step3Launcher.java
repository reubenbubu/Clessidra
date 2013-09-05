package com.ccbill.clessidra.quickstart.step3;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.ccbill.clessidra.exception.RateLimiterException;



@Component
public class Step3Launcher {

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private Step3Service service;



    public static void main(String[] args) {

        ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.clessidra.quickstart.step3.xml");
        Step3Launcher launcher = ctx.getBean(Step3Launcher.class);
        launcher.launch();

    }



    public void launch() {

        for (int i = 0; i < 6; i++) {
            try {
                System.out.println(i + " " + service.provisionService());
            }
            catch (RateLimiterException e) {
                logger.error(e.getConclusion().getDetailedExceededMessage());
            }
            try {
                System.out.println(i + " " + service.provisionService("Yearly Subscription"));
            }
            catch (RateLimiterException e) {
                logger.error(e.getConclusion().getDetailedExceededMessage());
            }
        }

        List<String> serviceList = new ArrayList<String>();
        serviceList.add("Monthly Subscription");
        serviceList.add("Quarterly Subscription");
        
        // this will emulate concurrent requests coming into our service
        List<Future<String>> futures = new ArrayList<Future<String>>();
        for (int i = 0; i < 6; i++) {
            futures.add(service.provisionServices(serviceList));
        }

        int a=0;
        for (Future<String> currentFuture : futures) {
            a++;
            try {
                System.out.println(a + " " + currentFuture.get());
            }
            catch (Exception e) {
                if (e.getCause() instanceof RateLimiterException) {
                    logger.error(a + " " + ((RateLimiterException) e.getCause()).getConclusion().getDetailedExceededMessage());
                }
            }
        }

    }


}
