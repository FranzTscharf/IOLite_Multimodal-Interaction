import de.iolite.apps.example.slack.SlackBotServer;
import de.iolite.apps.example.slack.SlackLetController;
import org.junit.Test;
import org.riversun.slacklet.SlackletService;
import org.riversun.xternal.simpleslackapi.SlackUser;

public class SlackBotTests {
    /*
    @Test
    public void serverStartConfig(){
        try {
            SlackletService slackService = new SlackletService("xoxb-13855094563-364153675334-tOcIgTMrUrqXdFmxpV38fvLM");
            SlackLetController csl = new SlackLetController();
            slackService.addSlacklet(csl);
            slackService.start();
            SlackUser su = slackService.getSlackSession().findUserByEmail("tscharffranz@gmail.com");
            //slackService.sendDirectMessageTo(su,"Hello!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void serverStartConfigShort(){
        try {
            SlackBotServer sbs = new SlackBotServer("xoxb-13855094563-364153675334-tOcIgTMrUrqXdFmxpV38fvLM");
            SlackUser su = sbs.getSlackService().getSlackSession().findUserByEmail("tscharffranz@gmail.com");
            //sbs.getSlackService().sendDirectMessageTo(su,"Hello, the credentials are correct. You did everything perfectly.");
            sbs.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

}
