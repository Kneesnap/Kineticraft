package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.config.Configs;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;

/**
 * Created by Kneesnap on 6/10/2017.
 */
public class CommandVote extends PlayerCommand {
    public CommandVote() {
        super("", "Information on voting.", "vote");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        ComponentBuilder cb = new ComponentBuilder("Click ").color(ChatColor.GRAY)
                .append("HERE").color(ChatColor.AQUA).bold(true).underlined(true)
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, Configs.getMainConfig().getVoteURL()))
                .append(" to vote for a reward.").bold(false).underlined(false).color(ChatColor.GRAY);
    }
}
