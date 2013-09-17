package com.ccbill.clessidra.queue;


import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * {@link LimitedQueueSemaphore} is a {@link Semaphore} wrapper that limits the number of queued
 * acquisitions. Inherently, it also returns an accurate queue length, which is a limitation of
 * {@link Semaphore} where it only returns an estimate.
 * 
 * @author reubena
 * 
 */
public class LimitedQueueSemaphore {

    private Semaphore semaphore = null;

    private AtomicInteger queueLength = new AtomicInteger(0);
    private Integer maxQueueSize = null;

    
    /**
     * Construct a {@link LimitedQueueSemaphore}
     * 
     * @param permits - number of permits
     * @param fair - fairness
     * @param maxQueueSize - maximum amount of queued acquisitions
     */
    public LimitedQueueSemaphore(int permits, boolean fair, int maxQueueSize) {
        semaphore = new Semaphore(permits, fair);
        this.maxQueueSize = new Integer(maxQueueSize);
    }


    /**
     * Acquire a permit
     * 
     * @throws InterruptedException
     * @throws SemaphoreQueueFullException When the queue limit is reached
     */
    public void acquire()
            throws InterruptedException, SemaphoreQueueFullException {
        if (queueLength.intValue() < maxQueueSize) {
            queueLength.incrementAndGet();
            semaphore.acquire();
            queueLength.decrementAndGet();
        }
        else {
            throw new SemaphoreQueueFullException();
        }
    }



    /**
     * Acquire a permit uninterruptibly
     * 
     * @throws SemaphoreQueueFullException
     */
    public void acquireUninterruptibly()
            throws SemaphoreQueueFullException {
        if (queueLength.intValue() < maxQueueSize) {
            queueLength.incrementAndGet();
            semaphore.acquireUninterruptibly();
            queueLength.decrementAndGet();
        }
        else {
            throw new SemaphoreQueueFullException();
        }

    }


    /**
     * Get the size of the queue
     * 
     * @return the size of the queue
     */
    public int getAccurateQueueLength() {
        return queueLength.get();
    }


    /**
     * Release a permit
     * 
     */
    public void release() {
        semaphore.release();
    }


}
