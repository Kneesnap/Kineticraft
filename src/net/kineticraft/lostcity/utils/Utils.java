package net.kineticraft.lostcity.utils;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.xxmicloxx.NoteBlockAPI.*;
import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.discord.DiscordSender;
import net.kineticraft.lostcity.data.lists.JsonList;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.discord.DiscordAPI;
import net.kineticraft.lostcity.item.ItemType;
import net.kineticraft.lostcity.item.ItemWrapper;
import net.kineticraft.lostcity.mechanics.metadata.Metadata;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.party.Parties;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.Directional;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utils - Contains basic static utilties.
 * Created by Kneesnap on 5/29/2017.
 */
public class Utils {

    public static List<BlockFace> FACES = Arrays.asList(BlockFace.SELF, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
    public static List<BlockFace> CUBE_FACES = Arrays.asList(BlockFace.SELF, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);
    @Getter private static Set<SongPlayer> repeat = new HashSet<>();

    /**
     * Gets an enum value from the given class. Returns null if not found.
     * @param value
     * @param clazz
     */
    public static <T extends Enum<T>> T getEnum(String value, Class<T> clazz) {
        return getEnum(value, clazz, null);
    }

    /**
     * Gets an enum value, falling back on a default value.
     * @param value
     * @param defaultValue
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T getEnum(String value, T defaultValue) {
        return getEnum(value, (Class<T>) defaultValue.getClass(), defaultValue);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T getEnum(String value, Class<T> clazz, T defaultValue) {
        try {
            return (T) clazz.getMethod("valueOf", String.class).invoke(null, value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Capitalize every letter after a space.
     * @param sentence
     * @return
     */
    public static String capitalize(String sentence) {
        String[] split = sentence.replaceAll("_", " ").split(" ");
        List<String> out = new ArrayList<>();
        for (String s : split)
            out.add(s.length() > 0 ? s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() : "");
        return String.join(" ", out);
    }

    /**
     * Is the given location 'safe'?
     * @param loc
     * @return safe
     */
    private static boolean isSafe(Location loc) {
        Block bk = loc.getBlock();
        Block below = bk.getRelative(BlockFace.DOWN);
        return !isSolid(bk) && !isSolid(bk.getRelative(BlockFace.UP)) && below.getType() != Material.AIR  && !below.isLiquid();
    }

    /**
     * Is this block solid? (Meaning players cannot walk through it)
     * @param bk
     * @return solid
     */
    public static boolean isSolid(Block bk) {
        return isSolid(bk.getType()) && !bk.isLiquid();
    }

    /**
     * Returns whether or not the given material has a collision box.
     * @param mat
     * @return solid
     */
    public static boolean isSolid(Material mat) {
        return mat.isSolid() && mat.isOccluding();
    }

    /**
     * Find the first available 'safe' teleport location.
     * @param origin
     * @return safe
     */
    public static Location findSafe(Location origin) {
        int maxHeight = origin.getWorld().getMaxHeight();
        Location safe = origin.clone();
        safe.setY(maxHeight); // Default location

        Location temp = origin.clone();
        temp.setY(0);
        while(temp.getBlockY() < maxHeight - 1) { // Don't go to the very max height because we check the block above tempY.
            temp.setY(temp.getBlockY() + 1);
            if (isSafe(temp) && origin.distanceSquared(safe) > origin.distanceSquared(temp))
                safe = temp.clone(); // This location is closer than the saved one, and it's safe.
        }
        return safe.add(0, 0.5, 0);
    }

    /**
     * Teleport the player to the specified location with cooldowns applied.
     * @param player
     * @param locationDescription
     * @param location
     */
    public static void teleport(Player player, String locationDescription, Location location) {
        if (location == null)
            return;

        if (location.getWorld().equals(Parties.getPartyWorld()) && player.getWorld().equals(Parties.getPartyWorld())) {
            player.sendMessage(ChatColor.RED + "You may not teleport to another location in the party area.");
            return;
        }

        if (MetadataManager.hasMetadata(player, Metadata.TELEPORTING)) {
            player.sendMessage(ChatColor.RED + "Please wait until your current teleport finishes.");
            return;
        }

        KCPlayer p = KCPlayer.getWrapper(player);

        double lastDamage = player.getLastDamage();
        final Location startLocation = player.getLocation().clone();
        final BukkitTask[] tpTask = new BukkitTask[1]; // Have to use an array here to store a value, as otherwise it can't be final.
        // Must be final to work in the bukkit scheduler.
        int[] tpTime = new int[] {p.getRank().isStaff() ? 0 : p.getTemporaryRank().getTpTime()};

        MetadataManager.setMetadata(player, Metadata.TELEPORTING, true);
        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1F, 1F);
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * (tpTime[0] + 2), 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * (tpTime[0] + 2), 2));
        player.sendMessage(ChatColor.BOLD + "Teleport: " + ChatColor.WHITE + ChatColor.UNDERLINE + locationDescription);

        tpTask[0] = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
                boolean complete = tpTime[0] <= -1 || !player.isOnline();
                if (complete || player.getLocation().distanceSquared(startLocation) >= 3.5 || player.getLastDamage() < lastDamage) {
                    if (!complete) {
                        player.removePotionEffect(PotionEffectType.CONFUSION);
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        player.sendMessage(ChatColor.RED + "Teleport cancelled.");
                        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1F, 2F);
                    }
                    tpTask[0].cancel();
                    MetadataManager.removeMetadata(player, Metadata.TELEPORTING);
                    return;
                }

                if (tpTime[0] > 0) {
                    player.sendMessage(ChatColor.WHITE + "Teleporting... " + ChatColor.UNDERLINE + tpTime[0] + "s");
                } else {
                    player.setNoDamageTicks(300); // 15 Seconds
                    safeTp(player, location);
                    player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1F, 1.333F);
                    KCPlayer.getWrapper(player).updatePlayer(); // Show unread mail.
                }

                tpTime[0]--;
        }, 0L, 20L);
    }

    /**
     * Get a friendly string of how much time it will take until this date is reached.
     * @param date
     * @return formatted
     */
    public static String formatDate(Date date) {
        return formatTimeFull(date.getTime() - System.currentTimeMillis());
    }

    /**
     * Turn milliseconds into a user friendly string.
     * @param time
     * @return formatted
     */
    public static String formatTime(long time) {
        if (time == -1)
            return "never";

        time /= 1000;
        String formatted = "";
        for (int i = 0; i < TimeInterval.values().length; i++) {
            TimeInterval iv = TimeInterval.values()[TimeInterval.values().length - i - 1];
            if (time >= iv.getInterval()) {
                int temp = (int) (time - (time % iv.getInterval()));
                int add = temp / iv.getInterval();
                formatted += " " + add + iv.getSuffix() + (add > 1 && iv != TimeInterval.SECOND ? "s" : "");
                time -= temp;
            }
        }
        return formatted.length() > 0 ? formatted.substring(1) : "now";
    }

    /**
     * Formats milliseconds into a user friendly display.
     * Different from formatTime because this does not use abbreviations.
     * @param time
     * @return formatted
     */
    public static String formatTimeFull(long time) {
        if (time == -1)
            return "Never";

        time /= 1000;
        String formatted = "";

        for (int i = 0; i < TimeInterval.values().length; i++) {
            TimeInterval iv = TimeInterval.values()[TimeInterval.values().length - i - 1];
            if (time >= iv.getInterval()) {
                int temp = (int) (time - (time % iv.getInterval()));
                int add = temp / iv.getInterval();
                formatted += " " + add + " " + capitalize(iv.name()) + (add > 1 ? "s" : "");
                time -= temp;
            }
        }

        return formatted.length() > 0 ? formatted.substring(1) : "Now";
    }

    /**
     * Convert user input ie: "3d 2h" into a date relative to now.
     * @param input
     * @return date
     */
    public static Date fromInput(String input) {
        long ms = System.currentTimeMillis();
        for (String s : input.split(" ")) {
            String code = s.substring(s.length() - 1, s.length());
            ms += Integer.parseInt(input.substring(0, s.length() - 1)) * TimeInterval.getByCode(code).getInterval() * 1000;
        }
        return Date.from(Instant.ofEpochMilli(ms));
    }

    /**
     * Convert a location into a friendly string.
     * @param l
     * @return formatted
     */
    public static String toString(Location l) {
       return "[" + (l.getWorld() != null ? l.getWorld().getName() : null) + "," + l.getX() + "," + l.getY() + "," + l.getZ() + "]";
    }

    /**
     * Convert a location into a friendly, clean string.
     * @param loc
     * @return clean
     */
    public static String toCleanString(Location loc) {
        return "[" + (loc != null ? (loc.getWorld() != null ? loc.getWorld().getName() : null)
                + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() : "null") + "]";
    }

    /**
     * Give an item to a player. If their inventory is full, it drops it at their feet.
     * @param player
     * @param itemStack
     */
    public static void giveItem(Player player, ItemStack itemStack) {
        if (player.getInventory().firstEmpty() > -1) {
            player.getInventory().addItem(itemStack);
        } else {
            player.getWorld().dropItem(player.getLocation(), itemStack);
            player.sendMessage(ChatColor.RED + "Your inventory was full, so you dropped the item.");
        }
    }

    /**
     * Give items to a player, if they have space. Otherwise, drop it.
     * @param player
     * @param items
     */
    public static void giveItems(Player player, ItemStack... items) {
        giveItems(player, Arrays.asList(items));
    }

    /**
     * Give items to a player, if they have space. Otherwise, drop it.
     * @param player
     * @param items
     */
    public static void giveItems(Player player, Iterable<ItemStack> items) {
        items.forEach(i -> giveItem(player,  i));
    }

    /**
     * Gets a players username by their uuid. Offline safe.
     * Returns null if this player has never joined.
     * @param uuid
     * @return name
     */
    public static String getPlayerName(UUID uuid) {
        return KCPlayer.isWrapper(uuid) ? KCPlayer.getWrapper(uuid).getUsername() : null;
    }

    /**
     * Return the display name of an ItemStack.
     * @param itemStack
     * @return Display Name
     */
    public static String getItemName(ItemStack itemStack) {
        return itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName()
                : capitalize(itemStack.getType().name().replaceAll("_", " "));
    }

    /**
     * Get a random number between the given range.
     * @param min
     * @param max
     * @return int
     */
    public static int randInt(int min, int max) {
        return max + min > 0 ? nextInt(max - min) + min : randInt(0, max - min) + min;
    }

    /**
     * Generate a random number between two doubles.
     * @param min
     * @param max
     * @return rand
     */
    public static double randDouble(double min, double max) {
        return min == max ? max : ThreadLocalRandom.current().nextDouble(min, max);
    }

    /**
     * Get a random element from an array.
     * @param arr
     * @param <T>
     * @return element
     */
    public static <T> T randElement(T... arr) {
        return randElement(Arrays.asList(arr));
    }

    /**
     * Get a random element from a json list
     * @param list
     * @param <T>
     * @return rand
     */
    public static <T extends Jsonable> T randElement(JsonList<T> list) {
        return randElement(list.getValues());
    }

    /**
     * Get a random element from a list.
     * @param iterable
     * @param <T>
     * @return element
     */
    public static <T> T randElement(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list.isEmpty() ? null : list.get(nextInt(list.size()));
    }

    /**
     * Get a random true/false value.
     * @return randomBool
     */
    public static boolean nextBool() {
        return randChance(2);
    }

    /**
     * Gets a random number between 0 and max - 1
     * @param max
     * @return rand
     */
    public static int nextInt(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

    /**
     * Check if a random chance succeeds.
     * @param chance
     * @return success
     */
    public static boolean randChance(int chance) {
        return nextInt(chance) == 0;
    }

    /**
     * Replaces an existing itemstack in a player's inventory with a new one.
     * @param player
     * @param original
     * @param newItem
     */
    public static void replaceItem(Player player, ItemStack original, ItemStack newItem) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (original.equals(player.getInventory().getItem(i))) {
                player.getInventory().setItem(i, newItem);
                player.updateInventory();
                return;
            }
        }

        Core.warn("Failed to replace " + Utils.getItemName(original) + " in " + player.getName() + "'s inventory.");
    }

    /**
     * Shave the first element of any array.
     * @param array
     * @param <T>
     * @return shavedArray
     */
    public static <T> T[] shift(T[] array) {
        return shift(array, 1);
    }

    /**
     * Shave a given number of elements from an array.
     * @param array
     * @param shave
     * @param <T>
     * @return shavedArray
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] shift(T[] array, int shave) {
        List<T> l = new ArrayList<>(Arrays.asList(array));
        for (int i = 0; i < shave; i++)
            l.remove(0);
        return l.toArray((T[]) Array.newInstance(array[0].getClass(), array.length - shave));
    }

    /**
     * Get the colored name of this CommandSender
     * @param sender
     * @return name
     */
    public static String getSenderName(CommandSender sender) {
        return hasWrapper(sender) ? KCPlayer.getWrapper(sender).getColoredName() : ChatColor.YELLOW + sender.getName();
    }

    /**
     * Use an item and decrement its amount.
     * @param item
     * @return item
     */
    public static ItemStack useItem(ItemStack item) {
        item.setAmount(item.getAmount() - 1);
        if (item.getAmount() <= 0)
            item.setType(Material.AIR);
        return item;
    }

    /**
     * Is an item equivalent to not existing?
     * @param itemStack
     * @return isAir
     */
    public static boolean isAir(ItemStack itemStack) {
        return itemStack == null || itemStack.getType() == Material.AIR;
    }

    /**
     * Formats a boolean toggle to be displayed in chat.
     * @param name
     * @param value
     * @return string
     */
    public static String formatToggle(String name, boolean value) {
        return (name != null ? ChatColor.GRAY + name + ": " : "") + (value ? ChatColor.GREEN : ChatColor.RED) + value;
    }

    /**
     * Can this CommandSender type have a wrapper?
     * @param sender
     * @return canHave
     */
    public static boolean hasWrapper(CommandSender sender) {
        return sender instanceof Player || sender instanceof DiscordSender;
    }

    /**
     * Gets the rank of a CommandSender
     * @param sender
     * @return rank
     */
    public static EnumRank getRank(CommandSender sender) {
        return hasWrapper(sender) ? KCPlayer.getWrapper(sender).getRank()
                : (sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender ? EnumRank.DEV : EnumRank.MU);
    }

    /**
     * Is the given CommandSender a staff member?
     * @param sender
     * @return isStaff
     */
    public static boolean isStaff(CommandSender sender) {
        return getRank(sender).isStaff();
    }

    /**
     * Does the first array contain any elements from the second array?
     * @param a
     * @param b
     * @param <T>
     * @return contains
     */
    public static <T> boolean containsAny(T[] a, T[] b) {
        return containsAny(Arrays.asList(a), Arrays.asList(b));
    }

    /**
     * Does the first list contain any elements from the second array?
     * @param a
     * @param b
     * @param <T>
     * @return
     */
    public static <T> boolean containsAny(List<T> a, List<T> b) {
        return b.stream().anyMatch(a::contains);
    }

    /**
     * Remove a potion with infinite duration.
     * @param player
     * @param type
     */
    public static void removeInfinitePotion(Player player, PotionEffectType type) {
        PotionEffect pe = player.getPotionEffect(type);
        if (pe != null && pe.getDuration() >= 30 * 60 * 20)
            player.removePotionEffect(type);
    }

    /**
     * Give a player an infinite potion effect, but only if they do not already have a potion of this type.
     * @param entity
     * @param type
     */
    public static void giveInfinitePotion(LivingEntity entity, PotionEffectType type) {
        if (!entity.hasPotionEffect(type))
            entity.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, 2));
    }

    /**
     * Gives or removes an infinite potion based on the 'has' parameter.
     * Respects player brewed potions, if they exist.
     *
     * @param player
     * @param type
     * @param has
     */
    public static void setPotion(Player player, PotionEffectType type, boolean has) {
        if (has) {
            giveInfinitePotion(player, type);
        } else {
            removeInfinitePotion(player, type);
        }
    }

    /**
     * Gives or removes an infinite potion based on if they do or do not already have this potion.
     * WARNING: Does not respect player-brewed potions.
     *
     * @param player
     * @param type
     * @return hasPotion (After this is called)
     */
    public static boolean togglePotion(Player player, PotionEffectType type) {
        boolean newState = !player.hasPotionEffect(type);
        setPotion(player, type, newState);
        return newState;
    }

    /**
     * Broadcast a message everywhere except to a specified player.
     * @param message
     * @param except
     */
    public static void broadcastExcept(String message, Player except) {
        getAllPlayersExcept(except).forEach(p -> p.sendMessage(message));
        Bukkit.getConsoleSender().sendMessage(message);
        DiscordAPI.sendGame(message);
    }

    /**
     * Returns a list of all players except the given player.
     * @param player
     * @return allExcept
     */
    public static List<Player> getAllPlayersExcept(Player player) {
        return Bukkit.getOnlinePlayers().stream().filter(p -> p != player).collect(Collectors.toList());
    }

    /**
     * Gets the supplied input from a resulting NFE.
     * @param nfe
     * @return input
     */
    public static String getInput(NumberFormatException nfe) {
        return nfe.getLocalizedMessage().split(": ")[1].replaceAll("\"", "");
    }

    /**
     * Find the amount of times a
     * @param search
     * @param find
     * @return
     */
    public static int getCount(String search, String find) {
        int found = 0;
        for (int i = 0; i < search.length() - find.length() + 1; i++)
            if (search.substring(i, i + find.length()).equals(find))
                found++;
        return found;
    }

    /**
     * Gives the player an item if they don't already have an item of that type.
     * Replaces it otherwise.
     *
     * @param player
     * @param item
     */
    public static void replaceItem(Player player, ItemWrapper item) {
        ItemType check = item.getType();
        for (int i = 0; i < player.getInventory().getSize(); i++ ) {
            if (check != ItemWrapper.getType(player.getInventory().getItem(i)))
                continue;
            player.getInventory().setItem(i, item.generateItem());
            return;
        }

        giveItem(player, item.generateItem());
    }

    /**
     * Get players nearby an entity. Async-Safe.
     * Does not include the calling entity.
     * @param entity
     * @param radius
     * @return players
     */
    public static List<Player> getNearbyPlayers(Entity entity, int radius) {
        List<Player> players = getNearbyPlayers(entity.getLocation(), radius);
        if (entity instanceof Player)
            players.remove(entity);
        return players;
    }

    /**
     * Get players nearby a location. Async-Safe.
     * @param loc
     * @param radius
     * @return players
     */
    public static List<Player> getNearbyPlayers(Location loc, int radius) {
        return new ArrayList<>(getNearbyEntities(loc, radius).stream().filter(e -> e instanceof Player).map(e -> (Player) e)
                .collect(Collectors.toList()));
    }

    /**
     * Get entities nearby a location. Async-Safe
     * TODO: Make this actually async-safe.
     * @param loc
     * @param radius
     * @return entities
     */
    public static Collection<Entity> getNearbyEntities(Location loc, int radius) {
        return loc.getWorld().getNearbyEntities(loc, radius, radius, radius);
    }

    /**
     * Check if a given location is within the given coordinates.
     * Does not require the Y parameter.
     *
     * @param location
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @return inArea
     */
    public static boolean inArea(Location location, int x1, int z1, int x2, int z2) {
        return inArea(location, x1, 0, z1, x2, 256, z2);
    }

    /**
     * Is the given location between the given box coordinates?
     *
     * @param l
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @return inArea
     */
    public static boolean inArea(Location l, int x1, int y1, int z1, int x2, int y2, int z2) {
        return Math.min(x2 + 1, Math.max(x1 - 1, l.getX())) == l.getX()
                && Math.min(y2 + 1, Math.max(y1 - 1, l.getY())) == l.getY()
                && Math.min(z2 + 1, Math.max(z1 - 1, l.getZ())) == l.getZ();
    }

    /**
     * Is the provided location within spawn?
     * @param location
     * @return spawn
     */
    public static boolean inSpawn(Location location) {
        return inArea(location, -200, -200, 200, 306);
    }

    /**
     * Is this input an integer?
     * @param input
     * @return integer
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Is the given KCPlayer not null and visible to the sender?
     * If the player is not visible, it will tell the player so.
     *
     * @param sender
     * @param player
     * @return
     */
    public static boolean isVisible(CommandSender sender, KCPlayer player) {
        return isVisible(sender, player != null ? player.getPlayer() : null);
    }

    /**
     * Is the given username online and visible to the supplied sender?
     * If the player is not visible, it tells the player so.
     *
     * @param sender
     * @param username
     * @return visible
     */
    public static boolean isVisible(CommandSender sender, String username) {
        return isVisible(sender, Bukkit.getPlayer(username));
    }

    /**
     * Checks if the given CommandSender is online and visible to the supplied sender.
     * If not visible, it will tell the player so.
     *
     * @param sender
     * @param receiver
     * @return
     */
    public static boolean isVisible(CommandSender sender, CommandSender receiver) {
        boolean shown = receiver != null && (!(receiver instanceof Player) || !KCPlayer.getWrapper(receiver).isVanished(sender));
        if (!shown)
            sender.sendMessage(ChatColor.RED + "Player not found" + (sender instanceof DiscordSender ? " in-game" : "") + ".");
        return shown;
    }

    /**
     * Read all lines from a plugin resource.
     * @param resource
     * @return lines
     */
    public static List<String> readLines(String resource) {
        return new BufferedReader(new InputStreamReader(Core.loadResource(resource), StandardCharsets.UTF_8))
                .lines().collect(Collectors.toList());
    }

    /**
     * Get the size of the data in a plugin resource.
     * @param resource
     * @return size
     */
    public static int readSize(String resource) {
        return readLines(resource).stream().mapToInt(String::length).sum();
    }

    /**
     * Remove duplicate entries from a list.
     * @param list
     */
    public static <T> void removeDuplicates(List<T> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.lastIndexOf(list.get(i)) != i) {
                list.remove(i);
                i--;
            }
        }
    }

    /**
     * Append an element to create a new array.
     * @param arr
     * @param <T>
     * @return newArray
     */
    public static <T> T[] append(T[] arr, T value) {
        List<T> add = new ArrayList<>(Arrays.asList(arr));
        add.add(value);
        return add.toArray(arr);
    }

    /**
     * Are the two supplied locations in the same block?
     * @param a
     * @param b
     * @return sameBlock
     */
    public static boolean isSameBlock(Location a, Location b) {
        return a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
    }

    /**
     * Get a random location within given bounds from an original location.
     * @param start
     * @param x
     * @param y
     * @param z
     * @return loc
     */
    public static Location scatter(Location start, double x, double y, double z) {
        return start.clone().add(randDouble(-x, x), randDouble(-y, y), randDouble(-z, z));
    }

    /**
     * Is the given location protected to the given player?
     * @param player
     * @param loc
     * @return protected
     */
    public static boolean isProtected(Player player, Location loc) {
        BlockBreakEvent evt = new BlockBreakEvent(loc.getBlock(), player);
        evt.setDropItems(false);
        Bukkit.getPluginManager().callEvent(evt);
        return evt.isCancelled();
    }

    /**
     * Get whether or not an entity is in a protected area.
     * @param player
     * @param target
     * @return protected
     */
    public static boolean isProtected(Player player, Entity target) {
        return isProtected(player, target.getLocation());
    }

    /**
     * Check if a string contains a substring regardless of case. Null-safe. Ignores color codes.
     * @param s1
     * @param substring
     * @return contains
     */
    public static boolean containsIgnoreCase(String s1, String substring) {
        return s1 != null && substring != null
                && ChatColor.stripColor(s1).toLowerCase().contains(ChatColor.stripColor(substring).toLowerCase());
    }

    /**
     * Get the WorldGuard region name that the entity is currently in.
     * @param ent
     * @return regionName
     */
    public static String getRegion(Entity ent) {
        return getRegion(ent.getLocation());
    }

    /**
     * Get the WorldGuard region name for the location specified.
     * @param loc
     * @return regionName
     */
    public static String getRegion(Location loc) {
        String name = "__global__";
        int priority = -1;

        for (ProtectedRegion r : WorldGuardPlugin.inst().getRegionManager(loc.getWorld()).getApplicableRegions(loc)) {
            if (r.getPriority() > priority) {
                priority = r.getPriority();
                name = r.getId();
            }
        }

        return name;
    }

    /**
     * Delete a file relative to the server path.
     * @param path
     */
    @SneakyThrows
    public static void removeFile(String path) {
        File f = new File(path);
        if (f.exists())
            FileUtils.forceDelete(f);
    }

    /**
     * Spawn a firework at the given location.
     * @param loc
     * @param firework
     */
    public static void spawnFirework(Location loc, FireworkEffect.Builder firework) {
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.setPower(0);
        meta.addEffects(firework.build());
        fw.setFireworkMeta(meta);
    }

    /**
     * Run a shell command asynchronously, but runs 'onFinish' synchronously after it completes.
     * @param cmd
     * @param onFinish
     */
    public static void runShell(String cmd, Runnable onFinish) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
            try {
                final ProcessBuilder childBuilder = new ProcessBuilder(cmd);
                childBuilder.redirectErrorStream(true);
                childBuilder.directory(Core.getInstance().getDataFolder().getParentFile().getParentFile());
                final Process child = childBuilder.start();

                Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
                    try {
                        @Cleanup BufferedReader reader = new BufferedReader(new InputStreamReader(child.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null)
                            Bukkit.getLogger().info(line);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                child.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (onFinish != null)
                    Bukkit.getScheduler().runTask(Core.getInstance(), onFinish);
            }
        });
    }

    /**
     * Teleport an entity to spawn.
     * @param ent
     */
    public static void toSpawn(Entity ent) {
        safeTp(ent, Core.getMainWorld().getSpawnLocation());
    }

    /**
     * Teleport an entity safely, including interdimensionally.
     * @param e
     * @param loc
     */
    public static void safeTp(Entity e, Location loc) {
        final Location safe = findSafe(loc.clone());
        if (e.getWorld() != safe.getWorld()) // If teleporting cross-dimensionally we'll need to teleport them again.
            e.teleport(safe);
        e.teleport(safe);
        e.setFallDistance(0);
    }

    /**
     * Confirm a condition is correct, otherwise throw a RuntimeException.
     * @param condition
     * @param error
     */
    public static void confirm(boolean condition, String error) {
        if (!condition)
            throw new RuntimeException(error);
    }

    /**
     * Get the item currently in an entities equipment slot.
     * @param le
     * @param slot
     * @return item
     */
    public static ItemStack getItem(LivingEntity le, EquipmentSlot slot) {
        EntityEquipment e = le.getEquipment();
        switch (slot) {
            case HAND:
                return e.getItemInMainHand();
            case OFF_HAND:
                return e.getItemInOffHand();
            case FEET:
                return e.getBoots();
            case LEGS:
                return e.getLeggings();
            case CHEST:
                return e.getChestplate();
            case HEAD:
                return e.getHelmet();
            default:
                throw new IllegalArgumentException("Cannot retreive " + slot);
        }
    }

    /**
     * Set the item in an entities equipment slot.
     * @param le
     * @param slot
     * @param item
     */
    public static void setItem(LivingEntity le, EquipmentSlot slot, ItemStack item) {
        EntityEquipment e = le.getEquipment();
        switch (slot) {
            case HAND:
                e.setItemInMainHand(item);
                break;
            case OFF_HAND:
                e.setItemInOffHand(item);
                break;
            case FEET:
                e.setBoots(item);
                break;
            case LEGS:
                e.setLeggings(item);
                break;
            case CHEST:
                e.setChestplate(item);
                break;
            case HEAD:
                e.setHelmet(item);
                break;
            default:
                throw new IllegalArgumentException("Cannot store " + slot);
        }
    }

    /**
     * Mirror the armor and held items of an entity to the other.
     * @param from
     * @param to
     */
    public static void mirrorItems(LivingEntity from, LivingEntity to) {
        for(EquipmentSlot slot : EquipmentSlot.values())
            setItem(to, slot, getItem(from, slot));
    }

    /**
     * Convert an Iterable to a List.
     * @param values
     * @param <T>
     * @return list
     */
    public static <T> List<T> toList(Iterable<T> values) {
        List<T> list = new ArrayList<>();
        values.forEach(list::add);
        return list;
    }

    /**
     * Run a task repeatedly at a given calendar time, accurate to the second.
     * @param interval
     * @param unit
     * @param task
     */
    public static void runCalendarTaskAt(TimeInterval interval, int unit, Runnable task) {
        AtomicBoolean runYet = new AtomicBoolean();
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            boolean isTime = interval.getValue() == unit;
            if (!runYet.get() && isTime)
                task.run();
            runYet.set(isTime);
        }, 0L, 20L);
    }

    /**
     * Run a task every given interval on the calendar. Accurate to the second.
     * @param interval
     * @param task
     */
    public static void runCalendarTaskEvery(TimeInterval interval, Runnable task) {
        runCalendarTaskEvery(interval, 1, task);
    }

    /**
     * Run a task every given interval on the calendar. Accurate to the second.
     * @param interval
     * @param unit
     * @param task
     */
    public static void runCalendarTaskEvery(TimeInterval interval, int unit, Runnable task) {
        AtomicInteger lastRun = new AtomicInteger(interval.getValue());
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            int now = interval.getValue();
            if (now % unit == 0 && now != lastRun.get()) {
                task.run();
                lastRun.set(now);
            }
        }, 0L, 20L);
    }

    /**
     * Get all the blocks between the two supplied blocks. Inclusive.
     * @param a
     * @param b
     * @return blocks
     */
    public static List<Block> getBlocksBetween(Block a, Block b) {
        return getBlocksBetween(a.getLocation(), b.getLocation());
    }

    /**
     * Get all the blocks between two locations, inclusive.
     * @param a
     * @param b
     * @return blocks
     */
    public static List<Block> getBlocksBetween(Location a, Location b) {
        List<Block> blocks = new ArrayList<>();
        looseFor(a.getBlockX(), b.getBlockX(), x -> looseFor(a.getBlockY(), b.getBlockY(), y ->
                looseFor(a.getBlockZ(), b.getBlockZ(), z -> blocks.add(new Location(a.getWorld(), x, y, z).getBlock()))));
        return blocks;
    }

    /**
     * Remove all non-alphanumeric characters from a string.
     * Used to sanitize file names.
     * @param input
     * @return sanitized
     */
    public static String sanitizeFileName(String input) {
        return input.replaceAll("[^a-zA-Z0-9 -_]", "");
    }

    /**
     * Execute a for loop automatically picking the max + mins.
     * @param a
     * @param b
     * @param code
     */
    public static void looseFor(int a, int b, Consumer<Integer> code) {
        for (int i = Math.min(a, b); i < Math.max(a, b); i++)
            code.accept(i);
    }

    /**
     * Get the direction a block is facing.
     * @param bk
     * @return facing
     */
    public static BlockFace getDirection(Block bk) {
        return bk.getState().getData() instanceof Directional ? ((Directional) bk.getState().getData()).getFacing() : BlockFace.SELF;
    }

    /**
     * Repeat the song a SongPlayer is playing.
     * @param mp
     */
    public static void repeatNBS(SongPlayer mp) {
        getRepeat().remove(mp); // We won't refer to this again.
        List<Player> players = mp.getPlayerList().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () ->
                playNBS(players, mp.getSong().getPath().getName().split("\\.nbs")[0], true), 2L);
    }

    /**
     * Play a .nbs sound file to the given players.
     * @param players
     * @param sound
     * @param repeat
     */
    public static void playNBS(List<Player> players, String sound, boolean repeat) {
        if (players.isEmpty())
            return; // Don't play the song to nobody.
        players.forEach(NoteBlockPlayerMain::stopPlaying);
        Song s = NBSDecoder.parse(Core.getFile("audio/" + sound + ".nbs"));
        SongPlayer player = new RadioSongPlayer(s);
        player.setAutoDestroy(!repeat);
        players.forEach(player::addPlayer);
        player.setPlaying(true);
        if (repeat)
            getRepeat().add(player);
    }

    /**
     * Stop all NBS sounds for a player.
     * @param player
     */
    public static void stopNBS(Player player) {
        NoteBlockPlayerMain main = NoteBlockPlayerMain.plugin;
        if (!main.playingSongs.containsKey(player.getName()))
            return;

        main.playingSongs.get(player.getName()).forEach(sp -> {
            sp.setFadeTarget((byte) 0); // We want to fade to 0 volume.
            sp.setFadeDone(0); // Reset the current fade.
            sp.setFadeDuration(30); // Should take 30 iterations to destroy. (Depends on song TPS, not server TPS.)
            getRepeat().remove(sp); // Don't repeat this sound anymore.
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), sp::destroy, 60L); // Completely disable sound.
        });
    }

    /**
     * Stop NBS music for a player immediately.
     * @param player
     */
    public static void stopNBSNow(Player player) {
        NoteBlockPlayerMain main = NoteBlockPlayerMain.plugin;
        if (!main.playingSongs.containsKey(player.getName()))
            return;

        main.playingSongs.get(player.getName()).forEach(sp -> {
            getRepeat().remove(sp); // Don't repeat this sound anymore.
            sp.destroy(); // Delete.
        });
    }

    /**
     * Get the closest entity to a given location.
     * @param loc
     * @param radius
     * @return closest
     */
    public static Entity getNearestEntity(Location loc, int radius) {
        return getNearestEntity(loc, radius, e -> true);
    }

    /**
     * Get the nearest entity meeting special conditions to a location.
     * @param loc
     * @param radius
     * @param p
     * @return closest
     */
    public static Entity getNearestEntity(Location loc, int radius, Predicate<Entity> p) {
        Entity res = null;
        List<Entity> possible = loc.getWorld().getNearbyEntities(loc, radius, radius, radius).stream().filter(p).collect(Collectors.toList());
        for (Entity e : possible)
            if (res == null || e.getLocation().distance(loc) < res.getLocation().distance(loc))
                res = e;
        return res;
    }

    /**
     * Get the nearest living entity to a location.
     * @param loc
     * @param radius
     * @return closest
     */
    public static LivingEntity getNearestLivingEntity(Location loc, int radius) {
        return (LivingEntity) getNearestEntity(loc, radius, e -> e instanceof LivingEntity);
    }

    /**
     * Get the nearest player to a location.
     * @param loc
     * @param radius
     * @return closest
     */
    public static Player getNearestPlayer(Location loc, int radius) {
        return (Player) getNearestEntity(loc, radius, e -> e instanceof Player);
    }

    /**
     * Get the nearest player to a location that is in survival mode.
     * @param en
     * @param radius
     * @return closest
     */
    public static Player getNearestSurvivalPlayer(Entity en, int radius) {
        return (Player) getNearestEntity(en.getLocation(), radius, e -> e instanceof Player && !e.equals(en) && ((Player) e).getGameMode() == GameMode.SURVIVAL);
    }

    /**
     * Get the nearest living entity to an entity.
     * @param en
     * @param radius
     * @return entity
     */
    public static LivingEntity getNearestLivingEntity(Entity en, int radius) {
        return (LivingEntity) getNearestEntity(en.getLocation(), radius, e -> e instanceof LivingEntity && !e.equals(en));
    }

    /**
     * Get the nearest player to an entity.
     * @param en
     * @param radius
     * @return nearest
     */
    public static Player getNearestPlayer(Entity en, int radius) {
        return (Player) getNearestEntity(en.getLocation(), radius, e -> e instanceof Player && !e.equals(en));
    }

    /**
     * Scramble the order of the letters in a string.
     * @param string
     * @return scrambled
     */
    public static String scramble(String string) {
        String scrambled = "";
        List<String> split = new ArrayList<>(Arrays.asList(string.split("")));
        while(!split.isEmpty())
            scrambled += split.remove(Utils.randInt(0, split.size() - 1));
        return scrambled;
    }

    /**
     * Get the direction "to" is relative to "from".
     * @param from
     * @param to
     * @return direction
     */
    public static BlockFace getDirection(Location from, Location to) {
        double dis = 0;
        BlockFace ret = BlockFace.SELF;
        for (BlockFace face : FACES) {
            if (face == BlockFace.SELF)
                continue;

            Location sub = to.clone().subtract(from.getX(), from.getY(), from.getZ());
            double tDis = (sub.getX() * face.getModX()) + (sub.getZ() * face.getModZ());
            if (tDis >= dis) {
                dis = tDis;
                ret = face;
            }
        }
        return ret;
    }

    /**
     * Get the yaw for when you are facing this direction.
     * @param face
     * @return yaw
     */
    public static float toYaw(BlockFace face) {
        return face.getOppositeFace().ordinal() * 90;
    }

    /**
     * Returns whether or not the player has an open inventory, excluding their own.
     * @param player
     * @return hasOpen
     */
    public static boolean hasOpenInventory(Player player) {
        InventoryType type = player.getOpenInventory().getType();
        return type != InventoryType.CREATIVE && type != InventoryType.CRAFTING; // Crafting = Survival, while Workbench = Crafting Table.
    }
}