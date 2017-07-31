package net.kineticraft.lostcity.dungeons.commmands;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.dungeons.Dungeon;
import net.kineticraft.lostcity.dungeons.Dungeons;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Exit the dungeon you are currently in.
 * Created by Kneesnap on 7/28/2017.
 */
public class CommandDQuit extends PlayerCommand {
    public CommandDQuit() {
        super("", "Exit the dungeon you are currently in.", "dquit");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        Dungeon d = Dungeons.getDungeon(p);

        if (d == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a dungeon.");
            return;
        }

        MetadataManager.updateCooldownSilently((Player) sender, "dquit", 2);
        d.removePlayer(p);
    }
}
