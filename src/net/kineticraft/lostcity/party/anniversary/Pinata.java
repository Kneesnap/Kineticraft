package net.kineticraft.lostcity.party.anniversary;

import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.party.Parties;
import net.kineticraft.lostcity.party.games.FreeplayGame;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Pinata-whacking game.
 * Created by Kneesnap on 10/6/2017.
 */
public class Pinata extends FreeplayGame {

    public Pinata() {
        setArena(-111, 61, -5, -41, 127, 63);
        addSpawnLocation(-44, 63, 29, 0, 0);
    }

    @Override
    public void onJoin(Player player) {
        super.onJoin(player);
        Utils.giveItem(player, ItemManager.createItem(Material.STICK, ChatColor.RED + "Piñata Bat", "BAM! WHACK! SMACK!"));
    }

    @EventHandler
    public void onWhackBecky(PlayerInteractEvent evt) {
        if (evt.getAction() != Action.LEFT_CLICK_BLOCK || !isPlaying(evt.getPlayer()))
            return;

        evt.getPlayer().playSound(evt.getPlayer().getLocation(), Sound.BLOCK_NOTE_PLING, 1F, 1F);
        if (!Utils.randChance(15))
            return;

        Location center = new Location(Parties.getPartyWorld(), -74.5, 76, 30);
        List<Donkey> donkeys = new ArrayList<>();
        int total = Utils.randInt(8, 15);
        for (int i = 0; i < total; i++) {
            Donkey d = center.getWorld().spawn(Utils.scatter(center, 4, 0, 4), Donkey.class);
            donkeys.add(d);
            d.setCustomNameVisible(true);
            d.setCustomName(ChatColor.GREEN + "Pinata");
            d.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(new AttributeModifier("donkey", Utils.randDouble(0, 1), AttributeModifier.Operation.ADD_NUMBER));
        }
        getScheduler().runTaskLater(() -> donkeys.forEach(Entity::remove), 2400L); // Remove donkeys in 2 minutes
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent evt) {
        if (!(evt.getEntity() instanceof Donkey) || !getArena().contains(evt.getEntity().getLocation()))
            return;

    }
}
