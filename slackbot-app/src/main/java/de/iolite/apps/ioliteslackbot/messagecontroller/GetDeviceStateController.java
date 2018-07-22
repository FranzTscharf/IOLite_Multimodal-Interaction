package de.iolite.apps.ioliteslackbot.messagecontroller;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceBooleanProperty;
import de.iolite.app.api.device.access.DeviceProperty;
import de.iolite.app.api.environment.Location;
import de.iolite.drivers.basic.DriverConstants;

/**
 * Gets the current state of devices in iolite
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
	
	public void is()
	{
		if(mc.getRequest().contains(" heater "))
		{
			checkHeater();
		}
		else if(mc.getRequest().contains(" on "))
		{
			checkOn();
		}
		else {
			mc.getResponse().reply("I didn't understand your command!");
		}
	}
	
	public void checkOn()
	{
		
ArrayList<Device> devs = mc.findDeviceByName();
		
		for(Device dev : devs)
		{
		
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
		
	}
	
	public void checkHeater()
	{
ArrayList<Device> devs = mc.findDeviceByName();
		
		for(Device dev : devs)
		{
		
		if(dev==null)
		{
			mc.getResponse().reply("Could not find this device!");
		}
		else if(dev.getBooleanProperty(DriverConstants.PROFILE_PROPERTY_Heater_valveStatus_ID)==null)
		{
			mc.getResponse().reply("This device dosn't have an open/closed state!");
		}
		else{
			DeviceBooleanProperty bol = dev.getBooleanProperty(DriverConstants.PROFILE_PROPERTY_Heater_valveStatus_ID);
			String state = "open";
			if(!bol.getValue())
				state = "closed";
			mc.getResponse().reply("The device \""+dev.getName()+"\" has the state \""+state+"\"");
		}
		}
		
		
	}
	
	
	

}