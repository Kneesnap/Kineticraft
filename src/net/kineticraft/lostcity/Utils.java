package net.kineticraft.lostcity;

import com.google.gson.JsonObject;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.mechanics.MetadataManager;
import net.kineticraft.lostcity.mechanics.MetadataManager.Metadata;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utils - Contains basic static utilties.
 * Created by Kneesnap on 5/29/2017.
 */
public class Utils {


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
    public static <T extends Enum<T>> T getEnum(String value, T defaultValue) {
        return getEnum(value, (Class<T>) defaultValue.getClass(), defaultValue);
    }

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
        String[] split = sentence.split(" ");
        List<String> out = new ArrayList<>();
        for (String s : split)
            out.add(s.length() > 0 ? s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() : "");
        return String.join(" ", out);
    }

    /**
     * Save a Location as Json.
     * @param loc
     * @return
     */
    public static JsonObject saveLocation(Location loc) {
        return new JsonData().setNum("x", loc.getX()).setNum("y", loc.getY()).setNum("z", loc.getZ()).setNum("yaw", loc.getYaw())
                .setNum("pitch", loc.getPitch()).setString("world", loc.getWorld().getName()).getJsonObject();
    }

    /**
     * Load a location from Json.
     * @param jsonObject
     * @return
     */
    public static Location loadLocation(JsonObject jsonObject) {
        JsonData data = new JsonData(jsonObject);
        return new Location(Bukkit.getWorld(data.getString("world")), data.getDouble("x"), data.getDouble("y"),
                data.getDouble("z"), data.getFloat("yaw"), data.getFloat("pitch"));
    }

    /**
     * Joins a string together by the given string.
     * @param join
     * @param values
     * @param display
     * @return
     */
    public static <T> String join(String join, T[] values, Displayer<T> display) {
        return join(join, Arrays.asList(values), display);
    }

    /**
     * Joins a string together by the delimeter.
     * @param join
     * @param values
     * @param displayer
     * @param <T>
     * @return
     */
    public static <T> String join(String join, Iterable<T> values, Displayer<T> displayer) {
        String res = "";
        for (T val : values)
            res += (res.length() > 0 ? join : "") + displayer.getDisplay(val);
        return res;
    }

    /**
     * Teleport the player to the specified location with cooldowns applied.
     * @param player
     * @param locationDescription
     * @param location
     */
    public static void teleport(Player player, String locationDescription, Location location) {
        if (MetadataManager.hasMetadata(player, Metadata.TELEPORTING)) {
            player.sendMessage(ChatColor.RED + "Please wait until your current teleport finishes.");
            return;
        }

        double lastDamage = player.getLastDamage();
        final Location startLocation = player.getLocation().clone();
        final BukkitTask[] tpTask = new BukkitTask[1]; // Have to use an array here to store a value, as otherwise it can't be final.
        // Must be final to work in the bukkit scheduler.
        int[] tpTime = new int[] {5}; // TODO: Make this variable.

        MetadataManager.setMetadata(player, Metadata.TELEPORTING, true);
        player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1F, 1F);
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * (tpTime[0] + 4), 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * (tpTime[0] + 4), 2));
        player.sendMessage(ChatColor.BOLD + "Teleport: " + ChatColor.WHITE + ChatColor.UNDERLINE + locationDescription);

        //TODO: Verify destination is safe)
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
                    player.teleport(location.add(0, 2, 0));
                    player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1F, 1.333F);
                }

                tpTime[0]--;
        }, 0L, 20L);
    }

    public interface Displayer<T> {
        public String getDisplay(T val);
    }

    /**
     * Turn milliseconds into a user friendly string.
     * @param time
     * @return
     */
    public static String formatTime(long time) {
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
        return formatted.equals("") ? "" : formatted.substring(1);
    }

    private enum TimeInterval {
        SECOND("s", 1),
        MINUTE("min", 60 * SECOND.getInterval()),
        HOUR("hr", 60 * MINUTE.getInterval()),
        DAY("day", 24 * HOUR.getInterval()),
        MONTH("month", 30 * DAY.getInterval()),
        YEAR("yr", 365 * DAY.getInterval());

        private String suffix;
        private int interval;

        TimeInterval(String s, int i) {
            this.suffix = s;
            this.interval = i;
        }

        public int getInterval() {
            return this.interval;
        }

        public String getSuffix() {
            return this.suffix;
        }
    }

    /**
     * Get a random number between the given range.
     * @param min
     * @param max
     */
    public static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max - min) + min;
    }
}
