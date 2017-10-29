package net.kineticraft.lostcity.dungeons.dungeons.stickysituation;

import net.kineticraft.lostcity.cutscenes.Cutscenes;
import net.kineticraft.lostcity.dungeons.puzzle.Puzzle;
import net.kineticraft.lostcity.dungeons.puzzle.PuzzleTrigger;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * The first puzzle in dungeon 2.
 * Created by Kneesnap on 10/14/2017.
 */
public class ShearFinder extends Puzzle {

    public ShearFinder() {
        super(ItemDespawnEvent.getHandlerList());
    }

    @PuzzleTrigger
    public void summonShears() {
        ItemStack drop = ItemManager.createItem(Material.SHEARS, ChatColor.YELLOW + "Worn Shears", "Mr. Barley's lost shears.");
        Item item = getWorld().dropItem(scanDrop(), drop);
    }

    private Location scanDrop() { // Finds a spot to spawn the shears.
        Location loc = new Location(getWorld(), Utils.randInt(-234, -174), 155, Utils.randInt(-82, -34));
        return (loc.getBlock().getType() == Material.AIR && loc.getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR)
                ? loc : scanDrop();
    }

    @EventHandler(ignoreCancelled = true) // Prevent Barley's shears from despawning.
    public void onItemDespawn(ItemDespawnEvent evt) {
        evt.setCancelled(evt.getEntity().getItemStack().getType() == Material.SHEARS && isPuzzle(evt.getEntity()));
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent evt) {
        if (!(evt.getRightClicked() instanceof Villager) || !evt.getRightClicked().getCustomName().contains("Barley") || Cutscenes.isWatching(evt.getPlayer()) || isComplete())
            return;

        if (evt.getPlayer().getInventory().contains(Material.SHEARS)) {
            evt.getPlayer().getInventory().remove(Material.SHEARS);
            complete();
        } else {
            evt.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Mr. Barley> " + ChatColor.GREEN + "We need my shears to move onward. Help me search from them!");
        }
    }
}
