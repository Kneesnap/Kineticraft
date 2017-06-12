package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import net.kineticraft.lostcity.mechanics.Vanish;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allows media+ to vanish.
 *
 * Created by Kneesnap on 6/11/2017.
 */
public class CommandVanish extends StaffCommand {

    public CommandVanish() {
        super(EnumRank.MEDIA, "", "Vanish from the game.", "vanish", "unvanish");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        KCPlayer player = KCPlayer.getWrapper((Player) sender);
        player.setVanished(!player.isVanished());
        sender.sendMessage(Utils.formatToggle("Vanished", player.isVanished()));
        Vanish.hidePlayers((Player) sender);
    }
}
