package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.dungeons.DungeonType;
import net.kineticraft.lostcity.dungeons.Dungeons;
import net.kineticraft.lostcity.mechanics.system.BuildType;
import net.kineticraft.lostcity.mechanics.system.Restrict;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allows testing / starting dungeons without entering a portal.
 * Created by Kneesnap on 7/22/2017.
 */
@Restrict(BuildType.PRODUCTION)
public class CommandInvoke extends StaffCommand {

    public CommandInvoke() {
        super(EnumRank.MOD, "<dungeon> [edit]", "Start a dungeon.", "invoke");
        autocomplete(DungeonType.values());
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Dungeons.startDungeon((Player) sender, DungeonType.valueOf(args[0].toUpperCase()),
                args.length > 1 && args[1].equalsIgnoreCase("edit"));
    }
}
