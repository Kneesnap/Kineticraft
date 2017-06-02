package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Gives the player information about dungeons.
 *
 * Created by Kneesnap on 6/1/2017.
 */
public class CommandDungeon extends PlayerCommand {


    public CommandDungeon() {
        super("", "Display information about Dungeons.", "dungeon", "dungeons");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GREEN + "Dungeons are an upcoming feature for Kineticraft.");
        sender.sendMessage(ChatColor.GREEN + "They are epic adventures powered by command blocks.");
        sender.sendMessage(ChatColor.GREEN + "Stay tuned for more information.");
    }
}
