package net.kineticraft.lostcity.dungeons.commands;

import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;

/**
 * Fire a puzzle trigger when called.
 * Created by Kneesnap on 7/30/2017.
 */
public class CommandPuzzleTrigger extends DungeonCommand {
    public CommandPuzzleTrigger() {
        super("<trigger>", "Execute a puzzle trigger.", "ptrigger");
    }

    @Override
    protected void onCommand(BlockCommandSender sender, String[] args) {
        getDungeon(sender).triggerPuzzles(args[0], (CommandBlock) sender.getBlock().getState());
    }

    @Override
    protected boolean deleteOnExecute() {
        return false;
    }
}
