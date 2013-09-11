package com.ccbill.clessidra.strategy;

import com.ccbill.clessidra.interfaces.LimiterStrategy;

/**
 * Object representing the conclusion of a {@link LimiterStrategy}.
 * 
 * This object will be returned by {@link LimiterStrategy#hasLimitBeenExceededChain(String, java.util.UUID, Object[])}
 * 
 * @author reubena
 * 
 */
public class LimiterStrategyConclusion {

	/**
	 * Whether the limit has been exceeded or not
	 */
	private Boolean hasLimitBeenExceeded;

	/**
	 * A generic message explaining why the limit was exceeded.
	 */
	private String genericExceededMessage;

	/**
	 * A detailed message explaining why the limit was exceeded.
	 */
	private String detailedExceededMessage;

	/**
	 * The {@link LimiterStrategy} responsible.
	 */
	private LimiterStrategy strategyResponsible;
	
	/**
	 * Whether the request was discarded due to reaching queue limit
	 */
	private Boolean hasExceededQueueSize;
	
	public LimiterStrategyConclusion(Boolean hasLimitBeenExceeded) {
		this.hasLimitBeenExceeded = hasLimitBeenExceeded;
	}

	public LimiterStrategyConclusion(Boolean hasLimitBeenExceeded, LimiterStrategy strategyResponsible) {
		this.hasLimitBeenExceeded = hasLimitBeenExceeded;
		this.strategyResponsible = strategyResponsible;
	}

	public Boolean getHasLimitBeenExceeded() {
		return hasLimitBeenExceeded;
	}

	public void setHasLimitBeenExceeded(Boolean hasLimitBeenExceeded) {
		this.hasLimitBeenExceeded = hasLimitBeenExceeded;
	}

	public String getGenericExceededMessage() {
		return genericExceededMessage;
	}

	public void setGenericExceededMessage(String genericExceededMessage) {
		this.genericExceededMessage = genericExceededMessage;
	}

	public String getDetailedExceededMessage() {
		return detailedExceededMessage;
	}

	public void setDetailedExceededMessage(String detailedExceededMessage) {
		this.detailedExceededMessage = detailedExceededMessage;
	}

	public LimiterStrategy getStrategyResponsible() {
		return strategyResponsible;
	}

	public void setStrategyResponsible(LimiterStrategy strategyResponsible) {
		this.strategyResponsible = strategyResponsible;
	}

	public Boolean getHasExceededQueueSize() {
		return hasExceededQueueSize;
	}

	public void setHasExceededQueueSize(Boolean hasExceededQueueSize) {
		this.hasExceededQueueSize = hasExceededQueueSize;
	}
	
	


}
