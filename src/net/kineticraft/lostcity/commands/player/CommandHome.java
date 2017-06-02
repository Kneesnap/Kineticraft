package net.kineticraft.lostcity.commands.player;

import com.mojang.authlib.yggdrasil.request.JoinMinecraftServerRequest;
import net.kineticraft.lostcity.Home;
import net.kineticraft.lostcity.Utils;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.security.cert.CertPathBuilderSpi;

/**
 * CommandHome - Teleport to your home.
 *
 * Created by Kneesnap on 6/1/2017.
 */
public class CommandHome extends PlayerCommand {
    public CommandHome() {
        super("<home>", "Teleport home.", "home");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        KCPlayer player = KCPlayer.getWrapper((Player) sender);
        if (!player.getHomes().containsKey(args[0])) {
            showUsage(sender);
            return;
        }

        Home teleport = player.getHomes().get(args[0]);
        Utils.teleport((Player) sender, "Home", teleport.getLocation());
    }

    @Override
    protected void showUsage(CommandSender sender, String label) {
        super.showUsage(sender, label);
        KCPlayer player = KCPlayer.getWrapper((Player) sender);
        sender.sendMessage(ChatColor.GRAY + "Homes: " + Utils.join(ChatColor.GRAY + ", ", player.getHomes().keySet(),
                h -> ChatColor.GREEN + h));
    }
}
