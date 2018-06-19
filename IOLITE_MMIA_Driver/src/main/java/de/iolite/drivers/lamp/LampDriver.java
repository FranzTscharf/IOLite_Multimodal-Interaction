/*
 * Copyright (C) 2018 IOLITE GmbH, All rights reserved. Created: 13.06.2018 Created by: lehmann
 */

package de.iolite.drivers.lamp;

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

import de.iolite.drivers.basic.DriverConstants;
import de.iolite.drivers.framework.Driver;
import de.iolite.drivers.framework.DriverAPI;
import de.iolite.drivers.framework.device.Device;
import de.iolite.drivers.framework.device.DeviceConfigurationObserver;
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
public class LampDriver implements Driver, DeviceConfigurationObserver {

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(LampDriver.class);

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
		new SimulatedLampDevice(PlainDeviceFactory.create(device));
	}

	@Override
	public final void onRemoved(@Nonnull final String deviceIdentifier) {
		Objects.requireNonNull(deviceIdentifier, "'deviceIdentifier' must not be null");
		// nothing to do yet
	}

	@Override
	@Nonnull
	public final DeviceConfigurationObserver start(@Nonnull final DriverAPI driverAPI,
			@Nonnull final Set<Device> existingDevices) throws DriverStartFailedException {
		Validate.notNull(driverAPI, "'driverAPI' must not be null");
		Validate.notNull(existingDevices, "'existingDevices' must not be null");

		
		// report 2 example lamps
		try {
			startJetty(existingDevices);
			PlainDeviceConfigurationBuilder deviceBuilder = new PlainDeviceConfigurationBuilder(
					driverAPI.configure("simulated-lamp1", PROFILE_DimmableLamp_ID));
			deviceBuilder.withProperty(PROFILE_PROPERTY_DimmableLamp_on_ID);
			deviceBuilder.withProperty(PROFILE_PROPERTY_DimmableLamp_dimmingLevel_ID);
			deviceBuilder.withProperty(PROFILE_PROPERTY_DimmableLamp_powerUsage_ID);
			deviceBuilder.withProperty(DriverConstants.PROFILE_PROPERTY_DimmableLamp_apiUrl_ID);
			deviceBuilder.withProperty(DriverConstants.PROFILE_PROPERTY_DimmableLamp_apiUrl_NAME);
			// deviceBuilder.withProperty(DriverConstants.ATTRIBUTE_BarometricSensor_longitude_ID);
			deviceBuilder.fromManufacturer("funny company");
			deviceBuilder.withModelName("Simulated Lamp 1");
			deviceBuilder.addIfAbsent();

			// deviceBuilder = new
			// PlainDeviceConfigurationBuilder(driverAPI.configure("simulated-lamp2",
			// PROFILE_DimmableLamp_ID));
			// deviceBuilder.withProperty(PROFILE_PROPERTY_DimmableLamp_on_ID);
			// deviceBuilder.withProperty(PROFILE_PROPERTY_DimmableLamp_dimmingLevel_ID);
			// deviceBuilder.withProperty(PROFILE_PROPERTY_DimmableLamp_powerUsage_ID);
			// deviceBuilder.fromManufacturer("funny company");
			// deviceBuilder.withModelName("Simulated Lamp 2");
			// deviceBuilder.addIfAbsent();

			

		} catch (final DeviceConfigurationException e) {
			LOGGER.error("Failed to report simulated devices", e);
		}

		existingDevices.forEach(this::onConfigured);

		return this;
	}

	@Override
	public final void stop() {
		LOGGER.debug("Stopped '{}'", this.getClass().getSimpleName());
	}

	private void startJetty(Set<Device> existingDevices) throws DeviceConfigurationException {

		try {
		
			Server server = new Server(8080);

			ResourceHandler resource_handler = new ResourceHandler();

			resource_handler.setDirectoriesListed(true);
			resource_handler.setResourceBase(".");

			HandlerList handlers = new HandlerList();
			handlers.setHandlers(new Handler[] { new HelloHandler(existingDevices) });
			server.setHandler(handlers);

			// Start things up! By using the server.join() the server thread will join with
			// the current thread.
			// See
			// "http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.html#join()"
			// for more details.
			server.start();
			server.join();
		} catch (Exception ex) {
			System.err.println(ex);
		}

	}
}
