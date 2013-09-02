package com.ccbill.clessidra.override;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.ccbill.clessidra.interfaces.LimiterStrategy;
import com.ccbill.clessidra.strategy.AbstractLimiterStrategy;
import com.thoughtworks.xstream.XStream;

/**
 * Provides the ability to read an xml file and load the property overrides in a {@link PropertyOverrideProvider} object.
 * 
 * @author reubena
 * 
 */

@Component
public class PropertyOverrideProvider {

	private RateLimiterOverrides rateLimiterOverrides;

	private static Logger logger = Logger.getLogger(PropertyOverrideProvider.class);

	public PropertyOverrideProvider() {

	}

	public PropertyOverrideProvider(RateLimiterOverrides rateLimiterOverrides) {
		this.rateLimiterOverrides = rateLimiterOverrides;
	}

	/**
	 * Creates an instance of {@link PropertyOverrideProvider}
	 * 
	 * @param filePath
	 *            The path to the xml file.
	 * @return The {@link PropertyOverrideProvider}
	 * @throws FileNotFoundException
	 */
	public static PropertyOverrideProvider createInstance(String filePath) throws FileNotFoundException {

		InputStream inputStream = null;

		// assume the filePath is absolute
		File file = new File(filePath);

		// if not found, try looking in the classpath
		if (!file.exists()) {

			inputStream = Object.class.getResourceAsStream(filePath);
			if (inputStream == null)
				inputStream = Object.class.getResourceAsStream("/" + filePath);

		} else {
			inputStream = new FileInputStream(file);
		}

		XStream xs = new XStream();

		xs.processAnnotations(RateLimiterOverrides.class);
		xs.processAnnotations(RateLimiterOverride.class);
		xs.processAnnotations(RateLimiterOverrideMethodGroup.class);
		xs.processAnnotations(RateLimiterOverrideStrategyGroup.class);
		xs.processAnnotations(RateLimiterPropertyOverride.class);
		RateLimiterOverrides overrides = (RateLimiterOverrides) xs.fromXML(inputStream);

		logger.debug("Loaded property overrides\n" + xs.toXML(overrides));

		return new PropertyOverrideProvider(overrides);

	}

