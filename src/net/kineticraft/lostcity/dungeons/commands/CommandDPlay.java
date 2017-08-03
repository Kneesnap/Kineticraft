package net.kineticraft.lostcity.dungeons.commands;

import org.bukkit.command.BlockCommandSender;

/**
 * Play a cutscene to all players in a dungeon.
 * Created by Kneesnap on 7/30/2017.
 */
public class CommandDPlay extends DungeonCommand {

    public CommandDPlay() {
        super("<cutscene>", "Play a cutscene for all players in the dungeon.", "dplay");
    }

    @Override
    protected void onCommand(BlockCommandSender sender, String[] args) {
        getDungeon(sender).playCutscene(args[0]);
    }
}
