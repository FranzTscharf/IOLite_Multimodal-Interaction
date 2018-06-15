/*
 * Copyright (C) 2015 IOLITE, All rights reserved. Created: 02.01.2015 Created by: lehmann
 */

package de.iolite.drivers.example;

import static de.iolite.drivers.basic.DriverConstants.PROPERTY_blindDriveStatus_LITERAL_stopped;
import static de.iolite.drivers.basic.DriverConstants.PROPERTY_playbackState_LITERAL_stop;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.ContextHandler.Context;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.drivers.basic.DriverConstants;
import de.iolite.drivers.framework.DataPoint;
import de.iolite.drivers.framework.DataPointConfiguration;
import de.iolite.drivers.framework.DataPointFactory;
import de.iolite.drivers.framework.DataPointValueCallback;
import de.iolite.drivers.framework.Driver;
import de.iolite.drivers.framework.DriverAPI;
import de.iolite.drivers.framework.device.Device;
import de.iolite.drivers.framework.device.DeviceConfigurationBuilder;
import de.iolite.drivers.framework.device.DeviceConfigurationObserver;
import de.iolite.drivers.framework.exception.DataPointConfigurationException;
import de.iolite.drivers.framework.exception.DataPointInstantiationException;
import de.iolite.drivers.framework.exception.DeviceConfigurationException;
import de.iolite.drivers.framework.exception.DeviceStartException;
import de.iolite.drivers.framework.exception.DriverStartFailedException;
import de.iolite.utilities.concurrency.scheduler.Scheduler;
/**
 * Demonstrates the implementation of an IOLITE driver.
 *
 * @author Grzegorz Lehmann
 * @since 16.11
 */
public final class ExampleDriver implements Driver {

	enum DataPointTypes {
		POWER_USAGE("power_usage"), ON_OFF_STATUS("on_off_status"), BOOLEAN_SENSOR("boolean_sensor"), DOUBLE_SENSOR("double_sensor"),
		INTEGER_DATAPOINT("integer_datapoint"), STRING_DATAPOINT("string_datapoint"), BLIND_DRIVE_STATUS("blind_drive_status"),
		DOUBLE_DATAPOINT("double_datapoint"), PLAYBACK_STATE_DATAPOINT("playback_state");

		@Nonnull
		private final String name;

		private DataPointTypes(@Nonnull final String typeName) {
			this.name = typeName;
		}

		@Nonnull
		static DataPointTypes get(@Nonnull final String dataPointTypeName)
				throws DataPointConfigurationException {
			for (final DataPointTypes value : values()) {
				if (value.getName().equalsIgnoreCase(dataPointTypeName)) {
					return value;
				}
			}
			throw new DataPointConfigurationException(String.format("Unknown data point type '%s'", dataPointTypeName));
		}
		

		@Nonnull
		String getName() {
			return this.name;
		}
	}

	private static final class DeviceStarter implements DeviceConfigurationObserver {

		@Nonnull
		private final DataPointFactory factory;

