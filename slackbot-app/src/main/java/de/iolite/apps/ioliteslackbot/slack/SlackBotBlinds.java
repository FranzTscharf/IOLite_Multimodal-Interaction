package de.iolite.apps.ioliteslackbot.slack;

import de.iolite.app.api.device.DeviceAPIException;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceBooleanProperty;
import de.iolite.apps.ioliteslackbot.IoLiteSlackBotApp;
import de.iolite.drivers.basic.DriverConstants;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;


public class SlackBotBlinds {
	
	@Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(IoLiteSlackBotApp.class);

    public static void allBlinds(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app){
        resp.reply("Sure thing! I will pull up all the blinds:");
        // iterate devices
        for (final Device device : app.getDeviceAPI().getDevices()) {
            // togle the on trigger!
            final DeviceBooleanProperty onProperty = device.getBooleanProperty(DriverConstants.PROPERTY_on_ID);
            final Boolean onValue;
            if (onProperty != null && (onValue = onProperty.getValue()) != null) {
                //get only lamps
                //if (device.getModelName().contains("Lamp") || device.getModelName().contains("lamp")){
                    try {
                        //toge on propertie
                        onProperty.requestValueUpdate(!onValue);
                        //give terminal output
                        LOGGER.warn("toggling device '{}'", device.getIdentifier());
                        //return the name of the device
                        resp.reply("I switched"+device.getIdentifier() + "on");
                    }
                    catch (final DeviceAPIException e) {
                        LOGGER.error("Failed to control device", e);
                    }
                //}
            }
        }
    }

}
