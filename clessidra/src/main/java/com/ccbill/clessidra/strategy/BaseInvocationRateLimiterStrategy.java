package com.ccbill.clessidra.strategy;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.ccbill.clessidra.strategy.defaults.DefaultInvocationRateLimiterStrategy;



/**
 * A base Limiter Strategy implementation that contains all the logic needed for method invocation
 * rate limitation. The only thing missing is the strategy group key which is implemented in the
 * {@link DefaultInvocationRateLimiterStrategy}.
 * 
 * @author reubena
 * 
 */

public abstract class BaseInvocationRateLimiterStrategy extends AbstractLimiterStrategy {

    private Logger logger = Logger.getLogger(BaseInvocationRateLimiterStrategy.class);

    private Map<String, List<DateUUID>> invocationHistory = new ConcurrentHashMap<String, List<DateUUID>>();

    private Integer numberOfInvocationsLimitDefault;

    private Integer perTimePeriodSecondsDefault;



    /**
     * Constructs this {@link BaseInvocationRateLimiterStrategy}
     * 
     * @param numberOfInvocationsLimitDefault The number of invocations limit
     * @param perTimePeriodSecondsDefault The time period for the limit
     */
    public BaseInvocationRateLimiterStrategy(Integer numberOfInvocationsLimitDefault, Integer perTimePeriodSecondsDefault) {
        this.numberOfInvocationsLimitDefault = numberOfInvocationsLimitDefault;
        this.perTimePeriodSecondsDefault = perTimePeriodSecondsDefault;
    }



    /**
     * {@inheritDoc}
     */
    public LimiterStrategyConclusion hasLimitBeenExceededChain(String methodGroup, UUID invocationUUID, Object[] args, boolean charged) {

        boolean callNextInChain = false;

        Integer numberOfInvocationsLimit = getNumberOfInvocationsLimit(methodGroup, args);
        Integer perTimePeriodSeconds = getPerTimePeriodSeconds(methodGroup, args);

        logger.trace("Running hasLimitBeenExceededChain of " + this.getClass().getName());

        String historyKey = getHistoryKey(methodGroup, invocationUUID, args);

        List<DateUUID> dateUuids = null;

        // find cutoff date, anything found after this date will be counted towards the limit
        Date cutoffDate = new Date(new Date().getTime() - (perTimePeriodSeconds * 1000));


        synchronized (this) {

            dateUuids = invocationHistory.get(historyKey);
            if (dateUuids == null) {
                dateUuids = Collections.synchronizedList(new ArrayList<DateUUID>());
                invocationHistory.put(historyKey, dateUuids);
            }

            List<DateUUID> cleanUpList = new ArrayList<DateUUID>();

            int currentInvocationCount = 0;

            // since the most recent entries are at the end of the list and the older entries shift
            // automatically towards the beginning
            // we only look for the expired entries at the front only
            for (DateUUID current : dateUuids) {
                if (current.getDate().before(cutoffDate)) {
                    // build a list of entries that are too old to keep in the invocation history
                    cleanUpList.add(current);
                }
                else {
                    break;
                }
            }

            // remove items in the invocation history old enough that won't be needed anymore
            for (DateUUID current : cleanUpList) {
                dateUuids.remove(current);
                logger.debug("Removed from invocation history : " + current);
            }

            currentInvocationCount = dateUuids.size();

            // if limit was not exceeded
            logger.debug("Checking invocation rate limit [key=" + historyKey + "] " + currentInvocationCount + " < " + numberOfInvocationsLimit +
                    " " + (currentInvocationCount < numberOfInvocationsLimit ? "Allowed" : "Blocked"));
            if (currentInvocationCount < numberOfInvocationsLimit) {

                if (charged)
                    dateUuids.add(new DateUUID(new Date(), invocationUUID));
                callNextInChain = true;

            }

        }

        if (callNextInChain) {
            return callNextChainedLimiterStrategy(methodGroup, invocationUUID, args, charged);
        }
        else {
            return buildExceededConclusion(this, methodGroup, invocationUUID, args);
        }

    }



    public LimiterStrategyConclusion hasLimitBeenExceededChain(String methodGroup, UUID invocationUUID, Object[] args) {

        return hasLimitBeenExceededChain(methodGroup, invocationUUID, args, true);

    }



    /**
     * Gets the number of invocations limit, taking into account any property overrides
     * 
     * @param methodGroup The group name of method group.
     * @param args The arguments of the annotated method.
     * @return The number of invocations limit
     */
    public Integer getNumberOfInvocationsLimit(String methodGroup, Object[] args) {

        if (getPropertyOverrideProvider() != null) {
            String override =
                    getPropertyOverrideProvider().getPropertyOverride(this.getClass(), methodGroup, getStrategyGroupKey(args),
                            "numberOfInvocationsLimit");
            if (override != null)
                return Integer.parseInt(override);
        }

        return numberOfInvocationsLimitDefault;
    }



    /**
     * Gets the time period for the limit, taking into account any property overrides
     * 
     * @param methodGroup
     * @param args
     * @return
     */
    public Integer getPerTimePeriodSeconds(String methodGroup, Object[] args) {

        if (getPropertyOverrideProvider() != null) {
            String override =
                    getPropertyOverrideProvider()
                            .getPropertyOverride(this.getClass(), methodGroup, getStrategyGroupKey(args), "perTimePeriodSeconds");
            if (override != null)
                return Integer.parseInt(override);
        }

        return perTimePeriodSecondsDefault;
    }



    /**
     * {@inheritDoc}
     */
    public void rollback(String methodGroup, UUID invocationUUID, Object[] args) {
        List<DateUUID> groupHistory = invocationHistory.get(getHistoryKey(methodGroup, invocationUUID, args));
        groupHistory.remove(new DateUUID(invocationUUID));
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public String getDetailedExceededMessage(String methodGroup, UUID invocationUUID, Object[] args) {

        Integer numberOfInvocationsLimit = getNumberOfInvocationsLimit(methodGroup, args);
        Integer perTimePeriodSeconds = getPerTimePeriodSeconds(methodGroup, args);

        return "Reached limit of " + numberOfInvocationsLimit + " invocations per " +
                (perTimePeriodSeconds > 1 ? perTimePeriodSeconds + " seconds." : "second.");
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public String getGenericExceededMessage(String methodGroup, UUID invocationUUID, Object[] args) {
        return "Reached allowed invocation rate.";
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() {
        invocationHistory.clear();
    }



    /**
     * An object representing the data needed in the invocation history
     * 
     * @author reubena
     * 
     */
    private class DateUUID {

        private UUID uuid;
        private Date date;



        public DateUUID(Date date, UUID uuid) {
            this.date = date;
            this.uuid = uuid;
        }



        public DateUUID(UUID uuid) {
            this.uuid = uuid;
        }



        @SuppressWarnings("unused")
        public UUID getUuid() {
            return uuid;
        }



        @SuppressWarnings("unused")
        public void setUuid(UUID uuid) {
            this.uuid = uuid;
        }



        public Date getDate() {
            return date;
        }



        @SuppressWarnings("unused")
        public void setDate(Date date) {
            this.date = date;
        }



        @Override
        public String toString() {
            return "DateUUID [uuid=" + uuid + ", date=" + date + "]";
        }



        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            DateUUID other = (DateUUID) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (uuid == null) {
                if (other.uuid != null)
                    return false;
            }
            else if (!uuid.equals(other.uuid))
                return false;
            return true;
        }



        private BaseInvocationRateLimiterStrategy getOuterType() {
            return BaseInvocationRateLimiterStrategy.this;
        }

    }

}
