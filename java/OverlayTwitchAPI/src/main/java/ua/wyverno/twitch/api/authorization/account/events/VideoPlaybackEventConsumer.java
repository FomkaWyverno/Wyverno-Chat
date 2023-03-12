package ua.wyverno.twitch.api.authorization.account.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.twitch4j.pubsub.events.VideoPlaybackEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.authorization.AccessTokenNoLongerValidException;
import ua.wyverno.twitch.api.authorization.Authorization;
import ua.wyverno.twitch.api.chat.ChatWebSocketServer;
import ua.wyverno.twitch.api.chat.Protocol;
import ua.wyverno.twitch.api.chat.VideoPlaybackProtocol;
import ua.wyverno.util.ExceptionToString;

import java.util.function.Consumer;

public class VideoPlaybackEventConsumer implements Consumer<VideoPlaybackEvent> {

    private static final Logger logger = LoggerFactory.getLogger(VideoPlaybackEventConsumer.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void accept(VideoPlaybackEvent event) {
        try {
            logger.debug("VideoPlaybackEvent type = " + event.getData().getType().toString());
            switch (event.getData().getType()) {
                case VIEW_COUNT -> {
                    logger.debug("VideoPlaybackEvent view-count");
                    int countViewers = event.getData().getViewers();
                    logger.debug(String.format("Stream - %s Viewers: %d",
                            Authorization.getAccountInstance()
                                    .getUsernameByIds(event.getChannelId())
                                    .map( username -> "ChannelName: " + username)
                                    .orElseGet(() -> "ChannelID: " + event.getChannelId()),
                            countViewers));

                    try {
                        Protocol p = new Protocol(Protocol.TYPE.videoPlayback, mapper.writeValueAsString(new VideoPlaybackProtocol(VideoPlaybackProtocol.TYPE.VIEW_COUNT, String.valueOf(countViewers))));

                        ChatWebSocketServer.getInstance().messageEvent(p);
                    } catch (JsonProcessingException e) {
                        logger.error(ExceptionToString.getString(e));
                    }


                }

                case STREAM_UP -> {
                    logger.debug(String.format("VideoPlaybackEvent Stream-Up | %s",
                            Authorization.getAccountInstance()
                                    .getUsernameByIds(event.getChannelId())
                                    .map(username -> "ChannelName: " + username)
                                    .orElseGet(() -> "Channel ID: " + event.getChannelId())));

                    try {
                        Protocol p = new Protocol(Protocol.TYPE.videoPlayback,
                                mapper.writeValueAsString(
                                        new VideoPlaybackProtocol(
                                                VideoPlaybackProtocol.TYPE.STREAM_UP, "")));

                        ChatWebSocketServer.getInstance().messageEvent(p);
                    } catch (JsonProcessingException e) {
                        logger.error(ExceptionToString.getString(e));
                    }
                }

                case STREAM_DOWN -> {
                    logger.debug(String.format("VideoPlaybackEvent Stream-down | %s",
                            Authorization.getAccountInstance()
                                    .getUsernameByIds(event.getChannelId())
                                    .map(username ->"ChannelName: "+username)
                                    .orElseGet(() ->"Channel ID: "+event.getChannelId())));

                    try {
                        Protocol p = new Protocol(Protocol.TYPE.videoPlayback,
                                mapper.writeValueAsString(
                                        new VideoPlaybackProtocol(
                                                VideoPlaybackProtocol.TYPE.STREAM_DOWN, "")));
                        ChatWebSocketServer.getInstance().messageEvent(p);
                    } catch (JsonProcessingException e) {
                        logger.error(ExceptionToString.getString(e));
                    }
                }
            }
        } catch (AccessTokenNoLongerValidException e) {
            logger.error(ExceptionToString.getString(e));
        }
    }
}
