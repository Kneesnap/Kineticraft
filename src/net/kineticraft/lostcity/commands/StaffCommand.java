package net.kineticraft.lostcity.commands;

import net.kineticraft.lostcity.EnumRank;

/**
 * StaffCommand - Represents a command executable by staff.
 * Created by Kneesnap on 6/1/2017.
 */
public abstract class StaffCommand extends PlayerCommand {

    public StaffCommand(String usage, String help, String... alias) {
        this(EnumRank.TRIAL, usage, help, alias);
    }

    public StaffCommand(EnumRank minRank, String usage, String help, String... alias) {
        super(minRank, usage, help, alias);
    }
}
