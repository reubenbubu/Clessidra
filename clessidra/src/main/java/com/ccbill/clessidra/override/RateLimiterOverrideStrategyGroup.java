package com.ccbill.clessidra.override;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("strategy-group")
public class RateLimiterOverrideStrategyGroup {

	public RateLimiterOverrideStrategyGroup(String strategyGroupKey) {
		this.strategyGroupKey = strategyGroupKey;
	}

	@XStreamAlias("group-key")
	@XStreamAsAttribute
	private String strategyGroupKey;

	@XStreamAlias("property-overrides")
	@XStreamImplicit
	private List<RateLimiterPropertyOverride> propertyOverrides = new ArrayList<RateLimiterPropertyOverride>();

	public List<RateLimiterPropertyOverride> getPropertyOverrides() {
		return propertyOverrides;
	}

	public void setPropertyOverrides(List<RateLimiterPropertyOverride> propertyOverrides) {
		this.propertyOverrides = propertyOverrides;
	}

	public String getStrategyGroupKey() {
		return strategyGroupKey;
	}

	public void setStrategyGroupKey(String strategyGroupKey) {
		this.strategyGroupKey = strategyGroupKey;
	}

}
