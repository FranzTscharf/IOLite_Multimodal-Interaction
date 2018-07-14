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

    public void setApp(IoLiteSlackBotApp setApp){
        this.app = setApp;
        messageController = new MessageController(app);
    }

    @Override
    public void onDirectMessagePosted(SlackletRequest req, SlackletResponse resp) {
        // BOT received direct message from user
        // get message content
        //String content = req.getContent();
        // reply to the user
        //resp.reply("You say '" + content + "'.");
        //getDecisionTree(req,resp, app);
        messageController.analyze(req,resp);
    }

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
