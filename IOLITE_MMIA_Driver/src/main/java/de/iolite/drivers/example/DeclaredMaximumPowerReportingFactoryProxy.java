/* Copyright (C) 2016 IOLITE GmbH, All rights reserved.
 * Created:    16.11.2016
 * Created by: lehmann
 */

package de.iolite.drivers.example;

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.drivers.basic.DriverConstants;
import de.iolite.drivers.framework.DataPoint;
import de.iolite.drivers.framework.DataPointConfiguration;
import de.iolite.drivers.framework.DataPointFactory;
import de.iolite.drivers.framework.DataPointValueCallback;
import de.iolite.drivers.framework.exception.DataPointConfigurationException;
import de.iolite.drivers.framework.exception.DataPointInstantiationException;
import de.iolite.drivers.framework.exception.IllegalValueException;

/**
 * Proxy creating the estimated PowerUsage DataPoint depending of on property and declaredMaximumPower attribute.
 *
 * @author Niels Johannes
 * @author Jonathan Gruber
 * @author Grzegorz Lehmann
 * @since 17.11
 */
final class DeclaredMaximumPowerReportingFactoryProxy implements DataPointFactory {

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(DeclaredMaximumPowerReportingFactoryProxy.class);

	@Nonnull
	private final DataPointFactory delegate;

	@Nullable
	private volatile DataPointValueCallback powerUsageCallback = null;

	@Nonnull
	private final PowerUsageEstimateFunction powerUsageEstimate;

	/**
	 * Construct the factory proxy to bind.
	 *
	 * @param delegateFactory factory to use for delegation
	 * @param powerValueSupplier supplies {@code DeclaredMaximumPower} attribute value or an empty optional if not set
	 */
	DeclaredMaximumPowerReportingFactoryProxy(@Nonnull final DataPointFactory delegateFactory, @Nonnull final Supplier<Optional<Double>> powerValueSupplier) {
		Validate.notNull(delegateFactory, "'delegateFactory' must not be null");
		Validate.notNull(powerValueSupplier, "'powerValueSupplier' must not be null");
		this.delegate = delegateFactory;
		this.powerUsageEstimate = new PowerUsageEstimateFunction(powerValueSupplier, this::reportNewPowerUsageEstimate);
	}

	private void reportNewPowerUsageEstimate(final double value) {
		final DataPointValueCallback callback = this.powerUsageCallback;
		if (callback == null) {
			LOGGER.debug("No power usage callback to report new estimate value '{}'", value);
			return;
		}
		try {
			callback.newDoubleValue(value);
			LOGGER.trace("Reported new power usage estimate value '{}'", value);
		}
		catch (final IllegalValueException e) {
			LOGGER.warn("Failed to report power usage estimate '{}' due to error: '{}'", value, e.getMessage());
		}
	}

	@Nonnull
	@Override
	public DataPoint create(@Nonnull final DataPointConfiguration configuration, @Nonnull final String propertyTypeIdentifier,
			@Nonnull final DataPointValueCallback callback)
			throws DataPointConfigurationException, DataPointInstantiationException {
		Validate.notNull(configuration, "'configuration' must not be null");
		Validate.notNull(propertyTypeIdentifier, "'propertyTypeIdentifier' must not be null");
		Validate.notNull(callback, "'callback' must not be null");

		if (propertyTypeIdentifier.equals(DriverConstants.PROPERTY_powerUsage_ID)) {
			if (!configuration.optBoolean(ExampleDriver.CONFIGURATION_ESTIMATE_POWER).isPresent()
					|| !configuration.optBoolean(ExampleDriver.CONFIGURATION_ESTIMATE_POWER).get()) {
				LOGGER.debug("Property configuration '{}' is not present for property '{}', configuration: {}.", ExampleDriver.CONFIGURATION_ESTIMATE_POWER,
						propertyTypeIdentifier, configuration);
				return this.delegate.create(configuration, propertyTypeIdentifier, callback);
			}
			LOGGER.debug("Property configuration '{}' is present for property '{}', configuration: {}.", ExampleDriver.CONFIGURATION_ESTIMATE_POWER,
					propertyTypeIdentifier, configuration);
			this.powerUsageCallback = callback;
			// non-writable data point only needs destroy function
			return this::dereferencePowerUsageCallback;
		}
		if (propertyTypeIdentifier.equals(DriverConstants.PROPERTY_on_ID)) {
			final BooleanValueForkingCallbackDecorator decorator = new BooleanValueForkingCallbackDecorator(callback, this.powerUsageEstimate::newOnValue);
			return this.delegate.create(configuration, propertyTypeIdentifier, decorator);
		}
		if (propertyTypeIdentifier.equals(DriverConstants.PROPERTY_dimmingLevel_ID)) {
			final NumericalValueForkingCallbackDecorator decorator = new NumericalValueForkingCallbackDecorator(callback, this.powerUsageEstimate::newDimmingLevel);
			return this.delegate.create(configuration, propertyTypeIdentifier, decorator);
		}
		return this.delegate.create(configuration, propertyTypeIdentifier, callback);
	}

	@Override
	public String toString() {
		return String.format("%s[delegate='%s']", getClass().getSimpleName(), this.delegate);
	}

	private void dereferencePowerUsageCallback() {
		this.powerUsageCallback = null;
	}
}
