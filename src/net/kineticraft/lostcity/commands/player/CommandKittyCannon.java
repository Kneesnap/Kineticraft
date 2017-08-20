package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;

/**
 * Summon an exploding kitty.
 * Created by Kneesnap on 8/19/2017.
 */
public class CommandKittyCannon extends PlayerCommand {
    public CommandKittyCannon() {
        super(EnumRank.THETA, "", "Summon an explosive kitty", "kittycannon");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        Ocelot cat = (Ocelot) p.getWorld().spawnEntity(p.getLocation(), EntityType.OCELOT);
        if (cat == null)
            return; // Probably in a protected area.

        cat.setCatType(Utils.randElement(Ocelot.Type.values()));
        cat.setTamed(true);
        cat.setBaby();
        cat.setVelocity(p.getEyeLocation().getDirection().multiply(2));

        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            cat.getWorld().createExplosion(cat.getLocation(), 0F);
            cat.remove();
        }, 20L);
    }
}
