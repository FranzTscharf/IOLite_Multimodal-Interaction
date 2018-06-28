/*
 * Copyright (C) 2018 IOLITE GmbH, All rights reserved. Created: 13.06.2018 Created by: lehmann
 */

package de.iolite.drivers.gesture;

import static de.iolite.drivers.basic.DriverConstants.PROFILE_DimmableLamp_ID;
import static de.iolite.drivers.basic.DriverConstants.PROFILE_PROPERTY_DimmableLamp_dimmingLevel_ID;
import static de.iolite.drivers.basic.DriverConstants.PROFILE_PROPERTY_DimmableLamp_on_ID;
import static de.iolite.drivers.basic.DriverConstants.PROFILE_PROPERTY_DimmableLamp_powerUsage_ID;
import static de.iolite.drivers.basic.DriverConstants.DeviceStatus.Configuration_Error;
import static de.iolite.drivers.basic.HasProfileIdentifier.DimmableLamp;

import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.drivers.framework.Driver;
import de.iolite.drivers.framework.DriverAPI;
import de.iolite.drivers.framework.device.Device;
import de.iolite.drivers.framework.device.DeviceConfigurationObserver;
import de.iolite.drivers.framework.device.plain.PlainDevice;
import de.iolite.drivers.framework.device.plain.PlainDeviceConfigurationBuilder;
import de.iolite.drivers.framework.device.plain.PlainDeviceFactory;
import de.iolite.drivers.framework.exception.DeviceConfigurationException;
import de.iolite.drivers.framework.exception.DriverStartFailedException;

/**
 * Demonstrates the implementation of a lamp driver.
 *
 * @author Grzegorz Lehmann
 * @since 18.06
 */
public class GestureDriver implements Driver, DeviceConfigurationObserver {
	
	private PlainDevice dev;

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(GestureDriver.class);

	@Override
	public final void onConfigured(@Nonnull final Device device) {
		Objects.requireNonNull(device, "'device' must not be null");

		if (!DimmableLamp.test(device)) {
			// not a lamp, reject the device
			LOGGER.error("Unsupported device type '{}'", device);
			device.error(Configuration_Error);
			return;
		}

		// create simulated lamp
		dev = PlainDeviceFactory.create(device);
		new SimulatedGestureDevice(dev);
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
					new PlainDeviceConfigurationBuilder(driverAPI.configure("Gesture-Ring", PROFILE_DimmableLamp_ID));
			deviceBuilder.withProperty(PROFILE_PROPERTY_DimmableLamp_on_ID);
			deviceBuilder.withProperty(PROFILE_PROPERTY_DimmableLamp_dimmingLevel_ID);
			deviceBuilder.withProperty(PROFILE_PROPERTY_DimmableLamp_powerUsage_ID);
			deviceBuilder.fromManufacturer("funny company");
			deviceBuilder.withModelName("Simulated Gesture Ring");
			deviceBuilder.addIfAbsent();
		}
		catch (final DeviceConfigurationException e) {
			LOGGER.error("Failed to report simulated devices", e);
		}

		existingDevices.forEach(this::onConfigured);
		
		//Run Jetty Server
		//StartJetty jetty = new StartJetty(dev);
		
		driverAPI.getScheduler().execute(new StartJetty(dev));
		

		return this;
	}

	@Override
	public final void stop() {
		LOGGER.debug("Stopped '{}'", this.getClass().getSimpleName());
	}
	

}
