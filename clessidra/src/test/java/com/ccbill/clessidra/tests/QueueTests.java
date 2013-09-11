package com.ccbill.clessidra.tests;


import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ccbill.clessidra.exception.RateLimiterException;
import com.ccbill.clessidra.tests.services.ConcurrencyQueuedServiceMethodAnnotated;



@ContextConfiguration(locations = { "classpath:beans.clessidra.test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class QueueTests {


    private Logger logger = Logger.getLogger(this.getClass());


    @Autowired
    private ConcurrencyQueuedServiceMethodAnnotated concurrencyQueuedServiceMethodAnnotated;



    @Test
    public void testQueue()
            throws InterruptedException {

        List<Future<String>> futures = Collections.synchronizedList(new Vector<Future<String>>());


        for (int i = 0; i < 20; i++) {
            futures.add(concurrencyQueuedServiceMethodAnnotated.testConcurrencyUngrouped());
//            Thread.sleep(150);
        }


        for (Future<String> current : futures) {
            try {
                current.get();
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (ExecutionException e) {
                if (e.getCause() instanceof RateLimiterException) {
                    RateLimiterException rle = (RateLimiterException) e.getCause();
                    logger.error(rle.getConclusion().getDetailedExceededMessage());
                }
                else {
                    logger.error(e);
                }
            }
        }

    }

}
