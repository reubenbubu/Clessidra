package com.ccbill.clessidra.queue;


import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
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



    public void impatientAcquire(long maxWaitMillis)
            throws SemaphoreQueueFullException, SemaphoreImpatienceException {
        if (queueLength.intValue() < maxQueueSize) {
            queueLength.incrementAndGet();
            try {
                semaphore.tryAcquire(maxWaitMillis, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                throw new SemaphoreImpatienceException("Semaphore acquire wait time of " + maxWaitMillis + "ms expired.", e);
            }
            finally {
                queueLength.decrementAndGet();
            }
        }
        else {
            throw new SemaphoreQueueFullException("Semaphore queue limit of " + maxQueueSize + " reached.");
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
    
    /**
     * Release a permit only if an acquire is queued
     */
    public void releaseToQueue() {
    	if (getAccurateQueueLength() > 0) {
    		semaphore.release();
    	}
    }
    
    public int availablePermits() {
        return semaphore.availablePermits();
    }
    
    @Override
    public String toString() {
    	return semaphore.toString() + "[QueueLength = " + getAccurateQueueLength() + "]";
    }


}
