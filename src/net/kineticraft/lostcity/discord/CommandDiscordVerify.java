package net.kineticraft.lostcity.discord;

import net.kineticraft.lostcity.commands.DiscordCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.mechanics.Callbacks;
import net.kineticraft.lostcity.mechanics.DataHandler;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Allows players to register themselves as verified on discord.
 * Created by Kneesnap on 6/28/2017.
 */
public class CommandDiscordVerify extends DiscordCommand {

    public CommandDiscordVerify() {
        super("<name>", "Link your in-game and discord accounts.", "verify");
    }

    @Override
    protected void onCommand(DiscordSender discord, String[] args) {
        if (!Utils.isVisible(discord, args[0]))
            return;

        if (MetadataManager.updateCooldown(discord, "verify", 30 * 20))
            return;

        Player verify = Bukkit.getPlayer(args[0]);
        KCPlayer pw = KCPlayer.getWrapper(verify);
        pw.updateDiscord();

        if (pw.isVerified()) {
            discord.sendMessage(verify.getName() + " has already verified.");
            return;
        }

        if (DiscordAPI.isVerified(discord.getUser())) {
            discord.sendMessage("Your discord account is already verified.");
            return;
        }

        discord.sendMessage("Please confirm this verification in-game.\n(Discord Terms of Service: We store your discord ID upon verification)");
        verify.sendMessage(ChatColor.GREEN + discord.getName() + ChatColor.GOLD + " is trying to verify as you on discord.");
        verify.sendMessage(ChatColor.GOLD + "If you did not do this, hit '" + ChatColor.RED + "CANCEL" + ChatColor.GOLD
                + "'. Otherwise, click '" + ChatColor.GREEN + "VERIFY" + ChatColor.GOLD + "'.");

        Callbacks.promptConfirm(verify, () -> {
            verify.sendMessage(ChatColor.GOLD + "You are now verified on discord.");
            discord.sendMessage(discord.getName() + " is now verified as " + verify.getName() + "!");

            pw.setDiscordId(discord.getUser().getIdLong());
            DataHandler.getDiscordMap().put(discord.getUser().getIdLong(), verify.getUniqueId());
            pw.updateDiscord();
        }, () -> discord.sendMessage("Verification denied by " + verify.getName() + "."), "VERIFY", "CANCEL");
    }
}
