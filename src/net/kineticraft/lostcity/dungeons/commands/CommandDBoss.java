package net.kineticraft.lostcity.dungeons.commands;

import net.kineticraft.lostcity.dungeons.BossType;
import org.bukkit.command.BlockCommandSender;

/**
 * Spawn a boss.
 * Created by Kneesnap on 8/2/2017.
 */
public class CommandDBoss extends DungeonCommand {

    public CommandDBoss() {
        super("<boss>", "Spawn a dungeon boss.", "dboss");
    }

    @Override
    protected void onCommand(BlockCommandSender sender, String[] args) {
        getDungeon(sender).spawnBoss(BossType.valueOf(args[0].toUpperCase()));
    }
}
