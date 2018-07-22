package de.iolite.apps.ioliteslackbot.messagecontroller;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceProperty;
import de.iolite.drivers.basic.DriverConstants;

/**
 * lower/
 *
 * @author Elin Björnsson
 * @since 22.07.2018
 */
public class UC_BlindsController {

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(UC_BlindsController.class);

	private MessageController mc;

	public UC_BlindsController(MessageController messageController) {
		this.mc = messageController;
	}

	public void lower_raise_blinds(boolean bol) {
		String room = "room";
		String verb_01 = "raise";
		if (!bol) {
			verb_01 = "lower";
		}
		if (mc.getIterationNr() == 0) {
			mc.getResponse().reply("Okay, in which room do you want me to " +verb_01+" the blinds?");
			mc.setIterationNr(1);
		} else {
			if (mc.getRequest().contains("bedroom")) {
				room = "bedroom";
				closeBlinds(bol, room);
			} else if (mc.getRequest().contains("kitchen")) {
				room = "kitchen";
				closeBlinds(bol, room);
			} else if (mc.getRequest().contains("living room")) {
				room = "living room";
				closeBlinds(bol, room);
			} else if (mc.getRequest().contains("office")) {
				room = "office";
				closeBlinds(bol, room);
			} else {
				mc.getResponse().reply("sorry, I couldn't understand your request..");
			}

			mc.setIterationNr(0);
		}

	}

	public void closeBlinds(boolean bol, String s) {
		String open_close = "raised";
		String blindstate = DriverConstants.PROPERTY_blindDriveStatus_LITERAL_moving_in;
		if (!bol) {

			open_close = "lowered";
			blindstate = DriverConstants.PROPERTY_blindDriveStatus_LITERAL_moving_out;
		}

		mc.getResponse().reply("I " + open_close + " the blinds:");

		ArrayList<Device> blinds = mc.getAllDevicesByProfileAndRoom("blind", s);
		for (Device dev : blinds) {
			try {

				DeviceProperty<?, ?> prop = dev.getProperty(DriverConstants.PROPERTY_blindDriveStatus_ID);
				// LOGGER.error(prop.getValue().toString());
				prop.requestValueUpdateFromString(blindstate);
				//mc.getResponse().reply(dev.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
