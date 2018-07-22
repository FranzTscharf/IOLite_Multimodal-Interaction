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
 * @author Johannes Hassler
 * @since 18.07.2018
 */
public class UC_IAmHomeController {

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(UC_IAmHomeController.class);

	private MessageController mc;

	public UC_IAmHomeController(MessageController messageController) {
		this.mc = messageController;
	}



	public void turnOffLamps() {

		mc.getResponse().reply("Okay, I will turn off all lamps!");
		ArrayList<Device> lamps = mc.getAllDevicesByProfile("lamp");
		ArrayList<Device> dimLamps = mc.getAllDevicesByProfile("dimmablelamp");
		lamps.addAll(dimLamps);

		for (Device dev : lamps) {
			try {
				dev.getBooleanProperty(DriverConstants.PROPERTY_on_ID).requestValueUpdate(false);
				//mc.getResponse().reply(dev.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public void imHome()
	{
		mc.getResponse().reply("Welcome home. I will turn on the lights for you!");
		ArrayList<Device> lamps = mc.getAllDevicesByProfileAndRoom("lamp", "living room");
		ArrayList<Device> dimLamps = mc.getAllDevicesByProfileAndRoom("dimmablelamp", "living room");
		lamps.addAll(dimLamps);
		
		for (Device dev : lamps) {
			if(dev.getName().toLowerCase().contains("ceiling")||dev.getName().toLowerCase().contains("decken"))
			try {
				dev.getBooleanProperty(DriverConstants.PROPERTY_on_ID).requestValueUpdate(true);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

