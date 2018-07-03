/* Copyright (C) 2018 IOLITE GmbH, All rights reserved.
 * Created:    13.06.2018
 * Created by: smarqoo
 */

package de.iolite.drivers.gesture;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.drivers.api.WriteFailedException;
import de.iolite.drivers.framework.device.plain.PlainDevice;
import de.iolite.drivers.framework.device.property.PropertyValue;
import de.iolite.drivers.framework.device.property.PropertyValueWriter;

/**
 * Simulates the GestureDevice.
 *
 * @author Johannes Hassler
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
	}

	@Override
	public final String toString() {
		return String.format("%s[device='%s']", getClass().getSimpleName(), this.device);
	}


	public final void setRecognizedGesture(String gesture)
	{
		this.device.notifyValue(DriverConstants.PROPERTY_recognizedGesture_ID, gesture);
		LOGGER.debug("Recognized gesture set to: " + gesture, this.device.getIdentifier());
	}

	@Override
	public void write(PropertyValue... arg0) throws WriteFailedException {
		// TODO Auto-generated method stub
		
	}
}