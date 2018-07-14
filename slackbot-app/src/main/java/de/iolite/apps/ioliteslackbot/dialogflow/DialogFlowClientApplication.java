package de.iolite.apps.ioliteslackbot.dialogflow;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.environment.Location;
import de.iolite.apps.ioliteslackbot.IoLiteSlackBotApp;
import de.iolite.apps.ioliteslackbot.dialogflow.model.Entities;
import de.iolite.apps.ioliteslackbot.dialogflow.model.Room;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * Management Class of DialogFlow to
 * Communicate to the AI API
 */
public class DialogFlowClientApplication {
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(IoLiteSlackBotApp.class);
    /**
     * This method is called if our algorithm did't found any suitable command
     * that means we ask dialogflow if he knows what the user whats
     * @param req
     * @param resp
     * @param app
     */
    public static void getDialogFlow(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app) {
        try {
            //make a request
            //#!TODO read saved apikey from iolite
            String inputNLPRequest = req.getContent();
            Result rslt = getNLPResponse("cce3c7714ef94160a045c9767cb719df", inputNLPRequest);
            getDialogFlowTree(req, resp, app, rslt);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    /**
     * Cast the actions response got from the AI API
     * @param req  SlackletRequest
     * @param resp SlackletResponse
     * @param app  the complete IoLiteSlackBotApp
     * @param rslt The Result of the success query
     */
    public static void getDialogFlowTree(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app, Result rslt) {
        String action = rslt.getAction();
        // case descision of intents
        switch (action) {
            case "smarthome.lights.switch.on":
                LOGGER.warn(rslt.getParameters().get("room").getAsString());
                LOGGER.warn("smarthome.lights.switch.on");
                resp.reply(rslt.getFulfillment().getSpeech());
            case "smarthome.lights.switch.check.on":
                LOGGER.warn(rslt.getParameters().get("room").getAsString());
                LOGGER.warn("smarthome.lights.switch.check.on");
                resp.reply(rslt.getFulfillment().getSpeech());
            default:
                LOGGER.warn("if nothing works");
                resp.reply(rslt.getFulfillment().getSpeech());
        }

    }

    /**
     * This main can be used for debugging purchases
     * the test the interation with the DialogFlow API
     * @param args of the console execution
     */
    public static void main(String[] args) {
        try {
            //make a request
            //#!TODO read saved apikey from iolite
            Result rslt = getNLPResponse("cce3c7714ef94160a045c9767cb719df", "switch on the light in the kitchen");
            System.out.println(rslt.getAction());
            System.out.println(rslt.getParameters().get("room"));
            System.out.println(rslt.getFulfillment().getSpeech());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * This function is used to get the response from the
     *
     * @param apikey
     * @param inputRequest
     */
    public static Result getNLPResponse(String apikey, String inputRequest) {
        try {
            AIConfiguration configuration = new AIConfiguration(apikey);
            AIDataService dataService = new AIDataService(configuration);
            AIRequest request = new AIRequest(inputRequest);
            AIResponse response = dataService.request(request);
            if (response.getStatus().getCode() == 200) {
                return response.getResult();
            } else {
                System.err.println(response.getStatus().getErrorDetails());
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void setEntities(IoLiteSlackBotApp app) {
        try {
            String ApiKey = "f8a3214ac92843b1b31f887d857db8da";
            setEntityRooms(app, ApiKey);
            setEntityDevices(app, ApiKey);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void setEntityDevices(IoLiteSlackBotApp app,String apikey) throws Exception{
        Entities rooms = getEntitiesAsDevices(app);
        String roomAsJson = getEntitiesAsJson(rooms);
        HttpResponse resp = setEntitiesAPI(roomAsJson, apikey);
        // !TODO in prod. delete warning
        LOGGER.warn("Devices to AI API Status Code:"+resp.getStatusLine().getStatusCode());
    }
    public static void setEntityRooms(IoLiteSlackBotApp app,String apikey) throws Exception{
        Entities rooms = getEntitiesAsRooms(app);
        String roomAsJson = getEntitiesAsJson(rooms);
        HttpResponse resp = setEntitiesAPI(roomAsJson, apikey);
        // !TODO in prod. delete warning
        LOGGER.warn("Rooms to AI API Status Code:"+resp.getStatusLine().getStatusCode());
    }

    public static HttpResponse setEntitiesAPI(String jsonEnterties, String developerAPIKey) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPut request = new HttpPut("https://api.api.ai/v1/entities?v=20150910");
        StringEntity params = new StringEntity(jsonEnterties);
        request.addHeader("content-type", "application/json; charset=utf-8");
        request.addHeader("Authorization", "Bearer "+developerAPIKey);
        request.addHeader("Accept", "application/json");
        request.setEntity(params);
        HttpResponse response = httpClient.execute(request);
        return response;
    }

    public static Entities getEntitiesAsRooms(IoLiteSlackBotApp app) throws Exception{
        Entities entities = new Entities("room", new ArrayList());
        for (Location location :app.getEnvironmentAPI().getLocations()){
            Room room = new Room(location.getIdentifier());
            room.addSynonym(location.getName());
            entities.getEntries().add(room);
        }
        return entities;
    }

    public static Entities getEntitiesAsDevices(IoLiteSlackBotApp app) throws Exception{
        Entities entities = new Entities("device", new ArrayList());
        for (Device device :app.getDeviceAPI().getDevices()){
            de.iolite.apps.ioliteslackbot.dialogflow.model.Device deviceModel
                    = new de.iolite.apps.ioliteslackbot.dialogflow.
                    model.Device(device.getIdentifier());
            deviceModel.addSynonym(device.getName());
            entities.getEntries().add(deviceModel);
        }
        return entities;
    }


    public static String getEntitiesAsJson(Entities entities) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            return mapper.writeValueAsString(entities);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


}
