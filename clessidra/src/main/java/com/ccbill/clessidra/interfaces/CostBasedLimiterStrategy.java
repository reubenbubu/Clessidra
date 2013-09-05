package com.ccbill.clessidra.interfaces;

/**
 * A {@link LimiterStrategy} that is based on invocation cost.
 * 
 * @author reubena
 * 
 */
public interface CostBasedLimiterStrategy extends LimiterStrategy {

	/**
	 * Calculate the cost of this method invocation.
	 * 
	 * @param args
	 *            The arguments of the annotated method.
	 * @return The cost.
	 */
	public Integer calculateCost(Object[] args);

}
