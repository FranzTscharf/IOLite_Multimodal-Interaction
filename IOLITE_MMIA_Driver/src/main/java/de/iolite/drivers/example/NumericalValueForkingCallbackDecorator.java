/* Copyright (C) 2016 IOLITE GmbH, All rights reserved.
 * Created:    16.11.2016
 * Created by: lehmann
 */

package de.iolite.drivers.example;

import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.drivers.framework.DataPointValueCallback;
import de.iolite.drivers.framework.exception.IllegalValueException;

/**
 * Callback decorator. Reports numerical values to the consumer.
 *
 * @author Niels Johannes
 * @since 17.11
 */
final class NumericalValueForkingCallbackDecorator implements DataPointValueCallback {

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(NumericalValueForkingCallbackDecorator.class);

	@Nonnull
	private final DataPointValueCallback delegate;

	@Nonnull
	private final Consumer<Double> forkedConsumer;

	/**
	 * Construct the {@code DataPointValueCallbackDecorator}.
	 *
	 * @param dataPointDelegate the decorated data point
	 * @param valueConsumer to report double values to
	 * @throws NullPointerException if any argument is {@code null}
	 */
	NumericalValueForkingCallbackDecorator(@Nonnull final DataPointValueCallback dataPointDelegate, @Nonnull final Consumer<Double> valueConsumer) {
		this.delegate = Objects.requireNonNull(dataPointDelegate, "'dataPointDelegate' must not be null");
		this.forkedConsumer = Objects.requireNonNull(valueConsumer, "'valueConsumer' must not be null");
	}

	@Override
	public void newBooleanValue(final boolean value)
			throws IllegalValueException {
		this.delegate.newBooleanValue(value);
	}

	@Override
	public void newBooleanValue(final boolean value, final long timestamp)
			throws IllegalValueException {
		this.delegate.newBooleanValue(value, timestamp);
	}

	@Override
	public void newDoubleValue(final double value)
			throws IllegalValueException {
		this.delegate.newDoubleValue(value);
		this.forkedConsumer.accept(value);
		LOGGER.trace("New value '{}' sent to consumer '{}'", value, this.forkedConsumer);
	}

	@Override
	public void newDoubleValue(final double value, final long timestamp)
			throws IllegalValueException {
		this.delegate.newDoubleValue(value, timestamp);
		this.forkedConsumer.accept(value);
		LOGGER.trace("New value '{}' with timestamp '{}' sent to consumer '{}'", value, timestamp, this.forkedConsumer);
	}

	@Override
	public void newIntValue(final int value)
			throws IllegalValueException {
		this.delegate.newIntValue(value);
		this.forkedConsumer.accept((double) value);
		LOGGER.trace("New value '{}' sent to consumer '{}'", value, this.forkedConsumer);
	}

	@Override
	public void newIntValue(final int value, final long timestamp)
			throws IllegalValueException {
		this.delegate.newIntValue(value, timestamp);
		this.forkedConsumer.accept((double) value);
		LOGGER.trace("New value '{}' with timestamp '{}' sent to consumer '{}'", value, timestamp, this.forkedConsumer);
	}

	@Override
	public void newStringValue(@Nonnull final String value)
			throws IllegalValueException {
		this.delegate.newStringValue(value);
	}

	@Override
	public void newStringValue(@Nonnull final String value, final long timestamp)
			throws IllegalValueException {
		Validate.notNull(value, "value must not be null");
		this.delegate.newStringValue(value, timestamp);
	}

	@Override
	public String toString() {
		return String.format("%s[delegate='%s',forkedConsumer='%s']", getClass().getSimpleName(), this.delegate, this.forkedConsumer);
	}
}
