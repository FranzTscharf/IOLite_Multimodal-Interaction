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

import static de.iolite.apps.ioliteslackbot.dialogflow.DialogFlowClientApplication.getDialogFlow;


public class SlackDirectMessageController {

    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(IoLiteSlackBotApp.class);


    public static void getDecisionTree(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app){
        switch (req.getContent()){
            case "turn all lights on":
                LOGGER.warn(req.getContent());
                lightAll(req,resp,app);
                break;
            case "switch all lights on":
                break;
            case "turn all lights off":
                System.out.print("");
                break;
            case "switch all lights off":
                break;
            case "pull up all the blinds":
                LOGGER.warn(req.getContent());
                SlackBotBlinds.allBlinds(req,resp,app);
                break;
            case "pull up the blinds":
            	LOGGER.warn(req.getContent());
                SlackBotBlinds.allBlinds(req,resp,app);
                break;
            default:
                //ask DialogFlow of a response if the other case don't fit;
                LOGGER.warn("DialogFlow Request");
                getDialogFlow(req,resp,app);
        }
    }

    public static void lightAll(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app){
        resp.reply("Sure think! I switched on the following devices:");
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
