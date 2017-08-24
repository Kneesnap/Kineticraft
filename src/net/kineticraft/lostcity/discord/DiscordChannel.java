package net.kineticraft.lostcity.discord;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.kineticraft.lostcity.utils.ServerUtils;

import java.util.Arrays;

/**
 * List of discord channels the code explicity needs to send messages too.
 * These are ones like Oryx, which will have a message sent when Core.warn is called.
 * Created by Kneesnap on 6/28/2017.
 */
@AllArgsConstructor @Getter
public enum DiscordChannel {

    ANNOUNCEMENTS(316722073567232001L),
    REPORTS(346101298732597249L),
    INGAME(199294638467710976L),
    STAFF_CHAT(199243469766656001L),
    ORYX(329817943204560909L);

    private final long channelId;

    /**
     * Get the JDA discord channel.
     * @return channel.
     */
    public MessageChannel getChannel() {
        return DiscordAPI.isAlive() ? DiscordAPI.getBot().getBot().getTextChannelById(getChannelId()) : null;
    }

    /**
     * Get a custom discord channel from a JDA channel.
     * @param channel
     * @return channel
     */
    public static DiscordChannel getChannel(MessageChannel channel) {
        return DiscordAPI.isAlive() ?
                Arrays.stream(values()).filter(dc -> dc.getChannel().equals(channel)).findAny().orElse(null) : null;
    }
}
