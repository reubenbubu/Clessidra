package com.ccbill.clessidra.strategy;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.ccbill.clessidra.interfaces.CostBasedLimiterStrategy;
import com.ccbill.clessidra.strategy.defaults.DefaultCostBasedLimiterStrategy;



/**
 * A base Limiter Strategy implementation that contains all the logic needed for cost based
 * limitation. The only thing missing is the strategy group key and the cost calculation which is
 * implemented in the {@link DefaultCostBasedLimiterStrategy}.
 * 
 * @author reubena
 * 
 */

public abstract class BaseCostBasedLimiterStrategy extends AbstractLimiterStrategy
        implements CostBasedLimiterStrategy {

    private Logger logger = Logger.getLogger(BaseCostBasedLimiterStrategy.class);

    private Map<String, List<DateCostUUID>> invocationCostHistory = new ConcurrentHashMap<String, List<DateCostUUID>>();

    private Integer costLimitDefault;
    private Integer perTimePeriodSecondsDefault;



    /**
     * Constructs this {@link BaseCostBasedLimiterStrategy}
     * 
     * @param costLimitDefault The cost limit
     * @param perTimePeriodSecondsDefault The time period for the limit
     */
    public BaseCostBasedLimiterStrategy(Integer costLimitDefault, Integer perTimePeriodSecondsDefault) {
        this.costLimitDefault = costLimitDefault;
        this.perTimePeriodSecondsDefault = perTimePeriodSecondsDefault;
    }



    /**
     * {@inheritDoc}
     */
    public LimiterStrategyConclusion hasLimitBeenExceededChain(String methodGroup, UUID invocationUUID, Object[] args) {

        Integer costLimit = getCostLimit(methodGroup, args);
        Integer perTimePeriodSeconds = getPerTimePeriodSeconds(methodGroup, args);

        logger.trace("Running hasLimitBeenExceededChain of " + this.getClass().getName());

        String historyKey = getHistoryKey(methodGroup, invocationUUID, args);

        boolean callNextInChain = false;

        List<DateCostUUID> dateCostUuids = null;

        // find cutoff date, anything found after this date will be counted towards the limit
        Date cutoffDate = new Date(new Date().getTime() - (perTimePeriodSeconds * 1000));

        synchronized (this) {

            dateCostUuids = invocationCostHistory.get(historyKey);
            if (dateCostUuids == null) {
                dateCostUuids = Collections.synchronizedList(new ArrayList<DateCostUUID>());
                invocationCostHistory.put(historyKey, dateCostUuids);
            }

            List<DateCostUUID> cleanUpList = new ArrayList<DateCostUUID>();

            int currentTotalInvocationCost = 0;


            for (DateCostUUID currentDateCost : dateCostUuids) {
                if (currentDateCost.getDate().after(cutoffDate)) {
                    currentTotalInvocationCost = currentTotalInvocationCost + currentDateCost.getCost();
                }
                else {
                    // build a list of entries in the invocation history that can be removed
                    cleanUpList.add(currentDateCost);
                }
            }

            // remove items in the invocation history old enough that won't be needed anymore
            for (DateCostUUID currentDateCost : cleanUpList) {
                dateCostUuids.remove(currentDateCost);
                logger.debug("Removed from invocation history : " + currentDateCost.toString());
            }

            // if limit was not exceeded
            logger.debug("Checking cost limit [key=" + historyKey + "] " + currentTotalInvocationCost + " < " + costLimit + " " +
                    (currentTotalInvocationCost < costLimit ? "Allowed" : "Blocked"));
            if (currentTotalInvocationCost < costLimit) {

                dateCostUuids.add(new DateCostUUID(new Date(), calculateCost(args), invocationUUID));
                callNextInChain = true;

            }

        }

        if (callNextInChain) {
            return callNextChainedLimiterStrategy(methodGroup, invocationUUID, args);
        }
        else {
            return buildExceededConclusion(this, methodGroup, invocationUUID, args);
        }

    }



    /**
     * {@inheritDoc}
     */
    public void rollback(String methodGroup, UUID invocationUUID, Object[] args) {
        List<DateCostUUID> groupHistory = invocationCostHistory.get(getHistoryKey(methodGroup, invocationUUID, args));
        groupHistory.remove(new DateCostUUID(invocationUUID));
    }



    /**
     * Gets the cost limit, taking into account any property overrides
     * 
     * @param methodGroup The group name of method group.
     * @param args The arguments of the annotated method.
     * @return The cost limit
     */
    public Integer getCostLimit(String methodGroup, Object[] args) {

        if (getPropertyOverrideProvider() != null) {
            String override = getPropertyOverrideProvider().getPropertyOverride(this.getClass(), methodGroup, getStrategyGroupKey(args), "costLimit");
            if (override != null)
                return Integer.parseInt(override);
        }

        return costLimitDefault;
    }



    /**
     * Get the time period in seconds, taking into account any property overrides
     * 
     * @param methodGroup The group name of method group.
     * @param args The arguments of the annotated method.
     * @return The time period
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
    @Override
    public String getDetailedExceededMessage(String methodGroup, UUID invocationUUID, Object[] args) {

        Integer costLimit = getCostLimit(methodGroup, args);
        Integer perTimePeriodSeconds = getPerTimePeriodSeconds(methodGroup, args);

        return "Reached limit of " + costLimit + " method cost per " + (perTimePeriodSeconds > 1 ? perTimePeriodSeconds + " seconds." : "second.");
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public String getGenericExceededMessage(String methodGroup, UUID invocationUUID, Object[] args) {
        return "Reached allowed method cost rate.";
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() {
        invocationCostHistory.clear();

    }



    /**
     * An object representing the data needed in the invocation history
     * 
     * @author reubena
     * 
     */
    private class DateCostUUID {

        private Date date;
        private Integer cost;
        private UUID uuid;



        @SuppressWarnings("unused")
        public DateCostUUID() {

        }



        public DateCostUUID(UUID uuid) {
            this.uuid = uuid;
        }



        public DateCostUUID(Date date, Integer cost, UUID uuid) {
            this.date = date;
            this.cost = cost;
            this.uuid = uuid;
        }



        public Date getDate() {
            return date;
        }



        @SuppressWarnings("unused")
        public void setDate(Date date) {
            this.date = date;
        }



        public Integer getCost() {
            return cost;
        }



        @SuppressWarnings("unused")
        public void setCost(Integer cost) {
            this.cost = cost;
        }



        @SuppressWarnings("unused")
        public UUID getUuid() {
            return uuid;
        }



        @SuppressWarnings("unused")
        public void setUuid(UUID uuid) {
            this.uuid = uuid;
        }



        @Override
        public String toString() {
            return "DateCostUUID [date=" + date + ", cost=" + cost + ", uuid=" + uuid + "]";
        }



        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            DateCostUUID other = (DateCostUUID) obj;
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



        private BaseCostBasedLimiterStrategy getOuterType() {
            return BaseCostBasedLimiterStrategy.this;
        }

    }

}
