package de.iolite.apps.ioliteslackbot.messagecontroller;

import de.iolite.app.api.device.access.Device;
import de.iolite.apps.ioliteslackbot.IoLiteSlackBotApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;



import javax.annotation.Nonnull;

import static de.iolite.apps.ioliteslackbot.slack.SlackDirectMessageController.getDecisionTree;

/**
 * Mapps devices from environmentApi to deviceApi
 *
 * @author Marc Ottenbacher
 * @since 12.07.2018
 */
public class DeviceMappingController {
    public DeviceMappingController(){}
    private static final Logger LOGGER = LoggerFactory.getLogger(IoLiteSlackBotApp.class);

    public static List<Device> mapDevices(List<de.iolite.app.api.environment.Device> envDev, IoLiteSlackBotApp app){
        String envDevId;
        List<Device> mappedDevices = new ArrayList<>();
        //loop through environment devices
        for (de.iolite.app.api.environment.Device envD: envDev) {
            envDevId = envD.getIdentifier();
            //loop through deviceApi for devices found in the environmentApi
            for (Device nD: app.getDeviceAPI().getDevices()) {
                if(nD.getIdentifier().contains(envDevId)){
                    mappedDevices.add(nD);
                }
            }
        }
        return mappedDevices;
    }

    public static Device mapDevice(Device envD, IoLiteSlackBotApp app){
        String envDevId = envD.getIdentifier();
        for (Device nD: app.getDeviceAPI().getDevices()) {
            if (nD.getIdentifier().contains(envDevId)) {
                return nD;
            }
        }
        return null;
    }
}
