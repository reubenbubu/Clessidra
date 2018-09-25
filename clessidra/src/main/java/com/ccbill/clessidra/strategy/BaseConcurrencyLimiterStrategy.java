package com.ccbill.clessidra.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.ccbill.clessidra.interfaces.RequiresPostInvocationCleanup;
import com.ccbill.clessidra.strategy.defaults.DefaultConcurrencyLimiterStrategy;

/**
 * A base Limiter Strategy implementation that contains all the logic needed for
 * concurrency limitation. The only thing missing is the strategy group key
 * which is implemented in the {@link DefaultConcurrencyLimiterStrategy}.
 * 
 * @author reubena
 * 
 */
public abstract class BaseConcurrencyLimiterStrategy extends AbstractLimiterStrategy
		implements RequiresPostInvocationCleanup {

	private Logger logger = Logger.getLogger(BaseConcurrencyLimiterStrategy.class);

	private Map<String, List<UUID>> currentActiveInvocations = new ConcurrentHashMap<String, List<UUID>>();

	private Integer concurrencyLimitDefault;

	/**
	 * Constructs this {@link BaseConcurrencyLimiterStrategy}
	 * 
	 * @param concurrencyLimitDefault
	 *            The limit of concurrent method invocations allowed.
	 */
	public BaseConcurrencyLimiterStrategy(Integer concurrencyLimitDefault) {
		this.concurrencyLimitDefault = concurrencyLimitDefault;
	}

	/**
	 * {@inheritDoc}
	 */
	public LimiterStrategyConclusion hasLimitBeenExceededChain(String methodGroup, UUID invocationUUID, Object[] args) {

		boolean callNextInChain = false;

		Integer concurrencyLimit = getConcurrencyLimit(methodGroup, args);

		logger.trace("Running hasLimitBeenExceededChain of " + this.getClass().getName());

		String historyKey = getHistoryKey(methodGroup, invocationUUID, args);

		int currentConcurrentInvocationCount = 0;

		List<UUID> currentInvocationUUIDs = null;

		synchronized (this) {

			// find the current concurrency amount
			currentInvocationUUIDs = currentActiveInvocations.get(historyKey);

			if (currentInvocationUUIDs == null) {
				currentInvocationUUIDs = Collections.synchronizedList(new ArrayList<UUID>());
				currentActiveInvocations.put(historyKey, currentInvocationUUIDs);
			}

			currentConcurrentInvocationCount = currentInvocationUUIDs.size();

			// if the concurrency amount is less than the limit
			logger.debug("Checking concurrency limit [key=" + historyKey + "] " + currentConcurrentInvocationCount
					+ " < " + concurrencyLimit + " "
					+ (currentConcurrentInvocationCount < concurrencyLimit ? "Allowed" : "Blocked"));
			if (currentConcurrentInvocationCount < concurrencyLimit) {

				// add the current invocation to the list
				currentInvocationUUIDs.add(invocationUUID);
				callNextInChain = true;

			}

		}

		if (callNextInChain) {
			return callNextChainedLimiterStrategy(methodGroup, invocationUUID, args);
		} else {
			return buildExceededConclusion(this, methodGroup, invocationUUID, args);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDetailedExceededMessage(String methodGroup, UUID invocationUUID, Object[] args) {

		Integer concurrencyLimit = getConcurrencyLimit(methodGroup, args);

		return "Reached allowed concurrency limit of " + concurrencyLimit + ".";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getGenericExceededMessage(String methodGroup, UUID invocationUUID, Object[] args) {
		return "Reached allowed concurrency limit.";
	}

	/**
	 * Gets the concurrency limit taking into account any possible property
	 * overrides
	 * 
	 * @param methodGroup
	 *            The group name of method group.
	 * @param args
	 *            The arguments of the annotated method.
	 * @return The concurrency limit.
	 */
	public Integer getConcurrencyLimit(String methodGroup, Object[] args) {

		if (getPropertyOverrideProvider() != null) {
			String override = getPropertyOverrideProvider().getPropertyOverride(this.getClass(), methodGroup,
					getStrategyGroupKey(args), "concurrencyLimit");
			if (override != null)
				return Integer.parseInt(override);
		}

		return concurrencyLimitDefault;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void rollback(String methodGroup, UUID invocationUUID, Object[] args) {

		String historyKey = getHistoryKey(methodGroup, invocationUUID, args);
		List<UUID> currentInvocationUUIDs = currentActiveInvocations.get(historyKey);
		currentInvocationUUIDs.remove(invocationUUID);
		currentActiveInvocations.put(historyKey, currentInvocationUUIDs);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void flush() {

		currentActiveInvocations.clear();

	}

}
