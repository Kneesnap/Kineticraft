package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Swap in and out of gamemode 3.
 * Created by Kneesnap on 6/1/2017.
 */
public class CommandSpectator extends StaffCommand {
    public CommandSpectator() {
        super(EnumRank.MEDIA, "", "Toggle spectator mode.", "spec", "gm3", "spectator");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        player.setGameMode(player.getGameMode() == GameMode.SPECTATOR ? GameMode.SURVIVAL : GameMode.SPECTATOR);
    }
}
