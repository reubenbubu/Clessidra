package com.ccbill.clessidra.override;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@Component
@XStreamAlias("rate-limiter-overrides")
public class RateLimiterOverrides {

	@XStreamImplicit
	private List<RateLimiterOverride> rateLimiterOverrides = new ArrayList<RateLimiterOverride>();

	public List<RateLimiterOverride> getRateLimiterOverrides() {
		return rateLimiterOverrides;
	}

	public void setRateLimiterOverrides(List<RateLimiterOverride> rateLimiterOverrides) {
		this.rateLimiterOverrides = rateLimiterOverrides;
	}

}
