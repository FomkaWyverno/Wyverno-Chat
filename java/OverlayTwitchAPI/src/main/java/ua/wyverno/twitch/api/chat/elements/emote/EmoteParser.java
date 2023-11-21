package ua.wyverno.twitch.api.chat.elements.emote;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EmoteParser {
    private static final Logger logger = LoggerFactory.getLogger(EmoteParser.class);

    /**
     * Парсить емодзі твітча з emotesTagValue та message в елемент HTML котрий буде відображати емодзі з чату Твітча
     * @param emotesTagValue A comma-delimited list of emotes and their positions in the message. Each emote is in the form, <emote ID>:<start position>-<end position>. The position indices are zero-based.<BR>
     *                       	Список емоцій та їх позицій у повідомленні, розділених комами. Кожна емоція має вигляд: <emote id>:<start position>-<end position>. Індекси позицій починаються з нуля.
     * @param message повідомлення з чату
     * @return HTML тег який відображає емодзі з твітчу
     */
    public static List<EmoteElement> parseEmoteListFromString(String emotesTagValue, String message) {
        logger.trace("Start parse emotes from String: {}", emotesTagValue);
        String[] emotes = StringUtils.split(emotesTagValue, '/');

        return Arrays.stream(emotes)
                .map(emote -> {
                    int indexDelim = emote.indexOf(':');
                    String emoteId = emote.substring(0,indexDelim);

                    String range = emote.substring(indexDelim+1);
                    if (StringUtils.contains(range,',')) { // range Word EmoteElement in message
                        range = range.substring(0, StringUtils.indexOf(range,','));
                    }

                    int rangeIndexDelim = StringUtils.indexOf(range,'-');
                    int startRange = Integer.parseInt(range.substring(0,rangeIndexDelim));
                    int endRange = Integer.parseInt(range.substring(rangeIndexDelim+1));

                    String emoteName = message.substring(startRange,endRange+1);

                    logger.trace("Parse EmoteName: {} | EmoteId: {} | From string: {}",
                            emoteName, emoteId, emote);

                    return new EmoteElement(emoteId,emoteName);
                })
                .collect(Collectors.toList());
    }
}
