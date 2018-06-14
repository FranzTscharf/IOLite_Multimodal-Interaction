/* Copyright (C) 2017 IOLITE GmbH, All rights reserved.
 * Created:    10.11.2017
 * Created by: lehmann
 */

package de.iolite.drivers.example;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calculates a power usage estimate value based on the state of the 'on' and 'dimming level' properties. The property values are reported with
 * {@link #newOnValue(boolean)} and {@link #newDimmingLevel(double)}. The declared maximum power usage needs to be provided in order for the function to work.
 *
 * @author Grzegorz Lehmann
 * @since 17.11
 */
class PowerUsageEstimateFunction {

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(PowerUsageEstimateFunction.class);

	@Nonnull
	private final Supplier<Optional<Double>> maximumPowerSupplier;

	@Nonnull
	private final Consumer<Double> powerUsageConsumer;

	@Nullable
	private Boolean lastOnValue;

	@Nullable
	private Double lastDimmingValue;

	/**
	 * Instantiates this function.
	 *
	 * @param declaredMaximumPowerSupplier provides the declared maximum power value, retrieved lazily on each calculation
	 * @param powerUsageEstimateConsumer receives the calculated power usage estimate values
	 * @throws NullPointerException if any argument is {@code null}
	 */
	PowerUsageEstimateFunction(@Nonnull final Supplier<Optional<Double>> declaredMaximumPowerSupplier,
			@Nonnull final Consumer<Double> powerUsageEstimateConsumer) {
		this.maximumPowerSupplier = Objects.requireNonNull(declaredMaximumPowerSupplier, "'declaredMaximumPowerSupplier' must not be null");
		this.powerUsageConsumer = Objects.requireNonNull(powerUsageEstimateConsumer, "'powerUsageEstimateConsumer' must not be null");
	}

	@Override
	public String toString() {
		return String.format("%s[lastOnValue='%s',lastDimmingValue='%s',powerUsageConsumer='%s']", getClass().getSimpleName(), this.lastOnValue,
				this.lastDimmingValue, this.powerUsageConsumer);
	}

	void newDimmingLevel(final double value) {
		this.lastDimmingValue = value;
		LOGGER.trace("Got new dimming value '{}'", value);

		reportPowerUsage();
	}

	void newOnValue(final boolean value) {
		this.lastOnValue = value;
		LOGGER.trace("Got new on/off value '{}'", value);

		reportPowerUsage();
	}

	private synchronized OptionalDouble getPowerUsageEstimate() {
		final Boolean on = this.lastOnValue;
		if (on == null) {
			return OptionalDouble.empty();
		}
		if (!on) {
			return OptionalDouble.of(0.);
		}
		final Optional<Double> maximumPower = this.maximumPowerSupplier.get();
		if (!maximumPower.isPresent()) {
			return OptionalDouble.empty();
		}
		final Double dimmingLevel = this.lastDimmingValue;
		return OptionalDouble.of(dimmingLevel == null ? maximumPower.get() : maximumPower.get() * dimmingLevel / 100);
	}

	private void reportPowerUsage() {
		final OptionalDouble powerUsageEstimate = getPowerUsageEstimate();
		if (!powerUsageEstimate.isPresent()) {
			LOGGER.trace("Failed to calculate power usage estimate");
			return;
		}
		final double powerUsage = powerUsageEstimate.getAsDouble();
		this.powerUsageConsumer.accept(powerUsage);
		LOGGER.trace("Reported estimated power usage '{}' to '{}'", powerUsage, this.powerUsageConsumer);
	}
}
