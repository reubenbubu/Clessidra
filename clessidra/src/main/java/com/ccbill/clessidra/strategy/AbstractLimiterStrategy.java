package com.ccbill.clessidra.strategy;

import java.util.ArrayList;
import java.util.UUID;

import com.ccbill.clessidra.interfaces.LimiterStrategy;
import com.ccbill.clessidra.interfaces.RequiresPostInvocationCleanup;
import com.ccbill.clessidra.override.PropertyOverrideProvider;
import com.google.common.base.Strings;

/**
 * Partial implementation of {@link LimiterStrategy}
 * 
 * @author reubena
 * 
 */
public abstract class AbstractLimiterStrategy implements LimiterStrategy {

	private PropertyOverrideProvider propertyOverrideProvider;

	private LimiterStrategy nextLimiterStrategy = null;

	/**
	 * Creates a chain of limiter strategies with property overrides
	 * 
	 * @param limiterStrategies
	 *            The list of {@link AbstractLimiterStrategy} subclasses.
	 * @param propertyOverrideProvider
	 *            The {@link PropertyOverrideProvider}
	 * @return The chain of limiter strategies
	 */
	public static AbstractLimiterStrategy createInstance(ArrayList<? extends AbstractLimiterStrategy> limiterStrategies,
			PropertyOverrideProvider propertyOverrideProvider) {

		AbstractLimiterStrategy firstLimiter = null;

		AbstractLimiterStrategy temp = null;

		for (AbstractLimiterStrategy currentInLoop : limiterStrategies) {

			// If the current strategy doesn't have it's own PropertyOverrideProvider, set the chain
			// PropertyOverrideProvider
			if (currentInLoop.getPropertyOverrideProvider() == null) {
				currentInLoop.setPropertyOverrideProvider(propertyOverrideProvider);
			}

			if (firstLimiter == null) {
				firstLimiter = currentInLoop;
				firstLimiter.setPropertyOverrideProvider(propertyOverrideProvider);
				temp = currentInLoop;
			} else {
				temp.setNextLimiterStrategy(currentInLoop);
				temp.setPropertyOverrideProvider(propertyOverrideProvider);
				temp = currentInLoop;
			}
		}

		return firstLimiter;

	}

	/**
	 * Creates a chain of limiter strategies
	 * 
	 * @param limiterStrategies
	 *            The list of {@link AbstractLimiterStrategy} subclasses.
	 * @return The chain of limiter strategies
	 */
	public static AbstractLimiterStrategy createInstance(ArrayList<? extends AbstractLimiterStrategy> limiterStrategies) {
		return createInstance(limiterStrategies, new PropertyOverrideProvider());
	}

	/**
	 * {@inheritDoc}
	 */
	public void setNextLimiterStrategy(LimiterStrategy nextLimiterStrategy) {
		this.nextLimiterStrategy = nextLimiterStrategy;
	}

	/**
	 * {@inheritDoc}
	 */
	public LimiterStrategy getNextLimiterStrategy() {
		return nextLimiterStrategy;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getHistoryKey(String methodGroup, UUID uuid, Object[] args) {

		String key = "";

		if (!Strings.isNullOrEmpty(methodGroup))
			key = "MG:" + methodGroup;

		String strategyGroup = getStrategyGroupKey(args);

		if (!Strings.isNullOrEmpty(strategyGroup)) {
			if (!Strings.isNullOrEmpty(key))
				key = key + ",";
			key = key + "SGK:" + strategyGroup;
		}

		return key;
	}

	/**
	 * {@inheritDoc}
	 */
	public void postInvocationCleanupChain(String methodGroup, UUID invocationUUID, Object[] args) {

		if (this instanceof RequiresPostInvocationCleanup) {
			rollback(methodGroup, invocationUUID, args);
		}

		if (getNextLimiterStrategy() != null) {
			getNextLimiterStrategy().postInvocationCleanupChain(methodGroup, invocationUUID, args);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public void rollbackChain(String methodGroup, UUID invocationUUID, Object[] args) {

		rollback(methodGroup, invocationUUID, args);

		LimiterStrategy nextLimiterStrategy = getNextLimiterStrategy();
		if (nextLimiterStrategy != null) {
			nextLimiterStrategy.rollback(methodGroup, invocationUUID, args);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public PropertyOverrideProvider getPropertyOverrideProvider() {
		return propertyOverrideProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPropertyOverrideProvider(PropertyOverrideProvider propertyOverrideProvider) {
		this.propertyOverrideProvider = propertyOverrideProvider;
	}

	public LimiterStrategyConclusion callNextChainedLimiterStrategy(String methodGroup, UUID invocationUUID, Object[] args) {

		LimiterStrategy nextLimiterStrategy = getNextLimiterStrategy();

		// if there are no more strategies in the chain, return false
		if (nextLimiterStrategy == null) {
			return new LimiterStrategyConclusion(false);
		}

		// call the next strategy in chain
		LimiterStrategyConclusion nextLimiterStrategyConclusion = nextLimiterStrategy.hasLimitBeenExceededChain(methodGroup, invocationUUID, args);

		// if the next strategy return true, rollback, the user method will not be invoked
		if (nextLimiterStrategyConclusion.getHasLimitBeenExceeded()) {
			rollback(methodGroup, invocationUUID, args);
		}

		return nextLimiterStrategyConclusion;

	}

	@Override
	public String getDetailedExceededMessage(String methodGroup, UUID invocationUUID, Object[] args) {
		return "Limit reached";
	}

	@Override
	public String getGenericExceededMessage(String methodGroup, UUID invocationUUID, Object[] args) {
		return "Limit reached";
	}

	public LimiterStrategyConclusion buildExceededConclusion(LimiterStrategy strategyResponsible, String methodGroup, UUID invocationUUID,
			Object[] args) {

		LimiterStrategyConclusion conclusion = new LimiterStrategyConclusion(true, strategyResponsible);
		conclusion.setDetailedExceededMessage(getDetailedExceededMessage(methodGroup, invocationUUID, args));
		conclusion.setGenericExceededMessage(getGenericExceededMessage(methodGroup, invocationUUID, args));

		return conclusion;

	}
	
	public LimiterStrategyConclusion buildExceededConclusion(LimiterStrategy strategyResponsible, String methodGroup, UUID invocationUUID,
			Object[] args, long suggestedRetryWaitMillis) {

		LimiterStrategyConclusion conclusion = new LimiterStrategyConclusion(true, strategyResponsible);
		conclusion.setDetailedExceededMessage(getDetailedExceededMessage(methodGroup, invocationUUID, args));
		conclusion.setGenericExceededMessage(getGenericExceededMessage(methodGroup, invocationUUID, args));

		return conclusion;

	}
}