	/**
	 * Returns the value of a particular property override.
	 * 
	 * @param limiterStrategy
	 *            The limiter strategy class.
	 * @param methodGroup
	 *            The method group.
	 * @param strategyGroupKey
	 *            The strategy group key.
	 * @param propertyName
	 *            The name of the property.
	 * @return The value of the property override.
	 */
	public String getPropertyOverride(Class<? extends AbstractLimiterStrategy> limiterStrategy, String methodGroup, String strategyGroupKey,
			String propertyName) {

		if (rateLimiterOverrides == null)
			return null;

		// look for most specific override
		for (RateLimiterOverride currentOverride : rateLimiterOverrides.getRateLimiterOverrides()) {
			if (currentOverride.getStrategyClassName().equals(limiterStrategy.getCanonicalName())) {
				for (RateLimiterOverrideMethodGroup currentMethodGroup : currentOverride.getRateLimiterOverrideMethodGroups()) {
					if (currentMethodGroup.getMethodGroupName().equals(methodGroup)) {
						for (RateLimiterOverrideStrategyGroup currentStrategyGroup : currentMethodGroup.getRateLimiterOverrideStrategyGroups()) {
							if (currentStrategyGroup.getStrategyGroupKey().equals(strategyGroupKey)) {
								for (RateLimiterPropertyOverride currentProperty : currentStrategyGroup.getPropertyOverrides()) {
									if (currentProperty.getPropertyName().equals(propertyName)) {
										return currentProperty.getPropertyValue();
									}
								}
							}
						}
					}
				}
			}
		}

		// try to match strategy group key with wildcards
		for (RateLimiterOverride currentOverride : rateLimiterOverrides.getRateLimiterOverrides()) {
			if (currentOverride.getStrategyClassName().equals(limiterStrategy.getCanonicalName())) {
				for (RateLimiterOverrideMethodGroup currentMethodGroup : currentOverride.getRateLimiterOverrideMethodGroups()) {
					if (currentMethodGroup.getMethodGroupName().equals(methodGroup)) {
						for (RateLimiterOverrideStrategyGroup currentStrategyGroup : currentMethodGroup.getRateLimiterOverrideStrategyGroups()) {
							// do wild card match on strategy group key
							if (wildCardMatch(strategyGroupKey, currentStrategyGroup.getStrategyGroupKey())) {
								for (RateLimiterPropertyOverride currentProperty : currentStrategyGroup.getPropertyOverrides()) {
									if (currentProperty.getPropertyName().equals(propertyName)) {
										return currentProperty.getPropertyValue();
									}
								}
							}
						}
					}
				}
			}
		}

		// try to match method group with wildcards
		for (RateLimiterOverride currentOverride : rateLimiterOverrides.getRateLimiterOverrides()) {
			if (currentOverride.getStrategyClassName().equals(limiterStrategy.getCanonicalName())) {
				for (RateLimiterOverrideMethodGroup currentMethodGroup : currentOverride.getRateLimiterOverrideMethodGroups()) {
					// do wild card match on method group
					if (wildCardMatch(methodGroup, currentMethodGroup.getMethodGroupName())) {
						for (RateLimiterOverrideStrategyGroup currentStrategyGroup : currentMethodGroup.getRateLimiterOverrideStrategyGroups()) {
							if (currentStrategyGroup.getStrategyGroupKey().equals(strategyGroupKey)) {
								for (RateLimiterPropertyOverride currentProperty : currentStrategyGroup.getPropertyOverrides()) {
									if (currentProperty.getPropertyName().equals(propertyName)) {
										return currentProperty.getPropertyValue();
									}
								}
							}
						}
					}
				}
			}
		}

		// try to match both strategy group and method group with wildcards
		for (RateLimiterOverride currentOverride : rateLimiterOverrides.getRateLimiterOverrides()) {
			if (currentOverride.getStrategyClassName().equals(limiterStrategy.getCanonicalName())) {
				for (RateLimiterOverrideMethodGroup currentMethodGroup : currentOverride.getRateLimiterOverrideMethodGroups()) {
					// do wild card match on method group
					if (wildCardMatch(methodGroup, currentMethodGroup.getMethodGroupName())) {
						for (RateLimiterOverrideStrategyGroup currentStrategyGroup : currentMethodGroup.getRateLimiterOverrideStrategyGroups()) {
							// do wild card match on strategy group key
							if (wildCardMatch(strategyGroupKey, currentStrategyGroup.getStrategyGroupKey())) {
								for (RateLimiterPropertyOverride currentProperty : currentStrategyGroup.getPropertyOverrides()) {
									if (currentProperty.getPropertyName().equals(propertyName)) {
										return currentProperty.getPropertyValue();
									}
								}
							}
						}
					}
				}
			}
		}

		return null;

	}

	/**
	 * Returns a list of {@link RateLimiterOverride} for a particular {@link LimiterStrategy}
	 * 
	 * @param limiterStrategy
	 *            The {@link LimiterStrategy} class.
	 * @return A list of overrides.
	 */
	public List<RateLimiterOverride> getStrategyOverrides(Class<? extends LimiterStrategy> limiterStrategy) {

		if (rateLimiterOverrides == null)
			return null;

		List<RateLimiterOverride> strategyOverrides = null;

		for (RateLimiterOverride currentOverride : rateLimiterOverrides.getRateLimiterOverrides()) {
			if (currentOverride.getStrategyClassName().equals(limiterStrategy.getClass().getName())) {
				if (strategyOverrides == null)
					strategyOverrides = new ArrayList<RateLimiterOverride>();

				strategyOverrides.add(currentOverride);
			}
		}

		return strategyOverrides;

	}

	private boolean wildCardMatch(String sourceText, String pattern) {

		String[] patternSplit = pattern.split("\\*");

		String text = sourceText;

		for (String card : patternSplit) {
			int index = text.indexOf(card);
			if (index == -1) {
				return false;
			}

			text = text.substring(index + card.length());
		}

		return true;

	}

}
