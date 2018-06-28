/* Copyright (C) 2018 IOLITE GmbH, All rights reserved.
 * Created:    13.06.2018
 * Created by: smarqoo
 */

package de.iolite.drivers.gesture;

import static de.iolite.drivers.basic.DriverConstants.PROPERTY_dimmingLevel_ID;
import static de.iolite.drivers.basic.DriverConstants.PROPERTY_on_ID;
import static de.iolite.drivers.basic.DriverConstants.PROPERTY_powerUsage_ID;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.drivers.api.WriteFailedException;
import de.iolite.drivers.framework.device.plain.PlainDevice;
import de.iolite.drivers.framework.device.property.PropertyValue;
import de.iolite.drivers.framework.device.property.PropertyValueWriter;

/**
 * Simulates a lamp.
 *
 * @author Grzegorz Lehmann
 * @since 18.06
 */
class SimulatedGestureDevice implements PropertyValueWriter {

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(SimulatedGestureDevice.class);

	@Nonnull
	private final PlainDevice device;

	SimulatedGestureDevice(@Nonnull final PlainDevice plainDevice) {
		this.device = Objects.requireNonNull(plainDevice, "'plainDevice' must not be null");
		this.device.start(this);
		turnOff();
	}

	@Override
	public final String toString() {
		return String.format("%s[device='%s']", getClass().getSimpleName(), this.device);
	}

	@Override
	public final void write(@Nonnull final PropertyValue... values)
			throws WriteFailedException {
		for (final PropertyValue value : values) {
			if (!PROPERTY_on_ID.equals(value.getPropertyIdentifier())) {
				throw new WriteFailedException(String.format("Unsupported property to write '%s'", value));
			}
			if (value.isTrue()) {
				turnOn();
			}
			else {
				turnOff();
			}
		}
	}

	private final void turnOff() {
		// turned off
		this.device.notifyValue(PROPERTY_on_ID, "false");
		// dimming level is 0%
		this.device.notifyValue(PROPERTY_dimmingLevel_ID, "0");
		// no power used when off
		this.device.notifyValue(PROPERTY_powerUsage_ID, "0");
		LOGGER.debug("Turned off '{}'", this.device.getIdentifier());
	}

	private final void turnOn() {
		// turned on
		this.device.notifyValue(PROPERTY_on_ID, "true");
		// 100% dimming level
		this.device.notifyValue(PROPERTY_dimmingLevel_ID, "100");
		// simulate using 20 Watts
		this.device.notifyValue(PROPERTY_powerUsage_ID, "20");
		LOGGER.debug("Turned on '{}'", this.device.getIdentifier());
	}
}
