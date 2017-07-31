package net.kineticraft.lostcity.dungeons.commmands;

import net.kineticraft.lostcity.commands.BlockCommand;
import net.kineticraft.lostcity.dungeons.Dungeons;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;

/**
 * Represents a command that can only be run in a dungeon, by a commandblock.
 * Created by Kneesnap on 7/30/2017.
 */
public abstract class DungeonCommand extends BlockCommand {

    public DungeonCommand(String usage, String help, String... alias) {
        super(usage, help, alias);
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (!Dungeons.isDungeon(((BlockCommandSender) sender).getBlock().getWorld())) {
            sender.sendMessage(ChatColor.RED + "This command can only be run in a dungeon.");
            return;
        }

        super.onCommand(sender, args);
    }
}
