package de.iolite.apps.ioliteslackbot.dialogflow;

import ai.api.model.Result;
import de.iolite.app.api.device.DeviceAPIException;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceBooleanProperty;
import de.iolite.apps.ioliteslackbot.IoLiteSlackBotApp;
import de.iolite.drivers.basic.DriverConstants;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;

public class DialogFlowMessageController {
    private SlackletRequest req;
    private SlackletResponse resp;
    private IoLiteSlackBotApp app;
    private Result rslt;

    public SlackletRequest getReq() {
        return req;
    }

    public void setReq(SlackletRequest req) {
        this.req = req;
    }

    public SlackletResponse getResp() {
        return resp;
    }

    public void setResp(SlackletResponse resp) {
        this.resp = resp;
    }

    public IoLiteSlackBotApp getApp() {
        return app;
    }

    public void setApp(IoLiteSlackBotApp app) {
        this.app = app;
    }

    public DialogFlowMessageController(Result rslt,SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app) {
        this.rslt = rslt;
        this.req = req;
        this.resp = resp;
        this.app = app;
    }

    public void smartHomeLightsSwitchOn(){
        resp.reply("Okay!1");
        /*
        if (rslt.getParameters().get("all").getAsBoolean()
                && !rslt.isActionIncomplete()
                && rslt.getParameters().get("device").getAsString().equals("")
                && rslt.getParameters().get("rooms").getAsString().equals("")){
            lightsSwitchOnAll();
            resp.reply("Okay!2");
        }
        */
    }

    private void lightsSwitchOnAll() {
        // iterate devices
        for (final Device device : app.getDeviceAPI().getDevices()) {
            // togle the on trigger!
            final DeviceBooleanProperty onProperty = device.getBooleanProperty(DriverConstants.PROPERTY_on_ID);
            final Boolean onValue;
            if (onProperty != null && (onValue = onProperty.getValue()) != null) {
                // get only lamps
                if (device.getIdentifier().contains("lamp") || device.getIdentifier().contains("lamp")) {
                    try {
                        onProperty.requestValueUpdate(!onValue);
                    } catch (final DeviceAPIException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
