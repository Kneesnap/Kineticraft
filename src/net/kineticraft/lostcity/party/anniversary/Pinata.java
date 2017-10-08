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
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Pinata-whacking game.
 * Created by Kneesnap on 10/6/2017.
 */
public class Pinata extends FreeplayGame {

    public Pinata() {
        setArena(-111, 61, -5, -41, 127, 63);
        addSpawnLocation(-44, 63, 29, 0, 0);
        setExit(-33, 62.5, 24.5, 70, 0);
    }

    @EventHandler
    public void onWhackBecky(PlayerInteractEvent evt) {
        if (evt.getAction() != Action.LEFT_CLICK_BLOCK || !isPlaying(evt.getPlayer())
                || evt.getClickedBlock().getType() != Material.WOOL || evt.getClickedBlock().getY() < 68
                || !evt.getPlayer().hasPotionEffect(PotionEffectType.LEVITATION))
            return;

        evt.getPlayer().playSound(evt.getPlayer().getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1F, 1F);
        if (!Utils.randChance(20))
            return;

        Location center = new Location(Parties.getPartyWorld(), -74.5, 76, 30);
        Zombie z = center.getWorld().spawn(center, Zombie.class);
        z.setCustomNameVisible(true);
        z.setCustomName(ChatColor.RED + "Piñata");

        z.getEquipment().setHelmet(ItemManager.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZn"
                + "QubmV0L3RleHR1cmUvM2IyNTI2NmQ0MGNlY2Q5M2QwNTMxNTZlNGE0YTc4NDE0MGQwMzQyNTVjNzIxY2MzNzVkMWMzNjQ4MzQyYjZmZCJ9fX0",
                "Pinata Skull", "He doesn't want to party anymore."));
        z.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        z.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        z.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
        z.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
        z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.4F);
        z.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(6);
        z.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
        z.setSilent(true);
        z.setGlowing(true);
        getScheduler().runTaskLater(z::remove, 1200L); // Remove zombie in 1 minute.
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent evt) { // If a pinata is killed, have a 12% chance of dropping something extra.
        if (evt.getEntity() instanceof Zombie && getArena().contains(evt.getEntity().getLocation()) && Utils.randChance(8))
            evt.getDrops().add(Utils.randElement(
                ItemManager.createItem(Material.DIAMOND, ChatColor.AQUA + "Rock Candy", "So delicious, so rare."),
                ItemManager.createItem(Material.CAKE, ChatColor.LIGHT_PURPLE + "Birthday Cake", "Happy 3rd Anniversary, Kineticraft!"),
                ItemManager.createItem(Material.SEA_LANTERN, ChatColor.RED + "Jaw Breaker", "Suck it, Piñatas!"),
                ItemManager.createItem(Material.IRON_BLOCK, ChatColor.RED + "Hershey's Kiss", "Mmmm, so chocolatey."),
                ItemManager.createItem(Material.TNT, ChatColor.LIGHT_PURPLE + "Pop Rocks", "An explosion for your taste buds."),
                ItemManager.createItem(Material.MAGMA_CREAM, ChatColor.RED + "Atomic Fireball", "It burns so good."),
                ItemManager.createItem(Material.EXP_BOTTLE, ChatColor.AQUA + "Soda", "Refreshing and fizzy."),
                ItemManager.createItem(Material.SLIME_BALL, ChatColor.AQUA + "Taffy", "Chewy...careful not to glue your teeth together!"),
                ItemManager.createItem(Material.NETHER_WARTS, ChatColor.RED + "Twizzlers", "You're the sweetest."),
                ItemManager.createItem(Material.MYCEL, ChatColor.RED + "Fudge", "What the fudge!?"),
                ItemManager.createItem(Material.WEB, ChatColor.LIGHT_PURPLE + "Cotton Candy", "So fluffy!"),
                ItemManager.createItem(Material.RAW_FISH, (byte) 1, ChatColor.LIGHT_PURPLE + "Swedish Fish", "A yummy, gummy candy."),
                ItemManager.createItem(Material.RED_ROSE, (byte) 2, ChatColor.AQUA + "Lollipop", "I'm a sucker for puns."),
                ItemManager.createItem(Material.COOKIE, ChatColor.YELLOW + "Birthday Cookie", "You're one smart cookie!"),
                ItemManager.createItem(Material.BLAZE_ROD, ChatColor.RED + "Hot Tamale", "You're one hot Tamale!"),
                ItemManager.createItem(Material.PUMPKIN_PIE, ChatColor.GOLD + "Seasonal Treat", "Pumpkin Spice flavor ALL THE THINGS!"),
                evt.getEntity().getEquipment().getHelmet()));
    }
}
