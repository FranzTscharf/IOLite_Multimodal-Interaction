import ai.api.model.Result;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.iolite.apps.ioliteslackbot.dialogflow.model.Entities;
import de.iolite.apps.ioliteslackbot.dialogflow.model.Entity;
import de.iolite.apps.ioliteslackbot.dialogflow.model.Room;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import java.util.ArrayList;

import static de.iolite.apps.ioliteslackbot.dialogflow.DialogFlowClientApplication.getNLPResponse;

public class DialogFlowTests {

    @Test
    public void basicCommand(){
        String inputNLPRequest = "turn all light on";
        Result rslt = getNLPResponse("f8a3214ac92843b1b31f887d857db8da", inputNLPRequest);
        rslt.getFulfillment();
    }
    @Test
    public void insertCommand(){
        try{
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPut request = new HttpPut("https://api.api.ai/v1/entities?v=20150910");
            StringEntity params = new StringEntity("{'name':'room','entries':[" +
                    "{'value':'Coffee Maker','synonyms':['coffee maker','coffee machine','coffee']}," +
                    "{'value':'Thermostat','synonyms':['Thermostat','heat','air conditioning']}," +
                    "{'value':'Lights','synonyms':['lights','light','lamps']}," +
                    "{'value':'Garage door','synonyms':['garage door','garage']}" +
                    "]}");
            request.addHeader("content-type", "application/json; charset=utf-8");
            request.addHeader("Authorization", "Bearer f8a3214ac92843b1b31f887d857db8da");
            request.addHeader("Accept", "application/json");
            request.setEntity(params);
            //Execute and get the response.
            HttpResponse response = httpClient.execute(request);
            response.getStatusLine();
            response.getEntity();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @Test
    public void jsonEntitiesRoomTest(){
        ObjectMapper mapper = new ObjectMapper();
        Room room = new Room("kitchen");
        room.addSynonym("kitchen");
        ArrayList<Entity> list = new ArrayList();
        list.add(room);
        Entities entities = new Entities("room",list);
        String jsonInString = null;
        try {
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            jsonInString = mapper.writeValueAsString(entities);
            System.out.println(jsonInString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }



}
