package com.ccbill.clessidra.interfaces;

import java.util.UUID;

import com.ccbill.clessidra.override.PropertyOverrideProvider;
import com.ccbill.clessidra.strategy.LimiterStrategyConclusion;

/**
 * The interface defining a {@link LimiterStrategy}. It is based on the chain-of-responsibility pattern whereby each limiter strategy is chained to
 * another. The decision to block a method invocation is passed on to the next {@link LimiterStrategy} if the current one doesn't detect the limit was
 * exceeded.
 * 
 * @author reubena
 * 
 */
public interface LimiterStrategy {

	/**
	 * Holds the logic of a particular limiter strategy
	 * 
	 * @param methodGroup
	 *            The group name of method group.
	 * @param invocationUUID
	 *            The invocation UUID.
	 * @param args
	 *            The arguments of the annotated method.
	 * @return The {@link LimiterStrategyConclusion} of this limiter strategy.
	 */
	public LimiterStrategyConclusion hasLimitBeenExceededChain(String methodGroup, UUID invocationUUID, Object[] args);
	
    /**
     * Holds the logic of a particular limiter strategy 
     * 
     * @param methodGroup
     *            The group name of method group.
     * @param invocationUUID
     *            The invocation UUID.
     * @param args
     *            The arguments of the annotated method.
     * @param charged
     *            Whether the method call should be charged towards the limit or not in the strategy.
     *            Useful to check whether the strategy is in an exceeded state or not.            
     * @return The {@link LimiterStrategyConclusion} of this limiter strategy.
     */
    public LimiterStrategyConclusion hasLimitBeenExceededChain(String methodGroup, UUID invocationUUID, Object[] args, boolean charged);	

	/**
	 * Rollback this limiter strategy only
	 * 
	 * @param methodGroup
	 *            The group name of method group.
	 * @param invocationUUID
	 *            The invocation UUID.
	 * @param args
	 *            The arguments of the annotated method.
	 */
	public void rollback(String methodGroup, UUID invocationUUID, Object[] args);

	/**
	 * Rollback this limiter strategy, and call the rollback of the next limiter strategy in the chain.
	 * 
	 * @param methodGroup
	 *            The group name of method group.
	 * @param invocationUUID
	 *            The invocation UUID.
	 * @param args
	 *            The arguments of the annotated method.
	 */
	public void rollbackChain(String methodGroup, UUID invocationUUID, Object[] args);

	/**
	 * Cleanup this limiter strategy using the rollback method if this limiter strategy implements the marker interface
	 * {@link RequiresPostInvocationCleanup} and then call the same method for the next strategy in the chain.
	 * 
	 * @param methodGroup
	 *            The group name of method group.
	 * @param invocationUUID
	 *            The invocation UUID.
	 * @param args
	 *            The arguments of the annotated method.
	 */
	public void postInvocationCleanupChain(String methodGroup, UUID invocationUUID, Object[] args);

	/**
	 * Returns the strategy group key. Every method invocation sharing the same strategy group key in the same limiter strategy will contribute
	 * towards the same counter.
	 * 
	 * @param args
	 *            The arguments of the annotated method.
	 * @return The strategy group key.
	 */
	public String getStrategyGroupKey(Object[] args);

	/**
	 * Set the next {@link LimiterStrategy} in the chain.
	 * 
	 * @param nextLimiterStrategy
	 *            The next {@link LimiterStrategy}.
	 */
	public void setNextLimiterStrategy(LimiterStrategy nextLimiterStrategy);

	/**
	 * Get the next {@link LimiterStrategy} in the chain
	 * 
	 * @return The next {@link LimiterStrategy}.
	 */
	public LimiterStrategy getNextLimiterStrategy();

	/**
	 * If the {@link LimiterStrategy} holds a map containing relevant history information to base the limiting logic on, this method will return the
	 * key to that map. In most cases this will be a combination of the method group name and the strategy group key.
	 * 
	 * @param methodGroup
	 *            The group name of method group.
	 * @param invocationUUID
	 *            The invocation UUID.
	 * @param args
	 *            The arguments of the annotated method.
	 * @return The history key.
	 */
	public String getHistoryKey(String methodGroup, UUID uuid, Object[] args);

	/**
	 * Returns the {@link PropertyOverrideProvider}.
	 * 
	 * @return The {@link PropertyOverrideProvider}.
	 */
	public PropertyOverrideProvider getPropertyOverrideProvider();

	/**
	 * Sets the {@link PropertyOverrideProvider}
	 * 
	 * @param propertyOverrideProvider
	 *            The {@link PropertyOverrideProvider} to set.
	 */
	public void setPropertyOverrideProvider(PropertyOverrideProvider propertyOverrideProvider);

	/**
	 * Returns a detailed message explaining why the limit has been exceeded. It is encouraged to include the limit values in the message returned.
	 * 
	 * @param methodGroup
	 *            The group name of method group.
	 * @param invocationUUID
	 *            The invocation UUID.
	 * @param args
	 *            The arguments of the annotated method.
	 * @return The message.
	 */
	public String getDetailedExceededMessage(String methodGroup, UUID invocationUUID, Object[] args);

	/**
	 * Returns a generic message explaining why the limit has been exceeded. It is discouraged to include any limit values in the message returned
	 * 
	 * @param methodGroup
	 *            The group name of method group.
	 * @param invocationUUID
	 *            The invocation UUID.
	 * @param args
	 *            The arguments of the annotated method.
	 * @return The message.
	 */
	public String getGenericExceededMessage(String methodGroup, UUID invocationUUID, Object[] args);

	/**
	 * Clears all the invocation history. Mainly for test purposes.
	 */
	public void flush();

}
