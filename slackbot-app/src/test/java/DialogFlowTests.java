import ai.api.model.Result;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.iolite.apps.ioliteslackbot.dialogflow.DialogFlowClientApplication;
import de.iolite.apps.ioliteslackbot.dialogflow.model.Entities;
import de.iolite.apps.ioliteslackbot.dialogflow.model.Entity;
import de.iolite.apps.ioliteslackbot.dialogflow.model.Room;
import org.junit.Test;

import java.util.ArrayList;

public class DialogFlowTests {

    @Test
    public void basicCommand(){
        String inputNLPRequest = "turn all light on";
        DialogFlowClientApplication dfca = new DialogFlowClientApplication("f8a3214ac92843b1b31f887d857db8da");
        Result rslt = dfca.getNLPResponse(inputNLPRequest);
        rslt.getFulfillment();
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
