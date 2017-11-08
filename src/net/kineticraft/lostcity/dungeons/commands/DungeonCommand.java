package net.kineticraft.lostcity.dungeons.commands;

import net.kineticraft.lostcity.commands.BlockCommand;
import net.kineticraft.lostcity.dungeons.Dungeon;
import net.kineticraft.lostcity.dungeons.Dungeons;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
        BlockCommandSender bcs = (BlockCommandSender) sender;
        if (!Dungeons.isDungeon(bcs.getBlock().getWorld())) {
            sender.sendMessage(ChatColor.RED + "This command can only be run in a dungeon.");
            return;
        }

        Dungeon d = getDungeon(bcs);
        if (d.isEditMode()) {
            sender.sendMessage(ChatColor.RED + "This command cannot be run in edit-mode.");
            return;
        }

        super.onCommand(sender, args);
        if (deleteOnExecute() && !d.isEditMode())
            bcs.getBlock().setType(Material.AIR); // Delete this command-block on execute, provided we aren't in edit-mode.
    }

    /**
     * Should this command block be deleted after it runs its command?
     * @return shouldDelete
     */
    protected boolean deleteOnExecute() {
        return true;
    }

    /**
     * Get the dungeon this command block is in.
     * @param bcs
     * @return dungeon
     */
    protected Dungeon getDungeon(BlockCommandSender bcs) {
        return Dungeons.getDungeon(bcs.getBlock());
    }
}