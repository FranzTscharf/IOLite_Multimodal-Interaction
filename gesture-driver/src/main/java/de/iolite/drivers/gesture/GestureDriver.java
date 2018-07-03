/*
 * Copyright (C) 2018 IOLITE GmbH, All rights reserved. Created: 13.06.2018 Created by: lehmann
 */

package de.iolite.drivers.gesture;




import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.drivers.framework.Driver;
import de.iolite.drivers.framework.DriverAPI;
import de.iolite.drivers.framework.device.Device;
import de.iolite.drivers.framework.device.DeviceConfigurationObserver;
import de.iolite.drivers.framework.device.plain.PlainDeviceConfigurationBuilder;
import de.iolite.drivers.framework.device.plain.PlainDeviceFactory;
import de.iolite.drivers.framework.exception.DeviceConfigurationException;
import de.iolite.drivers.framework.exception.DriverStartFailedException;

/**
 * Implementation of Gesture Driver.
 *
 * @author Johannes Hassler
 * @since 03.07.2018
 */
public class GestureDriver implements Driver, DeviceConfigurationObserver {
	
	private SimulatedGestureDevice dev;

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(GestureDriver.class);

	@Override
	public final void onConfigured(@Nonnull final Device device) {
		Objects.requireNonNull(device, "'device' must not be null");
		

		// create Simulated gesture Driver
		dev = new SimulatedGestureDevice(PlainDeviceFactory.create(device));
	}

	@Override
	public final void onRemoved(@Nonnull final String deviceIdentifier) {
		Objects.requireNonNull(deviceIdentifier, "'deviceIdentifier' must not be null");
		// nothing to do yet
	}

	@Override
	@Nonnull
	public final DeviceConfigurationObserver start(@Nonnull final DriverAPI driverAPI, @Nonnull final Set<Device> existingDevices)
			throws DriverStartFailedException {
		Validate.notNull(driverAPI, "'driverAPI' must not be null");
		Validate.notNull(existingDevices, "'existingDevices' must not be null");

		// Report Gesture Ring
		try {
			PlainDeviceConfigurationBuilder deviceBuilder =
					new PlainDeviceConfigurationBuilder(driverAPI.configure("Gesture-Ring",DriverConstants.PROFILE_GestureSensor_ID));
			deviceBuilder.withProperty(DriverConstants.PROFILE_PROPERTY_GestureSensor_recognizedGesture_ID);
			deviceBuilder.fromManufacturer("funny company");
			deviceBuilder.withModelName("Simulated Gesture Ring");
			deviceBuilder.addIfAbsent();
		}
		catch (final DeviceConfigurationException e) {
			LOGGER.error("Failed to report simulated devices", e);
		}

		existingDevices.forEach(this::onConfigured);
		
		//Run Jetty Server
		driverAPI.getScheduler().execute(new StartJetty(dev));
		

		return this;
	}

	@Override
	public final void stop() {
		LOGGER.debug("Stopped '{}'", this.getClass().getSimpleName());
	}
	

}
