package de.iolite.apps.ioliteslackbot.slack;

import static de.iolite.apps.ioliteslackbot.dialogflow.DialogFlowClientApplication.getDialogFlow;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.DeviceAPIException;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceBooleanProperty;
import de.iolite.app.api.environment.Location;
import de.iolite.apps.ioliteslackbot.IoLiteSlackBotApp;
import de.iolite.drivers.basic.DriverConstants;

public class SlackDirectMessageController {

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(IoLiteSlackBotApp.class);

	public static void getDecisionTree(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app) {
		switch (req.getContent()) {
		case "turn all lights on":
			LOGGER.warn(req.getContent());
			lightAll(req, resp, app);
			break;
		case "switch all lights on":
			break;
		case "turn all lights off":
			System.out.print("");
			break;
		case "switch all lights off":
			break;
		case "is the heater off":
			isTheHeatherOff(req, resp, app);
			break;
		case "getAllDeviceNames":
			getAllDeviceNames(req, resp, app);
			break;
		default:
			// ask DialogFlow of a response if the other case don't fit;
			LOGGER.warn("DialogFlow Request");
			getDialogFlow(req, resp, app);
		}
	}
	
	

	public static void lightAll(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app) {
		resp.reply("Sure think! I switched on the following devices:");
		// iterate devices
		for (final Device device : app.getDeviceAPI().getDevices()) {
			// togle the on trigger!
			final DeviceBooleanProperty onProperty = device.getBooleanProperty(DriverConstants.PROPERTY_on_ID);
			final Boolean onValue;
			if (onProperty != null && (onValue = onProperty.getValue()) != null) {
				// get only lamps
				if (device.getModelName().contains("Lamp") || device.getModelName().contains("lamp")) {
					try {
						// toge on propertie
						onProperty.requestValueUpdate(!onValue);
						// give terminal output
						LOGGER.warn("toggling device '{}'", device.getIdentifier());
						// return the name of the device
						resp.reply("I switched" + device.getIdentifier() + "on");
					} catch (final DeviceAPIException e) {
						LOGGER.error("Failed to control device", e);
					}
				}
			}
		}
	}

	public static void isTheHeatherOff(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app) {
		// iterate devices
		ArrayList<Device> devices = new ArrayList<Device>();
		for (final Device device : app.getDeviceAPI().getDevices()) {
			// togle the on trigger!
			if (device.getIdentifier().toLowerCase().contains("")) {
				devices.add(device);
			}
		}

		StringBuilder sb = new StringBuilder();

		if (devices.size() == 0) {
			sb.append("I cound not find the heater");
		}

		for (Device dev : devices) {
			sb.append("Heater " + dev.getName() + " has the temperature: "
					+ dev.getStringProperty(
							DriverConstants.PROFILE_PROPERTY_TemperatureSensor_currentEnvironmentTemperature_ID)
					+ "\n");
		}
		try {
			// toge on propertie

			// give terminal output
			LOGGER.warn(sb.toString());
			// return the name of the device
			resp.reply(sb.toString());
		} catch (final Exception e) {
			LOGGER.error("Failed to control device", e);
		}

	}

	public static void getAllDeviceNames(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app) {
		// iterate devices
		StringBuilder sb = new StringBuilder();
		sb.append("I found the following devices:" + "\n");
		for (final Device device : app.getDeviceAPI().getDevices()) {
			if (device.getName() != null)
				sb.append(device.getName() + " : " + device.getIdentifier() + " : " + device.getProfileIdentifier()
						+ " : " + device.getStringProperty(DriverConstants.PROFILE_PROPERTY_Device_deviceStatus_ID) + "\n");
		}
		try {
			LOGGER.warn(sb.toString());
			resp.reply(sb.toString());
		} catch (final Exception e) {
			LOGGER.error("Failed to control device", e);
		}

	}
	
	public static void getAllRoomNames(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app) {
		// iterate devices
		StringBuilder sb = new StringBuilder();
		sb.append("I found the following Rooms:" + "\n");
		for (final Location location : app.getEnvironmentAPI().getLocations()) {
			if (location.getName() != null)
				sb.append(location.getName()+"\n");
		}
		try {
			LOGGER.warn(sb.toString());
			resp.reply(sb.toString());
		} catch (final Exception e) {
			LOGGER.error("Failed to control device", e);
		}

	}
	
	
}
