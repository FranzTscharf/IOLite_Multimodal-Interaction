package de.iolite.apps.example.slack;

import de.iolite.app.api.device.DeviceAPIException;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceBooleanProperty;
import de.iolite.app.api.frontend.util.FrontendAPIRequestHandler;
import de.iolite.apps.example.IoLiteSlackBotApp;
import de.iolite.common.requesthandler.IOLITEHTTPRequest;
import de.iolite.common.requesthandler.IOLITEHTTPResponse;
import de.iolite.drivers.basic.DriverConstants;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class SlackDirectMessageController {

    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(IoLiteSlackBotApp.class);


    public static void getDecisionOfTree(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app){
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
            default:
                //here ask dialogFlow of a response;
                System.out.print("nothing of this onces");
        }
    }

    public static void lightAll(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app){
        LOGGER.warn("test");
        // go through all devices, and toggle ON/OFF properties
        for (final Device device : app.getDeviceAPI().getDevices()) {
            // let's get the 'on/off' status property
            final DeviceBooleanProperty onProperty = device.getBooleanProperty(DriverConstants.PROPERTY_on_ID);
            final Boolean onValue;
            if (onProperty != null && (onValue = onProperty.getValue()) != null) {
                resp.reply(device.getIdentifier());
                resp.reply(device.toString());
                LOGGER.warn("toggling device '{}'", device.getIdentifier());
                    try {
                        onProperty.requestValueUpdate(!onValue);
                    }
                    catch (final DeviceAPIException e) {
                        LOGGER.error("Failed to control device", e);
                    }
            }
        }
    }

}
