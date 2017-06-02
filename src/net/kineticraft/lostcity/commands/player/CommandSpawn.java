package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.Utils;
import net.kineticraft.lostcity.commands.Command;
import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Teleport to spawn.
 *
 * Created by Kneesnap on 6/1/2017.
 */
public class CommandSpawn extends PlayerCommand {
    public CommandSpawn() {
        super("", "Teleport to spawn", "spawn");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Utils.teleport((Player) sender, "Spawn", new Location(Core.getMainWorld(), 0, 22, 0, -90F, 0F));
    }
}
