package net.kineticraft.lostcity.data;

import lombok.Getter;
import net.kineticraft.lostcity.data.lists.JsonList;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Store information about a player when they die.
 * Created by Kneesnap on 7/18/2017.
 */
@Getter
public class PlayerDeath implements Jsonable {

    private Location location;
    private String inventory;
    private int xpLost;

    public PlayerDeath() {

    }

    public PlayerDeath(PlayerDeathEvent evt) {
        this.location = evt.getEntity().getLocation();
        this.inventory = new JsonList<>(evt.getDrops()).toJsonString();
        this.xpLost = evt.getEntity().getLevel();
    }

    /**
     * Restore a player to this death point.
     * @param player
     */
    @SuppressWarnings("unchecked")
    public void restore(Player player) {
        player.setLevel(getXpLost());
        Utils.giveItems(player, JsonSerializer.fromJson(JsonList.class, getInventory(), ItemStack.class));
        player.sendMessage(ChatColor.GOLD + "Death point restored.");
    }
}
