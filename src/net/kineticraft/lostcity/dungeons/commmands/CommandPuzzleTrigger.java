package net.kineticraft.lostcity.dungeons.commmands;

import net.kineticraft.lostcity.dungeons.Dungeons;
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
        Dungeons.getDungeon(sender.getBlock()).triggerPuzzles(args[0], (CommandBlock) sender.getBlock().getState());
    }
}
