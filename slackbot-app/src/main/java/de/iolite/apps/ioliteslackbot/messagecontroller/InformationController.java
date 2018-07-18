package de.iolite.apps.ioliteslackbot.messagecontroller;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.environment.Location;

/**
 * Gets informations about devices in iolite
 *
 * @author Johannes Hassler
 * @since 06.07.2018
 */

public class InformationController {
	
	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(InformationController.class);
	
	private MessageController mc;
	
	public InformationController(MessageController messageController)
	{
		this.mc = messageController;
	}
	
	public void getAll()
	{
	if(mc.getRequest().contains("all"))
	{

		if(mc.getRequest().contains("device")&&mc.getRequest().contains("name"))
		{
			getAllDeviceNames();
		}
		
		else if(mc.getRequest().contains("location"))
		{
			getAllLocationNames();
		}
		else if(mc.getRequest().contains("profile"))
		{
			getAllDeviceProfiles();
		}
		
	
		
	}
	
	
	
	if(mc.getRequest().contains("getalldeviceprofiles"))
	{
		getAllDeviceProfiles();
	}

	
	
	}
	
	private void getAllDeviceNames() {
		// iterate devices
		StringBuilder sb = new StringBuilder();
		sb.append("I found the following devices:" + "\n");
		for (final Device device : mc.getApp().getDeviceAPI().getDevices()) {
			if (device.getIdentifier() != null)
				sb.append(device.getName()+"\n");
		}
		try {
			LOGGER.warn(sb.toString());
			mc.getResponse().reply(sb.toString());
		} catch (final Exception e) {
			LOGGER.error("Failed to control device", e);
		}

	}
	
	public void getAllLocationNames() {
		// iterate devices
		StringBuilder sb = new StringBuilder();
		sb.append("I found the following Locations:" + "\n");
		for (final Location location : mc.getApp().getEnvironmentAPI().getLocations()) {
			if (location.getIdentifier() != null)
				sb.append(location.getName()+"\n");
		}
		try {
			LOGGER.warn(sb.toString());
			mc.getResponse().reply(sb.toString());
		} catch (final Exception e) {
			LOGGER.error("Failed to control device", e);
		}

	}
	
	private void getAllDeviceProfiles() {
		// iterate devices
		StringBuilder sb = new StringBuilder();
		sb.append("I found the following devices:" + "\n");
		for (final Device device : mc.getApp().getDeviceAPI().getDevices()) {
			if (device.getIdentifier() != null)
			{
				String profile = device.getProfileIdentifier();
				profile = profile.substring(profile.indexOf("#") + 1, profile.length()).toLowerCase();
				sb.append(device.getName()+" : "+profile+"\n");
			}
		}
		try {
			LOGGER.warn(sb.toString());
			mc.getResponse().reply(sb.toString());
		} catch (final Exception e) {
			LOGGER.error("Failed to control device", e);
		}

	}
	


}
