package ua.wyverno.twitch.api.authorization.account.events;

import com.github.twitch4j.pubsub.domain.ChannelPointsRedemption;
import com.github.twitch4j.pubsub.domain.ChannelPointsReward;
import com.github.twitch4j.pubsub.domain.ChannelPointsUser;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.authorization.AccessTokenNoLongerValidException;
import ua.wyverno.twitch.api.authorization.Authorization;
import ua.wyverno.twitch.api.chat.ChatWebSocketServer;
import ua.wyverno.twitch.api.chat.Protocol;
import ua.wyverno.util.ExceptionToString;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class RewardRedemptionEventConsumer implements Consumer<RewardRedeemedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(RewardRedemptionEventConsumer.class);

    private static final String TEMPLATE_DEFAULT;
    private static final String TEMPLATE_TEXT;

    static {
        String tmp1, tmp2;
        try {
            tmp1 = Files.readString(Paths.get("html/overlay/elements/reward_default.html"));
            tmp2 = Files.readString(Paths.get("html/overlay/elements/reward_text.html"));
        } catch (IOException e) {
            tmp1 = "DEFAULT TEMPLATE FOR REWARD NOT LOADED!";
            tmp2 = "TEMPLATE WITH TEXT FOR REWARD NOT LOADED!";
        }

        TEMPLATE_DEFAULT = tmp1;
        TEMPLATE_TEXT = tmp2;
    }

    @Override
    public void accept(RewardRedeemedEvent rewardRedeemedEvent) {

        ChannelPointsRedemption redemption = rewardRedeemedEvent.getRedemption();
        ChannelPointsReward reward = redemption.getReward();
        ChannelPointsUser user = redemption.getUser();

        String title = reward.getTitle();
        String cost = String.valueOf(reward.getCost());
        String username = user.getDisplayName();
        String urlRewardIcon = redemption.getReward().getDefaultImage().getUrl4x();
        boolean isUserInputRequired = reward.getIsUserInputRequired();
        String message;

        logger.info("Reward bought!");
        logger.info("Username: " + username);
        logger.info("Title: " + title);
        logger.info("Cost: " + cost);
        logger.info("isUserInputRequired: " + isUserInputRequired);
        logger.info("URL Reward Icon: " + urlRewardIcon);

        String htmlContext = "";

        Protocol.TYPE type;

        if (isUserInputRequired) {
            message = redemption.getUserInput();
            logger.info("Message: " + message);
            htmlContext = this.getRewardWithTextHtmlContext(redemption);
            type = Protocol.TYPE.reward_message;
        } else {
            htmlContext = this.getDefaultRewardHtmlContext(redemption);
            type = Protocol.TYPE.reward_block;
        }

        logger.debug("HTML Context\n" + htmlContext);

        ChatWebSocketServer.getInstance().messageEvent(new Protocol(type, htmlContext));
    }

    private String getDefaultRewardHtmlContext(ChannelPointsRedemption redemption) {
        String htmlContext = TEMPLATE_DEFAULT;

        htmlContext = getDefaultMapping(htmlContext, redemption);

        return htmlContext;
    }

    private String getRewardWithTextHtmlContext(ChannelPointsRedemption redemption) {
        String htmlContext = TEMPLATE_TEXT;

        htmlContext = getDefaultMapping(htmlContext, redemption);

        String message = redemption.getUserInput();
        String color = "";
        try {
            color = Authorization.getAccountInstance().getUserChatColorByIds(redemption.getUser().getId(),"#bb00ff");
        } catch (AccessTokenNoLongerValidException e) {
            logger.error(ExceptionToString.getString(e));
        }

        htmlContext = htmlContext.replace("{message}",message);
        htmlContext = htmlContext.replace("{color-user}", color);

        return htmlContext;
    }

    @NotNull
    private String getDefaultMapping(String htmlContext, ChannelPointsRedemption redemption) {

        String username = redemption.getUser().getDisplayName();
        String reward_name = redemption.getReward().getTitle();
        String cost = String.valueOf(redemption.getReward().getCost());
        String urlRewardIcon = redemption.getReward().getImage() != null ?
                redemption.getReward().getImage().getUrl1x() :
                redemption.getReward().getDefaultImage().getUrl1x();

        htmlContext = htmlContext.replace("{username}", username);
        htmlContext = htmlContext.replace("{reward-name}", reward_name);
        htmlContext = htmlContext.replace("{reward-cost}", cost);
        htmlContext = htmlContext.replace("{url-reward-icon}", urlRewardIcon);
        return htmlContext;
    }
}
