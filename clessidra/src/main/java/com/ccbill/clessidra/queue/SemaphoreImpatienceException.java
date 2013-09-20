package com.ccbill.clessidra.queue;


public class SemaphoreImpatienceException extends Exception {

    public SemaphoreImpatienceException() {
        super();
    }



    public SemaphoreImpatienceException(String message, Throwable cause) {
        super(message, cause);
    }



    public SemaphoreImpatienceException(String message) {
        super(message);
    }



    public SemaphoreImpatienceException(Throwable cause) {
        super(cause);
    }


}
