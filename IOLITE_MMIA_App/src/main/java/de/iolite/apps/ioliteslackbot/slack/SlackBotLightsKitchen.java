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
        import static de.iolite.apps.ioliteslackbot.messagecontroller.DeviceMappingController.*;


        import java.util.ArrayList;
        import java.util.List;



        import javax.annotation.Nonnull;

/**
 * Kitchen lights will be turned on here
 *
 * @author Marc Ottenbacher
 * @since 05.07.2018
 */
public class SlackBotLightsKitchen extends Slacklet {
    private IoLiteSlackBotApp app;


    public void setApp(IoLiteSlackBotApp setApp){
        this.app = setApp;
    }
    private final ArrayList<String> wordArrayList = new ArrayList<String>();



    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(IoLiteSlackBotApp.class);
/*
    public ArrayList getCommandArray(String initCommand) {
        for (String fullCommand : initCommand.split(" ")) {
            wordArrayList.add(fullCommand);
        }
        return wordArrayList;
    }
*/


    public void setlightsKitchen(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app){
        String slackUsrMsg = req.getContent();
        //ArrayList <String> usrMsg = getCommandArray(slackUsrMsg);
        List<Device> currentLocationDevices = getCurrentLocationDevices();
            for (final Device device : currentLocationDevices) {
                final DeviceBooleanProperty onProperty = device.getBooleanProperty(DriverConstants.PROPERTY_on_ID);
                final Boolean onValue;
                if (onProperty != null && (onValue = onProperty.getValue()) != null) {
                    //get only lamps
                    if (device.getModelName().toLowerCase().contains("lamp")) {
                    try {
                        //toge on propertie
                        onProperty.requestValueUpdate(!onValue);
                        //give terminal output
                        LOGGER.warn("toggling device '{}'", device.getIdentifier());
                        //return the name of the device
                        resp.reply("Swichted java -jar iolite.jarjava -jar iolite.jaron following devices "+device.getIdentifier() + "in the kitchen");
                    }
                    catch (final DeviceAPIException e) {
                        LOGGER.error("Could not switch on the lights in the kitchen", e);

                    }

                }
            }
        }

    }
    
   public List<Device> getCurrentLocationDevices() {
       List<Location> rooms = app.getEnvironmentAPI().getLocations();
       List<Device> locationDeviceList = new ArrayList<>();
       String deviceID;
       for (Location l: rooms) {
           if(l.getName().toLowerCase().contains("kitchen")){
               locationDeviceList = mapDevices(l.getDevices(), app);
           }
       }

   return locationDeviceList;
    }
}
