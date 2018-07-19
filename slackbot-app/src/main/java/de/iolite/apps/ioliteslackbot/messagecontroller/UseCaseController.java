package de.iolite.apps.ioliteslackbot.messagecontroller;

import de.iolite.app.api.device.DeviceAPIException;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceBooleanProperty;
import de.iolite.app.api.environment.Location;
import de.iolite.apps.ioliteslackbot.IoLiteSlackBotApp;
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

        List<Location> currentLocs = getCurrentLocations();

        if (!currentLocs.isEmpty()){
            for (Location l: currentLocs) {
                List<Device> currentLocationDevices = getCurrentLocationDevices(l);
                for (Device device : currentLocationDevices) {
                    turnSpecificDevice(device, l.getName());
                }
            }

        }else {
            messageController.getResponse().reply("Sorry, I could not find the requested room");
        }
    }

    private List<Location> getCurrentLocations() {
        List<Location> finalRooms = new ArrayList<>();
        String request = messageController.getRequest().toLowerCase();
        List<Location> rooms = messageController.getApp().getEnvironmentAPI().getLocations();
        for (Location r: rooms) {
            if (request.contains(r.getName().toLowerCase())){
                finalRooms.add(r);
            } else if (request.equals("everywhere") || request.equals("every") || request.equals("all")){
                finalRooms.add(r);
            }
        }
        return finalRooms;
    }


    public List<Device> getCurrentLocationDevices(Location currentLoc) {
        List<Device> locationDeviceList = new ArrayList<>();
        if (!currentLoc.getDevices().isEmpty()) {
            locationDeviceList = mapLocationDevices(currentLoc.getDevices());
        } else {
            messageController.getResponse().reply("Sorry, could not find any lamps in the `" + currentLoc.getName() + "`");
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

    private void turnSpecificDevice(Device devLoc, String loc) {
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
                    messageController.getResponse().reply("`" + devLoc.getName() + "` was turned " + sOn_off + " in the `" + loc + "`");
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
