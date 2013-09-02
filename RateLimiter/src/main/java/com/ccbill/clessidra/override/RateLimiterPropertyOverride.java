package com.ccbill.clessidra.override;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("property-override")
public class RateLimiterPropertyOverride {

	public RateLimiterPropertyOverride(String propertyName, String propertyValue) {
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}

	@XStreamAlias("propertyName")
	@XStreamAsAttribute
	private String propertyName;

	@XStreamAlias("propertyValue")
	@XStreamAsAttribute
	private String propertyValue;

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

}
