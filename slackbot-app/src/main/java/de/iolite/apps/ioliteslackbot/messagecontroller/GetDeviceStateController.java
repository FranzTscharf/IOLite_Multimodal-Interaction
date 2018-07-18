package de.iolite.apps.ioliteslackbot.messagecontroller;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceBooleanProperty;
import de.iolite.app.api.device.access.DeviceProperty;
import de.iolite.app.api.environment.Location;
import de.iolite.drivers.basic.DriverConstants;

/**
 * Gets informations about devices in iolite
 *
 * @author Johannes Hassler
 * @since 06.07.2018
 */

public class GetDeviceStateController {
	
	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(GetDeviceStateController.class);
	
	private MessageController mc;
	
	public GetDeviceStateController(MessageController messageController)
	{
		this.mc = messageController;
	}
	
	public void run()
	{
		Device dev = mc.findDeviceByName();
		
		if(dev==null)
		{
			mc.getResponse().reply("Could not find this device!");
		}
		else if(dev.getBooleanProperty(DriverConstants.PROPERTY_on_ID)==null)
		{
			mc.getResponse().reply("This device dosn't have an on/off state!");
		}
		else{
			DeviceBooleanProperty bol = dev.getBooleanProperty(DriverConstants.PROPERTY_on_ID);
			String state = "on";
			if(!bol.getValue())
				state = "off";
			mc.getResponse().reply("The device \""+dev.getName()+"\" has the state \""+state+"\"");
		}
		
		
	}
	
	
	public void run2()
	{
		Device dev = mc.findDeviceByName();
		
		if(dev==null)
		{
			mc.getResponse().reply("Could not find this device!");
		}
		
		else
		{
			DeviceProperty<?, ?> property = dev.getProperty(DriverConstants.PROPERTY_deviceStatus_ID);
			mc.getResponse().reply("This device has the state "+property.getValue());
		}
		
		
		
	}
	
	

}