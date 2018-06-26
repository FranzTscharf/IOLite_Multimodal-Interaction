package de.iolite.apps.example.slack;

import org.riversun.slacklet.Slacklet;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;

public class CustomSlackLet extends Slacklet {

    @Override
    public void onDirectMessagePosted(SlackletRequest req, SlackletResponse resp) {
        // BOT received direct message from user
        // get message content
        String content = req.getContent();
        // reply to the user
        resp.reply("You say '" + content + "'.");
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
