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
    private String dialogFlowApiKey = "";
    private SlackletRequest req;
    private SlackletResponse resp;
    private IoLiteSlackBotApp app;

    /**
     * Getter and Setter
     */
    public SlackletRequest getReq() {
        return req;
    }

    /**
     * Getter and Setter
     */
    public void setReq(SlackletRequest req) {
        this.req = req;
    }

    /**
     * Getter and Setter
     */
    public SlackletResponse getResp() {
        return resp;
    }

    /**
     * Getter and Setter
     */
    public void setResp(SlackletResponse resp) {
        this.resp = resp;
    }

    /**
     * Getter and Setter
     */
    public IoLiteSlackBotApp getApp() {
        return app;
    }

    /**
     * Getter and Setter
     */
    public void setApp(IoLiteSlackBotApp app) {
        this.app = app;
    }

    /**
     * Getter and Setter
     *
     * @param apikey
     */
    public DialogFlowClientApplication(String apikey) {
        this.dialogFlowApiKey = apikey;
    }

    public DialogFlowClientApplication(String apikey, IoLiteSlackBotApp app, SlackletRequest req, SlackletResponse resp) {
        this.dialogFlowApiKey = apikey;
        this.req = req;
        this.resp = resp;
        this.app = app;
    }

    /**
     * Getter and Setter
     *
     * @return
     */
    public String getDialogFlowApiKey() {
        return dialogFlowApiKey;
    }

    /**
     * Getter and Setter
     *
     * @param dialogFlowApiKey
     */
    public void setDialogFlowApiKey(String dialogFlowApiKey) {
        this.dialogFlowApiKey = dialogFlowApiKey;
    }

    /**
     * This method is called if our algorithm did't found any suitable command
     * that means we ask dialogflow if he knows what the user whats
     */
    public void getDialogFlow() {
        try {
            //make a request
            //#!TODO read saved apikey from iolite
            String inputNLPRequest = req.getContent();
            Result rslt = getNLPResponse(inputNLPRequest);
            getDialogFlowTree(rslt);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    /**
     * Cast the actions response got from the AI API
     *
     * @param rslt The Result of the success query
     */
    public void getDialogFlowTree(Result rslt) {
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
     *
     * @param args of the console execution
     */
    public void main(String[] args) {
        try {
            //make a request
            //#!TODO read saved apikey from iolite
            setDialogFlowApiKey("f8a3214ac92843b1b31f887d857db8da");
            Result rslt = getNLPResponse("switch on the light in the kitchen");
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
     * @param inputRequest
     */
    public Result getNLPResponse(String inputRequest) {
        try {
            AIConfiguration configuration = new AIConfiguration(dialogFlowApiKey);
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

    /**
     * Read Content(devices and rooms from iolite) and pass it to dialogflow
     */
    public void setEntities() {
        try {
            setEntityRooms();
            setEntityDevices();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Read Devices from iolite and pass it to dialogflow
     *
     * @throws Exception
     */
    public void setEntityDevices() throws Exception {
        Entities rooms = getEntitiesAsDevices();
        String roomAsJson = getEntitiesAsJson(rooms);
        HttpResponse resp = setEntitiesAPI(roomAsJson);
        // !TODO in prod. delete warning
        LOGGER.warn("Devices to AI API Status Code:" + resp.getStatusLine().getStatusCode());
    }

    /**
     * Read Rooms from iolite and pass it to dialogflow
     *
     * @throws Exception
     */
    public void setEntityRooms() throws Exception {
        Entities rooms = getEntitiesAsRooms();
        String roomAsJson = getEntitiesAsJson(rooms);
        HttpResponse resp = setEntitiesAPI(roomAsJson);
        // !TODO in prod. delete warning
        LOGGER.warn("Rooms to AI API Status Code:" + resp.getStatusLine().getStatusCode());
    }

    /**
     * Set the Json of devices or roooms to the dialogflow
     *
     * @param jsonEnterties
     * @return
     * @throws Exception
     */
    public HttpResponse setEntitiesAPI(String jsonEnterties) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPut request = new HttpPut("https://api.api.ai/v1/entities?v=20150910");
        StringEntity params = new StringEntity(jsonEnterties);
        request.addHeader("content-type", "application/json; charset=utf-8");
        request.addHeader("Authorization", "Bearer " + dialogFlowApiKey);
        request.addHeader("Accept", "application/json");
        request.setEntity(params);
        HttpResponse response = httpClient.execute(request);
        return response;
    }

    /**
     * Get the Rooms of iolite and cast it to models for the AI Request
     *
     * @return
     * @throws Exception
     */
    public Entities getEntitiesAsRooms() throws Exception {
        Entities entities = new Entities("room", new ArrayList());
        for (Location location : app.getEnvironmentAPI().getLocations()) {
            Room room = new Room(location.getIdentifier());
            room.addSynonym(location.getName());
            entities.getEntries().add(room);
        }
        return entities;
    }

    /**
     * Get the Devices of iolite and cast it to models for the AI Request
     *
     * @return
     * @throws Exception
     */
    public Entities getEntitiesAsDevices() throws Exception {
        Entities entities = new Entities("device", new ArrayList());
        for (Device device : app.getDeviceAPI().getDevices()) {
            de.iolite.apps.ioliteslackbot.dialogflow.model.Device deviceModel
                    = new de.iolite.apps.ioliteslackbot.dialogflow.
                    model.Device(device.getIdentifier());
            deviceModel.addSynonym(device.getName());
            entities.getEntries().add(deviceModel);
        }
        return entities;
    }

    /**
     * Convert the Models to Json objectcs and return the String
     *
     * @param entities
     * @return
     */
    public String getEntitiesAsJson(Entities entities) {
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
