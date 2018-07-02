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
  private static final String INPUT_PROMPT = "> ";
  private static final int ERROR_EXIT_CODE = 1;

  /**
   * Able to
   * @param req
   * @param resp
   * @param app
   */
  public static void dialogFlow(SlackletRequest req, SlackletResponse resp, IoLiteSlackBotApp app){
    try {
      //make a request
      //#!TODO read saved apikey from iolite
      String inputNLPRequest = req.getContent();
      Result rslt = readNLPResponse("cce3c7714ef94160a045c9767cb719df", inputNLPRequest);
      resp.reply(rslt.getFulfillment().getSpeech());
      //#!TODO make a interpretation of action -> iolite
      String action = rslt.getAction();
    } catch(Exception ex){
      LOGGER.error(ex.getMessage());
    }
  }

  /**
   * Test the interation with the DialogFlow API
   * @param args
   */
  public static void main(String[] args) {
    //interaction("cce3c7714ef94160a045c9767cb719df");
    System.out.println(
      readNLPResponse("cce3c7714ef94160a045c9767cb719df","How are you?").getFulfillment().getSpeech()
    );
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
