package com.ccbill.clessidra.exception;

import com.ccbill.clessidra.strategy.LimiterStrategyConclusion;

/**
 * An exception that is thrown when a method invocation is blocked due to
 * reaching a particular limit.
 * 
 * @author reubena
 * 
 */
@SuppressWarnings("serial")
public class RateLimiterException extends RuntimeException {

	private LimiterStrategyConclusion conclusion;
	
	public RateLimiterException() {
		super();
	}

	public RateLimiterException(LimiterStrategyConclusion conclusion) {
		super();
		this.conclusion = conclusion;
	}

	public RateLimiterException(String message, Throwable cause) {
		super(message, cause);
	}

	public RateLimiterException(String message, Throwable cause, LimiterStrategyConclusion conclusion) {
		super(message, cause);
		this.conclusion = conclusion;
	}

	public RateLimiterException(String message) {
		super(message);
	}

	public RateLimiterException(String message, LimiterStrategyConclusion conclusion) {
		super(message);
		this.conclusion = conclusion;
	}

	public RateLimiterException(Throwable cause) {
		super(cause);
	}

	public LimiterStrategyConclusion getConclusion() {
		return conclusion;
	}

	public void setConclusion(LimiterStrategyConclusion conclusion) {
		this.conclusion = conclusion;
	}

}
