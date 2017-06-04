package net.kineticraft.lostcity.commands;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.entity.Player;

/**
 * StaffCommand - Represents a command executable by staff.
 *
 * Created by Kneesnap on 6/1/2017.
 */
public abstract class StaffCommand extends PlayerCommand {

    public StaffCommand(String usage, String help, String... alias) {
        this(EnumRank.HELPER, usage, help, alias);
    }

    public StaffCommand(EnumRank minRank, String usage, String help, String... alias) {
        this(minRank, true, usage, help, alias);
    }

    public StaffCommand(EnumRank minRank, boolean playerOnly, String usage, String help, String... alias) {
        super(minRank, playerOnly, usage, help, alias);
    }
}
