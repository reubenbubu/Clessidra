package com.ccbill.clessidra.quickstart.step1;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;



@Component
public class Step1Launcher {

    @Autowired
    private Step1Service service;



    public static void main(String[] args) {

        ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.clessidra.quickstart.step1.xml");
        Step1Launcher launcher = ctx.getBean(Step1Launcher.class);
        launcher.launch();

    }



    public void launch() {

        for (int i = 0; i < 6; i++) {
            System.out.println(i + " " + service.provisionService());
        }

    }


}
