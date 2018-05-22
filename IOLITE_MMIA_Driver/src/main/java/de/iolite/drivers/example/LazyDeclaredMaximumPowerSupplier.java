/* Copyright (C) 2017 IOLITE GmbH, All rights reserved.
 * Created:    27.10.2017
 * Created by: lehmann
 */

package de.iolite.drivers.example;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import de.iolite.drivers.basic.DriverConstants;
import de.iolite.drivers.framework.device.AttributeVisitor;
import de.iolite.drivers.framework.device.Device;

/**
 * Returns the {@link DriverConstants#ATTRIBUTE_ElectricalDevice_declaredMaximumPower_ID} attribute value of a device.
 *
 * @author Grzegorz Lehmann
 * @since 17.10
 */
class LazyDeclaredMaximumPowerSupplier implements Supplier<Optional<Double>> {

	private static final class DeclaredMaximumPowerVisitor implements AttributeVisitor {

		@Nonnull
		private Optional<Double> attributeValue = Optional.empty();

		@Override
		public void visit(@Nonnull final String attributeIdentifier, final double value) {
			if (!DriverConstants.ATTRIBUTE_ElectricalDevice_declaredMaximumPower_ID.equals(attributeIdentifier)) {
				return;
			}
			this.attributeValue = Optional.of(value);
		}
	}

	@Nonnull
	private final Device device;

	/**
	 * Instantiates a new supplier for the given device.
	 *
	 * @param deviceToRead device to read the attribute value of
	 * @throws NullPointerException if {@code deviceToRead} is {@code null}
	 */
	LazyDeclaredMaximumPowerSupplier(@Nonnull final Device deviceToRead) {
		this.device = Objects.requireNonNull(deviceToRead, "'deviceToRead' must not be null");
	}

	@Nonnull
	@Override
	public final Optional<Double> get() {
		final DeclaredMaximumPowerVisitor visitor = new DeclaredMaximumPowerVisitor();
		this.device.accept(visitor);
		return visitor.attributeValue;
	}

	@Override
	public final String toString() {
		return this.getClass().getSimpleName();
	}
}
