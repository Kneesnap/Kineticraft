package net.kineticraft.lostcity.dungeons.commands;

import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.command.BlockCommandSender;

/**
 * Play a NBS file for all players in a dungeon.
 * Created by Kneesnap on 8/6/2017.
 */
public class CommandDMusic extends DungeonCommand {

    public CommandDMusic() {
        super("<nbs> [repeat]", "Play a nbs sound for everyone in the dungeon.", "dmusic");
    }

    @Override
    protected void onCommand(BlockCommandSender sender, String[] args) {
        Utils.playNBS(getDungeon(sender).getPlayers(), args[0], isArg(args, 1, "repeat"));
    }
}
