package de.iolite.apps.ioliteslackbot.messagecontroller;

import de.iolite.app.api.device.DeviceAPIException;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceBooleanProperty;
import de.iolite.app.api.environment.Location;
import de.iolite.apps.ioliteslackbot.IoLiteSlackBotApp;
import de.iolite.apps.ioliteslackbot.messagecontroller.MessageController;
import de.iolite.drivers.basic.DriverConstants;
import org.riversun.slacklet.Slacklet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;


public class ConversationStatusEnum {

    public static void main(String[] args) {
        ConversationStatusEnum statusEnum = new ConversationStatusEnum();
        statusEnum.setStatus(Status.NewConversation);
        System.out.println(statusEnum.getStatus());
        System.out.println(statusEnum.locIsRequired());
    }

    private Status status;
    private static Status curStatus;

    public static boolean locIsRequired() {
        if(getStatus().equals(Status.RequireLocationInformation)){
            return true;
        }else{
            return false;
        }
    }

    public static void setStatus (Status status){
        curStatus = status;
    }

    public static Status getStatus(){
        Status currentStatus = curStatus;
        return  currentStatus;
    }

    public enum Status {
        NewConversation, RequireLocationInformation,
    }
}