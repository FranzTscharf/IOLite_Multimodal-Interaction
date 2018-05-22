/* Copyright (C) 2016 IOLITE GmbH, All rights reserved.
 * Created:    16.11.2016
 * Created by: lehmann
 */

package de.iolite.drivers.example;

import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.drivers.framework.DataPoint;
import de.iolite.drivers.framework.DataPointConfiguration;
import de.iolite.drivers.framework.DataPointFactory;
import de.iolite.drivers.framework.DataPointValueCallback;
import de.iolite.drivers.framework.exception.DataPointConfigurationException;
import de.iolite.drivers.framework.exception.DataPointInstantiationException;
import de.iolite.drivers.framework.exception.IllegalValueException;
import de.iolite.utilities.concurrency.scheduler.Scheduler;

/**
 * Datapoint for sensors double properties with randomized values.
 *
 * @author Jonathan Gruber
 * @since 17.09
 */
final class DoubleSensorDataPointFactory implements DataPointFactory {

	private static final class DoubleSensorDataPoint implements DataPoint {

		private DoubleSensorDataPoint(final double initialValue, @Nonnull final DataPointValueCallback callback)
				throws DataPointConfigurationException {
			try {
				callback.newDoubleValue(initialValue);
			}
			catch (final IllegalValueException e) {
				throw new DataPointConfigurationException("Initial value is illegal", e);
			}
		}

		@Override
		public void destroy() {
			// nothing to do
		}
	}

	private static final class DoubleSensorDataPointWithRandomization implements DataPoint {

		private final double min;

		private final double max;

		@Nonnull
		private final Future<?> randomizationTask;

		@Nonnull
		private final Random random = new Random();

		@Nonnull
		private final DataPointValueCallback callback;

		private double lastValue;

		private DoubleSensorDataPointWithRandomization(final double initialValue, final double minValue, final double maxValue,
				@Nonnull final DataPointValueCallback dataPointValueCallback, @Nonnull final Scheduler taskScheduler) {
			this.callback = dataPointValueCallback;
			this.min = minValue;
			this.max = maxValue;
			this.lastValue = initialValue;
			this.randomizationTask = taskScheduler.scheduleWithFixedDelay(this::reportRandomValue, 0, 10, TimeUnit.SECONDS);
		}

		@Override
		public void destroy() {
			this.randomizationTask.cancel(true);
		}

		private void reportRandomValue() {
			final double newRandom = this.lastValue + (this.random.nextDouble() * 2 - 1);
			this.lastValue = Math.min(Math.max(newRandom, this.min), this.max);

			try {
				this.callback.newDoubleValue(this.lastValue);
			}
			catch (final IllegalValueException e) {
				LOGGER.error("Failed to report random value '{}'", this.lastValue, e.getMessage());
			}
		}
	}

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(DoubleSensorDataPointFactory.class);

	@Nonnull
	private final Scheduler scheduler;

	/**
	 * Constructor of PowerUsageDataPointFactory.
	 *
	 * @param dataPointScheduler scheduler for this driver.
	 */
	DoubleSensorDataPointFactory(@Nonnull final Scheduler dataPointScheduler) {
		this.scheduler = Validate.notNull(dataPointScheduler, "'dataPointScheduler' must not be null");
	}

	@Override
	@Nonnull
	public DataPoint create(@Nonnull final DataPointConfiguration configuration, @Nonnull final String propertyTypeIdentifier,
			@Nonnull final DataPointValueCallback callback)
			throws DataPointConfigurationException, DataPointInstantiationException {
		Validate.notNull(configuration, "'configuration' must not be null");
		Validate.notNull(propertyTypeIdentifier, "'propertyTypeIdentifier' must not be null");
		Validate.notNull(callback, "'callback' must not be null");
		final double initialValue = configuration.getDouble(ExampleDriver.CONFIGURATION_INITIAL_VALUE);
		final double minValue = configuration.getDouble(ExampleDriver.CONFIGURATION_MIN_VALUE);
		final double maxValue = configuration.getDouble(ExampleDriver.CONFIGURATION_MAX_VALUE);

		return new DoubleSensorDataPointWithRandomization(initialValue, minValue, maxValue, callback, this.scheduler);
	}
}
