package de.iolite.apps.ioliteslackbot.messagecontroller;

import java.util.ArrayList;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceProperty;
import de.iolite.drivers.basic.DriverConstants;

/**
 * Turns on/off devices when getting home or leaving home.
 *
 * @author Elin Björnsson
 * @since 18.07.2018
 */
public class UC_IAmHomeController {

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(UC_BedController.class);

	private MessageController mc;

	public UC_IAmHomeController(MessageController messageController) {
		this.mc = messageController;
	}

	public void Home_leaving(boolean bol) {
		String verb_02 = "on";
		if (!bol) {
			verb_02 = "off";
		}
		if (mc.getIterationNr() == 0) {
			mc.getResponse().reply("Okay, Do you want me to switch " + verb_02+ " the lamps?");
			mc.setIterationNr(1);
		} else {
			if (mc.getRequest().contains("yes")) {
				turnOffLamps(bol);
			} else if (mc.getRequest().contains("no")) {
				mc.getResponse().reply("okay");
			} else if (mc.getRequest().contains("lamp")) {
				turnOffLamps(bol);
			} else {
				mc.getResponse().reply("sorry, I couldn't understand your request..");
			}

			mc.setIterationNr(0);
		}

	}

	public void turnOffLamps(boolean bol) {
		String on_off = "on";
		if (!bol) {
			on_off = "off";
		}

		mc.getResponse().reply("I turned " + on_off + " the following lamps:");
		ArrayList<Device> lamps = mc.getAllDevicesByProfile("lamp");
		ArrayList<Device> dimLamps = mc.getAllDevicesByProfile("dimmablelamp");
		lamps.addAll(dimLamps);

		for (Device dev : lamps) {
			try {
				dev.getBooleanProperty(DriverConstants.PROPERTY_on_ID).requestValueUpdate(bol);
				mc.getResponse().reply(dev.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