		private DeviceStarter(@Nonnull final DataPointFactory dataPointFactory) {
			this.factory = dataPointFactory;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onConfigured(@Nonnull final Device device) {
			Validate.notNull(device, "'device' must not be null");
			// start all configured devices immediately
			try {
				LOGGER.info("Start device '{}'", device.getIdentifier());
				final LazyDeclaredMaximumPowerSupplier lazyDeclaredMaximumPowerSupplier = new LazyDeclaredMaximumPowerSupplier(device);
				final DeclaredMaximumPowerReportingFactoryProxy dataPointFactory = new DeclaredMaximumPowerReportingFactoryProxy(this.factory, lazyDeclaredMaximumPowerSupplier);
				device.start(dataPointFactory);
			}
			catch (final DeviceStartException e) {
				LOGGER.error("Failed to start device '{}'", device, e);
			}

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onRemoved(@Nonnull final String deviceIdentifier) {
			// nothing to do
		}
	}

	private final class ExampleDataPointFactory implements DataPointFactory {

		@Nonnull
		private final Map<DataPointTypes, DataPointFactory> strategies = new EnumMap<>(DataPointTypes.class);

		private ExampleDataPointFactory(@Nonnull final Scheduler scheduler) {
			this.strategies.put(DataPointTypes.POWER_USAGE, new PowerUsageDataPointFactory(scheduler));
			this.strategies.put(DataPointTypes.ON_OFF_STATUS, new OnOffStatusDataPointFactory());
			this.strategies.put(DataPointTypes.BOOLEAN_SENSOR, new BooleanSensorDataPointFactory(scheduler));
			this.strategies.put(DataPointTypes.DOUBLE_SENSOR, new DoubleSensorDataPointFactory(scheduler));
			this.strategies.put(DataPointTypes.INTEGER_DATAPOINT, new IntegerDataPointFactory(0));
			this.strategies.put(DataPointTypes.BLIND_DRIVE_STATUS, new StringDataPointFactory(PROPERTY_blindDriveStatus_LITERAL_stopped));
			this.strategies.put(DataPointTypes.DOUBLE_DATAPOINT, new DoubleDataPointFactory(0.0));
			this.strategies.put(DataPointTypes.STRING_DATAPOINT, new StringDataPointFactory(""));
			this.strategies.put(DataPointTypes.PLAYBACK_STATE_DATAPOINT, new StringDataPointFactory(PROPERTY_playbackState_LITERAL_stop));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		@Nonnull
		public DataPoint create(@Nonnull final DataPointConfiguration configuration, @Nonnull final String propertyTypeIdentifier,
				@Nonnull final DataPointValueCallback callback)
				throws DataPointConfigurationException, DataPointInstantiationException {
			Validate.notNull(configuration, "'configuration' must not be null");
			Validate.notNull(propertyTypeIdentifier, "'propertyTypeIdentifier' must not be null");
			Validate.notNull(callback, "'callback' must not be null");
			final DataPointTypes dataPointType = DataPointTypes.get(configuration.getDataPointType());
			final DataPointFactory strategy = this.strategies.get(dataPointType);

			if (strategy == null) {
				throw new DataPointInstantiationException(String.format("Unknown data point type '%s'", dataPointType));
			}
			return strategy.create(configuration, propertyTypeIdentifier, callback);
		}
	}

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(ExampleDriver.class);

	/**
	 * Configuration entry for initial value of a randomized property.
	 */
	@Nonnull
	static final String CONFIGURATION_INITIAL_VALUE = "initial.value";

	/**
	 * Configuration min value of a randomized property.
	 */
	@Nonnull
	static final String CONFIGURATION_MIN_VALUE = "min.value";

	/**
	 * Configuration max value of a randomized property.
	 */
	@Nonnull
	static final String CONFIGURATION_MAX_VALUE = "max.value";

	/**
	 * Configuration entry that (when set true) report random values to the power usage property.
	 */
	@Nonnull
	static final String CONFIGURATION_RANDOMIZE_VALUE = "randomize.value";

	/**
	 * Configuration entry that (when set true) report estimated power usage.
	 */
	@Nonnull
	static final String CONFIGURATION_ESTIMATE_POWER = "estimate.power";

	@Nonnull
	private static final String IOLITE_GMBH_NAME = "IOLITE GmbH";

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Nonnull
	public DeviceConfigurationObserver start(@Nonnull final DriverAPI driverAPI, @Nonnull final Set<Device> existingDevices)
			throws DriverStartFailedException {
		Validate.notNull(driverAPI, "'driverAPI' must not be null");
		Validate.notNull(existingDevices, "'existingDevices' must not be null");
		// report some example devices
		try {
			configureExampleDevices(driverAPI);
			startJetty(driverAPI);
		}
		catch (final DeviceConfigurationException e) {
			throw new DriverStartFailedException("Failed to configure devices", e);
		}

		final ExampleDataPointFactory factory = new ExampleDataPointFactory(driverAPI.getScheduler());
		existingDevices.forEach(device -> {
			final LazyDeclaredMaximumPowerSupplier lazyDeclaredMaximumPowerSupplier = new LazyDeclaredMaximumPowerSupplier(device);
			final OnOffStatusDataPointFactory onOffStatusDataPointFactory = new OnOffStatusDataPointFactory();
			final DataPointFactory dataPointFactory = new DeclaredMaximumPowerReportingFactoryProxy(factory, lazyDeclaredMaximumPowerSupplier);
			try {
				LOGGER.debug("Start device '{}'", device.getIdentifier());
				device.start(dataPointFactory);
			}
			catch (final DeviceStartException e) {
				LOGGER.error("Failed to start existing device '{}' due to error: {}", device.getIdentifier(), e.getMessage());
			}
		});
		return new DeviceStarter(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		LOGGER.debug("Stopped '{}'", this.getClass().getSimpleName());
	}
	
	private void startJetty(@Nonnull final DriverAPI deviceManagement)
			throws DeviceConfigurationException {
		
		  try {
		        Server server = new Server(8000);

		        // Create the ResourceHandler. It is the object that will actually handle the request for a given file. It is
		        // a Jetty Handler object so it is suitable for chaining with other handlers as you will see in other examples.
		        ResourceHandler resource_handler = new ResourceHandler();

		        // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
		        // In this example it is the current directory but it can be configured to anything that the jvm has access to.
		        resource_handler.setDirectoriesListed(true);
		        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
		        resource_handler.setResourceBase(".");

		        // Add the ResourceHandler to the server.
		        HandlerList handlers = new HandlerList();
		        //handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
		        handlers.setHandlers(new Handler[] { new HelloHandler() });
		        server.setHandler(handlers);

		        // Start things up! By using the server.join() the server thread will join with the current thread.
		        // See "http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.html#join()" for more details.
		        server.start();
		        server.join();
		    } catch (Exception ex) {
		        System.err.println(ex);
		    }
	
	}

	private void configureExampleDevices(@Nonnull final DriverAPI deviceManagement)
			throws DeviceConfigurationException {
		// Configure a lamp device
		final DeviceConfigurationBuilder lamp1 = deviceManagement.configure("lamp1", DriverConstants.PROFILE_Lamp_ID);
		lamp1.fromManufacturer(IOLITE_GMBH_NAME);
		lamp1.withDataPoint(DataPointTypes.ON_OFF_STATUS.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_Lamp_on_ID);
		lamp1.withConfiguration(CONFIGURATION_RANDOMIZE_VALUE, true).and(CONFIGURATION_INITIAL_VALUE, 120).forDataPoint(
				DataPointTypes.POWER_USAGE.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_Lamp_powerUsage_ID);
		lamp1.addIfAbsent();

		// Configure a lamp device with an estimated power usage, depending on the declaredMaximumPowerUsage attribute. The attribute has to be set in the UI.
		final DeviceConfigurationBuilder lamp2 = deviceManagement.configure("lamp2", DriverConstants.PROFILE_Lamp_ID);
		lamp2.fromManufacturer(IOLITE_GMBH_NAME);
		lamp2.withDataPoint(DataPointTypes.ON_OFF_STATUS.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_Lamp_on_ID);
		lamp2.withConfiguration(CONFIGURATION_ESTIMATE_POWER, true).forDataPoint(DataPointTypes.POWER_USAGE.getName()).ofProperty(
				DriverConstants.PROFILE_PROPERTY_Lamp_powerUsage_ID);
		lamp2.forceAdd();

		// Configure a dimable lamp device with DeclaredMaximumPowerReportingFactoryProxy attribute and DimmingLevel to calculate PowerUsageEstimate
		final DeviceConfigurationBuilder lamp3 = deviceManagement.configure("lampDimmable", DriverConstants.PROFILE_DimmableLamp_ID);
		lamp3.fromManufacturer(IOLITE_GMBH_NAME);
		lamp3.withDataPoint(DataPointTypes.ON_OFF_STATUS.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_DimmableLamp_on_ID);
		lamp3.withDataPoint(DataPointTypes.INTEGER_DATAPOINT.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_DimmableLamp_dimmingLevel_ID);
		lamp3.withConfiguration(CONFIGURATION_ESTIMATE_POWER, true).forDataPoint(DataPointTypes.POWER_USAGE.getName()).ofProperty(
				DriverConstants.PROFILE_PROPERTY_DimmableLamp_powerUsage_ID);
		lamp3.forceAdd();

		// Configure a contact sensor device
		final DeviceConfigurationBuilder contactSensor1 = deviceManagement.configure("contactSensor1", DriverConstants.PROFILE_ContactSensor_ID);
		contactSensor1.fromManufacturer(IOLITE_GMBH_NAME);
		contactSensor1.withDataPoint(DataPointTypes.BOOLEAN_SENSOR.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_ContactSensor_contactDetected_ID);
		contactSensor1.addIfAbsent();

		// Configure a movement sensor device
		final DeviceConfigurationBuilder movementSensor1 = deviceManagement.configure("movementSensor1", DriverConstants.PROFILE_MovementSensor_ID);
		movementSensor1.fromManufacturer(IOLITE_GMBH_NAME);
		movementSensor1.withDataPoint(DataPointTypes.BOOLEAN_SENSOR.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_MovementSensor_movementDetected_ID);
		movementSensor1.addIfAbsent();

		// Configure a smoke sensor device
		final DeviceConfigurationBuilder smokeSensor1 = deviceManagement.configure("smokeSensor1", DriverConstants.PROFILE_SmokeDetectionSensor_ID);
		smokeSensor1.fromManufacturer(IOLITE_GMBH_NAME);
		smokeSensor1.withDataPoint(DataPointTypes.BOOLEAN_SENSOR.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_SmokeDetectionSensor_smokeDetected_ID);
		smokeSensor1.addIfAbsent();

		// Configure a window device
		final DeviceConfigurationBuilder window1 = deviceManagement.configure("window1", DriverConstants.PROFILE_Window_ID);
		window1.fromManufacturer(IOLITE_GMBH_NAME);
		window1.withDataPoint(DataPointTypes.BOOLEAN_SENSOR.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_Window_open_ID);
		window1.addIfAbsent();

		// Configure a blind device
		final DeviceConfigurationBuilder blind1 = deviceManagement.configure("blind1", DriverConstants.PROFILE_Blind_ID);
		blind1.fromManufacturer(IOLITE_GMBH_NAME);
		blind1.withDataPoint(DataPointTypes.INTEGER_DATAPOINT.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_Blind_blindLevel_ID);
		blind1.withDataPoint(DataPointTypes.BLIND_DRIVE_STATUS.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_Blind_blindDriveStatus_ID);
		blind1.withConfiguration(CONFIGURATION_RANDOMIZE_VALUE, true).and(CONFIGURATION_INITIAL_VALUE, 120).forDataPoint(
				DataPointTypes.POWER_USAGE.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_Blind_powerUsage_ID);
		blind1.addIfAbsent();

		// Configure a door device
		final DeviceConfigurationBuilder door1 = deviceManagement.configure("door1", DriverConstants.PROFILE_Door_ID);
		door1.fromManufacturer(IOLITE_GMBH_NAME);
		door1.withDataPoint(DataPointTypes.BOOLEAN_SENSOR.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_Door_open_ID);
		door1.addIfAbsent();

		// Configure a socket device
		final DeviceConfigurationBuilder socket1 = deviceManagement.configure("socket1", DriverConstants.PROFILE_Socket_ID);
		socket1.fromManufacturer(IOLITE_GMBH_NAME);
		socket1.withDataPoint(DataPointTypes.ON_OFF_STATUS.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_Socket_on_ID);
		socket1.withConfiguration(CONFIGURATION_RANDOMIZE_VALUE, true).and(CONFIGURATION_INITIAL_VALUE, 120).forDataPoint(
				DataPointTypes.POWER_USAGE.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_Socket_powerUsage_ID);
		socket1.addIfAbsent();

		// Configure a cook top with four hobs device
		final DeviceConfigurationBuilder cookTop1 = deviceManagement.configure("cooktop1", DriverConstants.PROFILE_CookTopWithFourHobs_ID);
		cookTop1.fromManufacturer(IOLITE_GMBH_NAME);
		// cookTop1.withDataPoint(DataPointTypes.ON_OFF_STATUS.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_CookTopWithFourHobs_on_ID);
		cookTop1.withDataPoint(DataPointTypes.INTEGER_DATAPOINT.getName()).ofProperty(
				DriverConstants.PROFILE_PROPERTY_CookTopWithFourHobs_hob1HeatLevelSetting_ID);
		cookTop1.withDataPoint(DataPointTypes.INTEGER_DATAPOINT.getName()).ofProperty(
				DriverConstants.PROFILE_PROPERTY_CookTopWithFourHobs_hob2HeatLevelSetting_ID);
		cookTop1.withDataPoint(DataPointTypes.INTEGER_DATAPOINT.getName()).ofProperty(
				DriverConstants.PROFILE_PROPERTY_CookTopWithFourHobs_hob3HeatLevelSetting_ID);
		cookTop1.withDataPoint(DataPointTypes.INTEGER_DATAPOINT.getName()).ofProperty(
				DriverConstants.PROFILE_PROPERTY_CookTopWithFourHobs_hob4HeatLevelSetting_ID);
		cookTop1.withDataPoint(DataPointTypes.INTEGER_DATAPOINT.getName()).ofProperty(
				DriverConstants.PROFILE_PROPERTY_CookTopWithFourHobs_hob1HeatLevelRemaining_ID);
		cookTop1.withDataPoint(DataPointTypes.INTEGER_DATAPOINT.getName()).ofProperty(
				DriverConstants.PROFILE_PROPERTY_CookTopWithFourHobs_hob2HeatLevelRemaining_ID);
		cookTop1.withDataPoint(DataPointTypes.INTEGER_DATAPOINT.getName()).ofProperty(
				DriverConstants.PROFILE_PROPERTY_CookTopWithFourHobs_hob3HeatLevelRemaining_ID);
		cookTop1.withDataPoint(DataPointTypes.INTEGER_DATAPOINT.getName()).ofProperty(
				DriverConstants.PROFILE_PROPERTY_CookTopWithFourHobs_hob4HeatLevelRemaining_ID);
		cookTop1.withConfiguration(CONFIGURATION_RANDOMIZE_VALUE, true).and(CONFIGURATION_INITIAL_VALUE, 120).forDataPoint(
				DataPointTypes.POWER_USAGE.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_CookTopWithFourHobs_powerUsage_ID);
		cookTop1.addIfAbsent();

		// Configure a oven device
		final DeviceConfigurationBuilder oven1 = deviceManagement.configure("oven1", DriverConstants.PROFILE_Oven_ID);
		oven1.fromManufacturer(IOLITE_GMBH_NAME);
		oven1.withDataPoint(DataPointTypes.ON_OFF_STATUS.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_Oven_on_ID);
		oven1.withDataPoint(DataPointTypes.DOUBLE_DATAPOINT.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_Oven_bakingTemperatureSetting_ID);
		oven1.withConfiguration(CONFIGURATION_RANDOMIZE_VALUE, true).and(CONFIGURATION_INITIAL_VALUE, 120).forDataPoint(
				DataPointTypes.POWER_USAGE.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_Oven_powerUsage_ID);
		oven1.addIfAbsent();

		// Configure a media player device
		final DeviceConfigurationBuilder mediaPlayer1 = deviceManagement.configure("mediaplayer1", DriverConstants.PROFILE_MediaPlayerDevice_ID);
		mediaPlayer1.fromManufacturer(IOLITE_GMBH_NAME);
		mediaPlayer1.withDataPoint(DataPointTypes.STRING_DATAPOINT.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_MediaPlayerDevice_mediaURI_ID);
		mediaPlayer1.withDataPoint(DataPointTypes.PLAYBACK_STATE_DATAPOINT.getName()).ofProperty(
				DriverConstants.PROFILE_PROPERTY_MediaPlayerDevice_playbackState_ID);
		mediaPlayer1.withConfiguration(CONFIGURATION_RANDOMIZE_VALUE, true).and(CONFIGURATION_INITIAL_VALUE, 120).forDataPoint(
				DataPointTypes.POWER_USAGE.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_MediaPlayerDevice_powerUsage_ID);
		mediaPlayer1.addIfAbsent();

		// Configure a HSV lamp device
		final DeviceConfigurationBuilder hsvLamp1 = deviceManagement.configure("hsvLamp1", DriverConstants.PROFILE_HSVLamp_ID);
		hsvLamp1.fromManufacturer(IOLITE_GMBH_NAME);
		hsvLamp1.withDataPoint(DataPointTypes.ON_OFF_STATUS.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_HSVLamp_on_ID);
		hsvLamp1.withDataPoint(DataPointTypes.INTEGER_DATAPOINT.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_HSVLamp_dimmingLevel_ID);
		hsvLamp1.withDataPoint(DataPointTypes.DOUBLE_DATAPOINT.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_HSVLamp_hue_ID);
		hsvLamp1.withDataPoint(DataPointTypes.DOUBLE_DATAPOINT.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_HSVLamp_saturation_ID);
		hsvLamp1.withDataPoint(DataPointTypes.DOUBLE_DATAPOINT.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_HSVLamp_colorTemperature_ID);
		hsvLamp1.withConfiguration(CONFIGURATION_RANDOMIZE_VALUE, true).and(CONFIGURATION_INITIAL_VALUE, 120).forDataPoint(
				DataPointTypes.POWER_USAGE.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_HSVLamp_powerUsage_ID);
		hsvLamp1.addIfAbsent();

		// Configure CO2 sensor
		final DeviceConfigurationBuilder carbonDioxide1 = deviceManagement.configure("CO2", DriverConstants.PROFILE_CarbonDioxideSensor_ID);
		carbonDioxide1.fromManufacturer(IOLITE_GMBH_NAME);
		carbonDioxide1.withConfiguration(CONFIGURATION_RANDOMIZE_VALUE, false).and(CONFIGURATION_INITIAL_VALUE, 1000).forDataPoint(DataPointTypes.DOUBLE_DATAPOINT.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_CarbonDioxideSensor_carbonDioxidePPM_ID);
		carbonDioxide1.addIfAbsent();

		// Configure a Heater devices
		final int numberOfHeaters = 4;
		final double initialValue = 20.0;
		final double minValue = 15.0;
		final double maxValue = 25.0;
		for (int i = 0; i < numberOfHeaters; i++) {
			final DeviceConfigurationBuilder heater = deviceManagement.configure("heater" + (i + 1), DriverConstants.PROFILE_Heater_ID);
			heater.fromManufacturer(IOLITE_GMBH_NAME);
			heater.withConfiguration(CONFIGURATION_INITIAL_VALUE, initialValue).and(CONFIGURATION_MIN_VALUE, minValue).and(CONFIGURATION_MAX_VALUE,
					maxValue).forDataPoint(DataPointTypes.DOUBLE_SENSOR.getName()).ofProperty(
							DriverConstants.PROFILE_PROPERTY_Heater_currentEnvironmentTemperature_ID);
			heater.withDataPoint(DataPointTypes.DOUBLE_DATAPOINT.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_Heater_heatingTemperatureSetting_ID);
			heater.withDataPoint(DataPointTypes.DOUBLE_DATAPOINT.getName()).ofProperty(DriverConstants.PROFILE_PROPERTY_Heater_valvePosition_ID);
			heater.addIfAbsent();
		}
	}
}
