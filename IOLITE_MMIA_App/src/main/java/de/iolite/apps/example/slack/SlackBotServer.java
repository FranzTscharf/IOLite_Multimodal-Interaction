package de.iolite.apps.example.slack;

import org.riversun.slacklet.SlackletService;

import java.io.IOException;

public class SlackBotServer {
        private static SlackletService slackService = null;
        private static String APIKEY;
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

        public SlackBotServer() throws IOException {
            slackService = new SlackletService(APIKEY);
            slackService.addSlacklet(new SlackLetController());
            slackService.start();
        }
        public SlackBotServer(String apiKey) throws IOException {
            APIKEY = apiKey;
            slackService = new SlackletService(apiKey);
            slackService.addSlacklet(new SlackLetController());
            slackService.start();
        }


}
