package de.iolite.apps.example.slack;

import de.iolite.apps.example.IoLiteSlackBotApp;
import org.riversun.slacklet.Slacklet;
import org.riversun.slacklet.SlackletService;

import java.io.IOException;

public class SlackBotServer {
        private IoLiteSlackBotApp app;
        private SlackletService slackService = null;
        private String APIKEY;
        public String getApiKey(){
            return APIKEY;
        }
        public void stop() {
            try {
                slackService.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void start(){
            try {
                slackService.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public SlackletService getSlackService(){
            return slackService;
        }

        public IoLiteSlackBotApp getApp() {
            return app;
        }

        public SlackBotServer(String apiKey, IoLiteSlackBotApp setApp) throws IOException {
            APIKEY = apiKey;
            app = setApp;
            slackService = new SlackletService(apiKey);
            SlackLetController slc = new SlackLetController();
            slc.setApp(app);
            slackService.addSlacklet(slc);
            slackService.start();
        }


}
