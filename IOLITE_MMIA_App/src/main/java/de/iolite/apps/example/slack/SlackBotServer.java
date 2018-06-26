package de.iolite.apps.example.slack;

import org.riversun.slacklet.Slacklet;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import org.riversun.slacklet.SlackletService;

import java.io.IOException;

public class SlackBotServer {
        private static SlackletService slackService = null;
        private static String APIKEY;
        public void setAPIKEY(String apikey) throws IOException {
            this.APIKEY = apikey;
            slackService.stop();
            slackService = null;
            slackService = new SlackletService(APIKEY);
            slackService.addSlacklet(new CustomSlackLet());
            slackService.start();
        }
        public String getAPIKEY(){
            return this.APIKEY;
        }
        public void stopServer() throws IOException {
            slackService.stop();
        }
        public void startServer() throws IOException {
            slackService.start();
        }

        public SlackBotServer() throws IOException {
            slackService = new SlackletService(APIKEY);
            slackService.addSlacklet(new CustomSlackLet());
            slackService.start();
        }
        public SlackBotServer(String apiKey) throws IOException {
            this.APIKEY = apiKey;
            slackService = new SlackletService(apiKey);
            slackService.addSlacklet(new CustomSlackLet());
            slackService.start();
        }


}
