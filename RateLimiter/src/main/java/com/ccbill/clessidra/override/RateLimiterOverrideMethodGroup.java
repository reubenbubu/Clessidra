package com.ccbill.clessidra.override;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("method-group")
public class RateLimiterOverrideMethodGroup {

	public RateLimiterOverrideMethodGroup(String methodGroupName) {
		this.methodGroupName = methodGroupName;
	}

	@XStreamAlias("group-name")
	@XStreamAsAttribute
	private String methodGroupName;

	@XStreamAlias("strategy-groups")
	@XStreamImplicit
	private List<RateLimiterOverrideStrategyGroup> rateLimiterOverrideStrategyGroups = new ArrayList<RateLimiterOverrideStrategyGroup>();

	public String getMethodGroupName() {
		return methodGroupName;
	}

	public void setMethodGroupName(String methodGroupName) {
		this.methodGroupName = methodGroupName;
	}

	public List<RateLimiterOverrideStrategyGroup> getRateLimiterOverrideStrategyGroups() {
		return rateLimiterOverrideStrategyGroups;
	}

	public void setRateLimiterOverrideStrategyGroups(List<RateLimiterOverrideStrategyGroup> rateLimiterOverrideStrategyGroups) {
		this.rateLimiterOverrideStrategyGroups = rateLimiterOverrideStrategyGroups;
	}

}
