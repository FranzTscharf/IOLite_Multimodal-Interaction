package de.iolite.apps.ioliteslackbot.slack;

        import de.iolite.app.api.device.DeviceAPIException;
        import de.iolite.app.api.device.access.Device;
        import de.iolite.app.api.device.access.DeviceBooleanProperty;
        import de.iolite.app.api.environment.Location;
        import de.iolite.apps.ioliteslackbot.IoLiteSlackBotApp;
        import de.iolite.drivers.basic.DriverConstants;
        import org.riversun.slacklet.Slacklet;
        import org.riversun.slacklet.SlackletRequest;
        import org.riversun.slacklet.SlackletResponse;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;

        import java.awt.*;
        import java.util.ArrayList;
        import java.util.List;



        import javax.annotation.Nonnull;

        import static de.iolite.apps.ioliteslackbot.slack.SlackDirectMessageController.getDecisionTree;

public class SlackBotLightsKitchen extends Slacklet {
    private IoLiteSlackBotApp app;

    public void setApp(IoLiteSlackBotApp setApp){
        this.app = setApp;
    }
    private final ArrayList<String> wordArrayList = new ArrayList<String>();


    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(IoLiteSlackBotApp.class);

    public ArrayList getCommandArray(String initCommand) {
        for (String fullCommand : initCommand.split(" ")) {
            wordArrayList.add(fullCommand);
        }
        return wordArrayList;
    }


    public void setlightsKitchen(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app){
        //get user message
        String slackUsrMsg = req.getContent();
        //put user message into a string array (data preperation)
        ArrayList <String> usrMsg = getCommandArray(slackUsrMsg);
        // check whether kitchen was found in user message
        Location currentLoc = checkRoom(usrMsg);
        //if found turn lights on else return
        if(currentLoc!=null){
            for (final Device device : app.getDeviceAPI().getDevices()) {
                // togle the on trigger!
                final DeviceBooleanProperty onProperty = device.getBooleanProperty(DriverConstants.PROPERTY_on_ID);
                final Boolean onValue;
                if (onProperty != null && (onValue = onProperty.getValue()) != null) {
                    //get only lamps
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
    
    public Location checkRoom(ArrayList<String> usrMsg){
        List<Location> rooms = app.getEnvironmentAPI().getLocations();

        for (String temp : usrMsg){
            for(Location l : rooms) {
                if(temp == l.getName()) {
                    if (temp.toLowerCase() == "kitchen"){
                        return l;
                    }
                    else {continue;}
                }
            }
        }
        return null;
    }
}
