package de.iolite.apps.ioliteslackbot.messagecontroller;

import de.iolite.app.api.device.DeviceAPIException;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceBooleanProperty;
import de.iolite.app.api.environment.Location;
import de.iolite.app.api.environment.LocationType;
import de.iolite.app.api.environment.User;
import de.iolite.apps.ioliteslackbot.IoLiteSlackBotApp;
import de.iolite.apps.ioliteslackbot.messagecontroller.MessageController;
import de.iolite.drivers.basic.DriverConstants;
import org.riversun.slacklet.Slacklet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Kitchen lights will be turned on here
 *
 * @author Marc Ottenbacher
 * @since 05.07.2018
 */

public class UseCaseController extends Slacklet {
    private IoLiteSlackBotApp app;
    private MessageController messageController;


    public UseCaseController(MessageController messageController) {
        this.messageController = messageController;
    }

    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(IoLiteSlackBotApp.class);

    public void useCase1_SwitchTheLightsInLocation(){

        Location currentLoc = getCurrentLocation();

        if (currentLoc.getName() != null){
            List<Device> currentLocationDevices = getCurrentLocationDevices(currentLoc);
            for (Device device : currentLocationDevices) {
                turnSpecificDevice(device);
            }
        }else {
            messageController.getResponse().reply("Sorry, I could not find the requested room");
        }
    }

    private Location getCurrentLocation() {
        List<Location> rooms = messageController.getApp().getEnvironmentAPI().getLocations();
        for (Location r: rooms) {
            if (messageController.getRequest().contains(r.getName())){
                return r;
            }
        }
        messageController.getResponse().reply("Sorry, I could not find the requested room");
        messageController.conversationStatus = MessageController.ConversationStatus.NewConversation;
        return null;
    }


    public List<Device> getCurrentLocationDevices(Location currentLoc) {
        List<Device> locationDeviceList = new ArrayList<>();
        if (!currentLoc.getDevices().isEmpty()) {
            locationDeviceList = mapLocationDevices(currentLoc.getDevices());
        } else {
            messageController.getResponse().reply("Sorry, could not find any rooms");
        }
        return locationDeviceList;
    }

    public List<Device> mapLocationDevices(List<de.iolite.app.api.environment.Device> envDev){
        String envDevId;
        List<Device> mappedDevices = new ArrayList<>();

        //loop through environment devices
        for (de.iolite.app.api.environment.Device envD: envDev) {
            envDevId = envD.getIdentifier();
            //loop through deviceApi for devices found in the environmentApi
            for (Device nD: messageController.getApp().getDeviceAPI().getDevices()) {
                if(nD.getIdentifier().equals(envDevId)){
                    mappedDevices.add(nD);
                }
            }
        }
        return mappedDevices;
    }

    private void turnSpecificDevice(Device devLoc) {
        DeviceBooleanProperty onProperty = devLoc.getBooleanProperty(DriverConstants.PROPERTY_on_ID);

        boolean on_off = false;
        String sOn_off = "off";

        if (messageController.on_off == "on") {
            on_off = true;
            sOn_off = "on";
        }

        if (devLoc != null) {

            if (onProperty == null) {
                messageController.getResponse().reply("The device doesn't has an on property..");
            } else {
                try {
                    onProperty.requestValueUpdate(on_off);
                    messageController.getResponse().reply(devLoc.getName() + " was turned " + sOn_off);
                } catch (DeviceAPIException e) {
                    messageController.getResponse().reply("Error while switching on/off " + devLoc.getIdentifier());
                    LOGGER.debug(e.getMessage());
                }
            }
        } else {
            messageController.getResponse().reply("Could not find the device");
        }
    }
}
