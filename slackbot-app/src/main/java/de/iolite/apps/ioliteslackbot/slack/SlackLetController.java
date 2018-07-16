package de.iolite.apps.ioliteslackbot.slack;

import de.iolite.apps.ioliteslackbot.IoLiteSlackBotApp;
import de.iolite.apps.ioliteslackbot.messagecontroller.MessageController;

import org.riversun.slacklet.Slacklet;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import static de.iolite.apps.ioliteslackbot.slack.SlackDirectMessageController.getDecisionTree;

public class SlackLetController extends Slacklet {
    private IoLiteSlackBotApp app;
    private MessageController messageController;

    /**
     * This function is the setter for the iolite app to access the api's
     * @param setApp
     */
    public void setApp(IoLiteSlackBotApp setApp){
        this.app = setApp;
        messageController = new MessageController(app);
    }

    /**
     * This function just redirects our message to our message Controller
     * and it gets triggered if a user writes the bot driectly in a private conversation
     * @param req
     * @param resp
     */
    @Override
    public void onDirectMessagePosted(SlackletRequest req, SlackletResponse resp) {
        messageController.analyze(req,resp);
    }

    /**
     * This function gets triggered if the user mentiones the bot in a conversation
     * @param req
     * @param resp
     */
    @Override
    public void onMentionedMessagePosted(SlackletRequest req, SlackletResponse resp) {
        // BOT received message mentioned to the BOT such like "@bot How are you?"
        // from user.
        String content = req.getContent();
        // get 'mention' text formatted <@user> of sender user.
        String mention = req.getUserDisp();
        resp.reply("Hi," + mention + ". You say '" + content + "'.");
    }
}
