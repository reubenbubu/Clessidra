package com.ccbill.clessidra.queue;

/**
 * Thrown by {@link LimitedQueueSemaphore} when trying to acquire a permit but queue is full.
 * 
 * @author reubena
 *
 */
public class SemaphoreQueueFullException extends Exception {

    public SemaphoreQueueFullException() {
    }



    public SemaphoreQueueFullException(String message) {
        super(message);
    }



    public SemaphoreQueueFullException(Throwable cause) {
        super(cause);
    }



    public SemaphoreQueueFullException(String message, Throwable cause) {
        super(message, cause);
    }

}
