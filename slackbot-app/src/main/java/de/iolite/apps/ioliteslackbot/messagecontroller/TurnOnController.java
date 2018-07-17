package de.iolite.apps.ioliteslackbot.messagecontroller;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.DeviceAPIException;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceBooleanProperty;
import de.iolite.drivers.basic.DriverConstants;

/**
 * Turns on/off devices
 *
 * @author Johannes Hassler
 * @since 06.07.2018
 */
public class TurnOnController {

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(TurnOnController.class);

	private MessageController mc;

	public TurnOnController(MessageController messageController) {
		this.mc = messageController;
	}

	// TurnOn
	public void turn() {
		if (mc.getRequest().contains("all")) {
			turnAll();
		} else {
			turnSpecific();
		}
	}

	private void turnSpecific() {

		boolean on_off = false;
		String sOn_off = "off";

		if (mc.getRequest().contains("on")) {
			on_off = true;
			sOn_off = "on";
		}

		Device dev = mc.findDeviceByName();

		if (dev != null) {
			DeviceBooleanProperty onProperty = dev.getBooleanProperty(DriverConstants.PROPERTY_on_ID);

			if (onProperty == null) {
				mc.getResponse().reply("The device dosn't has an on property..");
			} else {
				try {
					onProperty.requestValueUpdate(on_off);
					mc.getResponse().reply(dev.getName() + " was turnd " + sOn_off);
				} catch (DeviceAPIException e) {
					mc.getResponse().reply("Error while swiching on/off " + dev.getIdentifier());
					LOGGER.debug(e.getMessage());
				}
			}
		} else {
			mc.getResponse().reply("Could not find device");
		}
	}

	private void turnAll() {
		boolean on_off = false;
		String sOn_off = "off";

		if (mc.getRequest().contains("on")) {
			on_off = true;
			sOn_off = "on";
		}

		ArrayList<Device> devices = mc.getAllDevicesByProfile();

		for (Device dev : devices) {
			DeviceBooleanProperty onProperty = dev.getBooleanProperty(DriverConstants.PROPERTY_on_ID);

			if (onProperty == null) {
				mc.getResponse().reply("The devices dosn't have an on property..");
				break;
			} else {
				try {
					onProperty.requestValueUpdate(on_off);
					mc.getResponse().reply(dev.getName() + " was turnd " + sOn_off);
				} catch (DeviceAPIException e) {
					mc.getResponse().reply("Error while swiching on/off " + dev.getIdentifier());
					LOGGER.debug(e.getMessage());
				}
			}
		}

	}

}
