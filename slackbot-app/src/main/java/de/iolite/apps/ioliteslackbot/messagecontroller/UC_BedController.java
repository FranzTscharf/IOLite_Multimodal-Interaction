package de.iolite.apps.ioliteslackbot.messagecontroller;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceProperty;
import de.iolite.drivers.basic.DriverConstants;

/**
 * Servs the use case if the user wants to go to bed.
 *
 * @author Johannes Hassler
 * @since 06.07.2018
 */
public class UC_BedController {

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(UC_BedController.class);

	private MessageController mc;

	public UC_BedController(MessageController messageController) {
		this.mc = messageController;
	}

	public void sleep_wakeup(boolean bol) {
		String verb_01 = "open";
		String verb_02 = "on";
		if (!bol) {
			verb_01 = "close";
			verb_02 = "off";
		}
		if (mc.getIterationNr() == 0) {
			mc.getResponse().reply("Okay, Do you want me to " +verb_01+" the Blinds and switch " + verb_02+ " the lamps?");
			mc.setIterationNr(1);
		} else {
			if (mc.getRequest().contains("yes")) {
				closeBlinds(bol);
				turnOffLamps(bol);
			} else if (mc.getRequest().contains("no")) {
				mc.getResponse().reply("okay, how ever you want!");
			} else if (mc.getRequest().contains("lamp")) {
				turnOffLamps(bol);
			} else if (mc.getRequest().contains("blind")) {
				closeBlinds(bol);
			} else {
				mc.getResponse().reply("sorry, I couldn't understand your request..");
			}

			mc.setIterationNr(0);
		}

	}

	public void closeBlinds(boolean bol) {
		String open_close = "opened";
		String blindstate = DriverConstants.PROPERTY_blindDriveStatus_LITERAL_moving_in;
		if (!bol) {

			open_close = "closed";
			blindstate = DriverConstants.PROPERTY_blindDriveStatus_LITERAL_moving_out;
		}

		mc.getResponse().reply("I " + open_close + " the blinds:");

		ArrayList<Device> blinds = mc.getAllDevicesByProfileAndRoom("blind", "bedroom");
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

	public void turnOffLamps(boolean bol) {
		String on_off = "on";
		if (!bol) {
			on_off = "off";
		}

		mc.getResponse().reply("I turned " + on_off + " the lamps:");
		ArrayList<Device> lamps = mc.getAllDevicesByProfileAndRoom("lamp", "bedroom");
		ArrayList<Device> dimLamps = mc.getAllDevicesByProfileAndRoom("dimmablelamp", "bedroom");
		lamps.addAll(dimLamps);

		for (Device dev : lamps) {
			try {
				dev.getBooleanProperty(DriverConstants.PROPERTY_on_ID).requestValueUpdate(bol);
				//mc.getResponse().reply(dev.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
