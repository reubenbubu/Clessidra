package com.ccbill.clessidra.quickstart.step6;


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
public class Step6Launcher {

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private Step6Service service;



    public static void main(String[] args) {

        ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.clessidra.quickstart.step6.xml");
        Step6Launcher launcher = ctx.getBean(Step6Launcher.class);
        launcher.launch();

    }



    public void launch() {

        for (int i = 0; i < 6; i++) {
            try {
                System.out.println(i + " " + service.provisionService(1));
            }
            catch (RateLimiterException e) {
                logger.error(e.getConclusion().getDetailedExceededMessage());
            }
            try {
                System.out.println(i + " " + service.provisionService(2, "Yearly Subscription A"));
            }
            catch (RateLimiterException e) {
                logger.error(e.getConclusion().getDetailedExceededMessage());
            }
            try {
                System.out.println(i + " " + service.provisionService(3, "Yearly Subscription B"));
            }
            catch (RateLimiterException e) {
                logger.error(e.getConclusion().getDetailedExceededMessage());
            }
        }

        List<String> serviceList = new ArrayList<String>();
        serviceList.add("Monthly Subscription");
        serviceList.add("Quarterly Subscription");

        List<Future<String>> futures = new ArrayList<Future<String>>();
        for (int i = 0; i < 6; i++) {
            futures.add(service.provisionServices(serviceList));
        }

        int a = 0;
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
