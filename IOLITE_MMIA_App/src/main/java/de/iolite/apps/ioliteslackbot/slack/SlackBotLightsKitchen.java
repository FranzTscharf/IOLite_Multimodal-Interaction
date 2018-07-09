package de.iolite.apps.ioliteslackbot.slack;

        import de.iolite.apps.ioliteslackbot.IoLiteSlackBotApp;
        import org.riversun.slacklet.Slacklet;
        import org.riversun.slacklet.SlackletRequest;
        import org.riversun.slacklet.SlackletResponse;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;

        import javax.annotation.Nonnull;

        import static de.iolite.apps.ioliteslackbot.slack.SlackDirectMessageController.getDecisionTree;

public class SlackBotLightsKitchen extends Slacklet {
    private IoLiteSlackBotApp app;

    public void setApp(IoLiteSlackBotApp setApp){
        this.app = setApp;
    }


    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(IoLiteSlackBotApp.class);

    @Override
    public void onDirectMessagePosted(SlackletRequest req, SlackletResponse resp) {
        // BOT received direct message from user
        // get message content
        //String content = req.getContent();
        // reply to the user
        //resp.reply("You say '" + content + "'.");
        getDecisionTree(req,resp, app);
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
