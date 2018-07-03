package de.iolite.apps.ioliteslackbot.dialogflow;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import de.iolite.apps.ioliteslackbot.IoLiteSlackBotApp;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

/**
 * Text client reads requests line by line from stdandart input.
 */
public class DialogFlowClientApplication {
  @Nonnull
  private static final Logger LOGGER = LoggerFactory.getLogger(IoLiteSlackBotApp.class);

  public static void getDialogFlow(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app){
    try {
      //make a request
      //#!TODO read saved apikey from iolite
      String inputNLPRequest = req.getContent();
      Result rslt = readNLPResponse("cce3c7714ef94160a045c9767cb719df", inputNLPRequest);
      getDialogFlowTree(req,resp,app,rslt);
    } catch(Exception ex){
      LOGGER.error(ex.getMessage());
    }
  }

  public static void getDialogFlowTree(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app, Result rslt){
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
   * Test the interation with the DialogFlow API
   * @param args
   */
  public static void main(String[] args) {
    try {
      //make a request
      //#!TODO read saved apikey from iolite
      Result rslt = readNLPResponse("cce3c7714ef94160a045c9767cb719df", "switch on the light in the kitchen");
      System.out.println(rslt.getAction());
      System.out.println(rslt.getParameters().get("room"));
      System.out.println(rslt.getFulfillment().getSpeech());
    } catch(Exception ex){
      System.out.println(ex.getMessage());
    }
  }

  /**
   * This function is used to get the response from the
   * @param apikey
   * @param inputRequest
   */
  public static Result readNLPResponse(String apikey, String inputRequest) {
    try{
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


}
