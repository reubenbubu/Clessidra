package com.ccbill.clessidra.override;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("rate-limiter-override")
public class RateLimiterOverride {

	public RateLimiterOverride(String strategyClassName) {
		this.strategyClassName = strategyClassName;
	}

	@XStreamAlias("strategy-class-name")
	@XStreamAsAttribute
	private String strategyClassName;

	@XStreamAlias("method-groups")
	@XStreamImplicit
	private List<RateLimiterOverrideMethodGroup> rateLimiterOverrideMethodGroups = new ArrayList<RateLimiterOverrideMethodGroup>();

	public String getStrategyClassName() {
		return strategyClassName;
	}

	public void setStrategyClassName(String strategyClassName) {
		this.strategyClassName = strategyClassName;
	}

	public List<RateLimiterOverrideMethodGroup> getRateLimiterOverrideMethodGroups() {
		return rateLimiterOverrideMethodGroups;
	}

	public void setRateLimiterOverrideMethodGroups(
			List<RateLimiterOverrideMethodGroup> rateLimiterOverrideMethodGroups) {
		this.rateLimiterOverrideMethodGroups = rateLimiterOverrideMethodGroups;
	}

}
