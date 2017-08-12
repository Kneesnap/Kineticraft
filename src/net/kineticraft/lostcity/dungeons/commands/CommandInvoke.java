package net.kineticraft.lostcity.dungeons.commands;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.dungeons.DungeonType;
import net.kineticraft.lostcity.dungeons.DungeonUsage;
import net.kineticraft.lostcity.dungeons.Dungeons;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allows testing / starting dungeons without entering a portal.
 * Created by Kneesnap on 7/22/2017.
 */
public class CommandInvoke extends StaffCommand {

    public CommandInvoke() {
        super("<dungeon> [edit]", "Start a dungeon.", "invoke");
        autocomplete(DungeonType.values());
        autocomplete("edit");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Dungeons.startDungeon((Player) sender, DungeonType.valueOf(args[0].toUpperCase()),
                isArg(args, 1, "edit") ? DungeonUsage.EDIT : DungeonUsage.TEST);
    }
}
