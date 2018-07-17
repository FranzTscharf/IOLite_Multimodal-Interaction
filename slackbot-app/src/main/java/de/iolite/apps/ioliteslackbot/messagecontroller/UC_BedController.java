package de.iolite.apps.ioliteslackbot.messagecontroller;

import java.awt.List;
import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.api.IOLITEAPINotResolvableException;
import de.iolite.api.IOLITEAPIProvider;
import de.iolite.api.IOLITEPermissionDeniedException;
import de.iolite.app.api.device.DeviceAPIException;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceAPI;
import de.iolite.app.api.device.access.DeviceBooleanProperty;
import de.iolite.app.api.device.access.DeviceProperty;
import de.iolite.app.api.environment.Location;
import de.iolite.drivers.basic.DriverConstants;
import de.iolite.models.properties.Property;

/**
 * Turns on/off devices
 *
 * @author Johannes Hassler
 * @since 06.07.2018
 */
public class UC_BedController {
	private IOLITEAPIProvider context;
	private DeviceAPI deviceAPI;
	private Location location;
	

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(UC_BedController.class);

	private MessageController mc;

	public UC_BedController(MessageController messageController) {
		this.mc = messageController;
	}

	
	public void sleep() {
	
		if(mc.getIterationNr()==0)
		{
			mc.getResponse().reply("Okay, Do you want me to lower the Blinds and switch off the lamps?");
			mc.setIterationNr(1);
		}
		else {
			if(mc.getRequest().contains("yes"))
			{
				closeBlinds();
				turnOffLamps();
			}
			else if(mc.getRequest().contains("no"))
			{
				mc.getResponse().reply("okay, how ever you want!");
			}
			else if(mc.getRequest().contains("lamp"))
			{
				turnOffLamps();
			}
			else if(mc.getRequest().contains("blind"))
			{
				closeBlinds();
			}
			else{
				mc.getResponse().reply("sorry, I couldn't understand your request..");
			}
			
			mc.setIterationNr(0);
		}
		
		
	}
	
	
	public void closeBlinds()
	{
		
		mc.getResponse().reply("I closed the following blinds:");
		
		ArrayList<Device> blinds = mc.getAllDevicesByProfile("blind");
		for(Device dev : blinds){
			try {
				
				DeviceProperty<?, ?> prop = dev.getProperty(DriverConstants.PROPERTY_blindDriveStatus_LITERAL_moving_in);
				//LOGGER.error(prop.getValue().toString());
				prop.requestValueUpdateFromString(DriverConstants.PROPERTY_blindDriveStatus_LITERAL_moving_in);
				mc.getResponse().reply(dev.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Example code from the other group
	 * 
	 */
	
//	public void closeAllBlind() throws IOLITEAPINotResolvableException, IOLITEPermissionDeniedException
//	{
//		deviceAPI=context.getAPI(DeviceAPI.class);
//		for (final de.iolite.app.api.device.access.Device allDevices : deviceAPI.getDevices()){
//			for(final de.iolite.app.api.environment.Device device :location.getDevices() )
//			{
//				if((device.getIdentifier().equals(allDevices.getIdentifier())) && (allDevices.getProfileIdentifier().equals(DriverConstants.PROFILE_Blind_ID))){
//					Device.getStringProperty(DriverConstants.PROPERTY_blindDriveStatus_ID).requestValueUpdate(DriverConstants.PROPERTY_blindDriveStatus_LITERAL_moving_in);
//				}
//			}
//		}
//	}
	
	
	
	public void turnOffLamps()
	{
		mc.getResponse().reply("I turned off the following lamps:");
		ArrayList<Device> lamps = mc.getAllDevicesByProfile("lamp");
		ArrayList<Device> dimLamps = mc.getAllDevicesByProfile("dimmablelamp");
		lamps.addAll(dimLamps);
		
		for(Device dev : lamps)
		{
			try {
				dev.getBooleanProperty(DriverConstants.PROPERTY_on_ID).requestValueUpdate(false);
				mc.getResponse().reply(dev.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
		

	

}
