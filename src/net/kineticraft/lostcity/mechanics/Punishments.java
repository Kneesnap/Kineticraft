package net.kineticraft.lostcity.mechanics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.discord.DiscordAPI;
import net.kineticraft.lostcity.discord.DiscordChannel;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.utils.TimeInterval;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Punishments - Port of our old JS punishment system.
 * Created by Kneesnap on 6/17/2017.
 */
public class Punishments extends Mechanic {

    @Override
    public void onEnable() {
        Utils.runCalendarTaskEvery(TimeInterval.WEEK, () -> {
            Map<String, Map<PunishmentType, Integer>> banCount = new HashMap<>();
            KCPlayer.getPlayerMap().values().forEach(p -> p.getPunishments().stream() // Generate ban report.
                    .filter(Punishment::isValid) // Make sure the punishment is still valid.
                    .filter(pu -> (System.currentTimeMillis() - pu.getTimestamp()) / 1000 <= TimeInterval.WEEK.getInterval()) // Make sure the punishment was in the past week.
                    .forEach(pu -> {
                        banCount.putIfAbsent(pu.getSource(), new HashMap<>());
                        Map<PunishmentType, Integer> count = banCount.get(pu.getSource());
                        count.put(pu.getType(), count.getOrDefault(pu.getType(), 0) + 1);
                    }));

            String fullReport = "```\nWeekly Ban Report:";
            for (String staff : banCount.keySet()) {
                Map<PunishmentType, Integer> countMap = banCount.get(staff);
                fullReport += "\n" + staff + ": " + countMap.values().stream().mapToInt(Integer::intValue).sum();
                for (PunishmentType type : countMap.keySet())
                    fullReport += "\n - " + Utils.capitalize(type.name()) + ": " + countMap.get(type);
            }

            DiscordAPI.sendMessage(DiscordChannel.ORYX, fullReport + "```");
        });
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent evt) {
        KCPlayer player = KCPlayer.getWrapper(evt.getUniqueId());
        if (player.isBanned())
            evt.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                ChatColor.RED + "You are banned from Kineticraft!\n"
                        + ChatColor.RED + "Reason: " + ChatColor.YELLOW + player.getPunishments().last().getType().getDisplay() + "\n"
                        + ChatColor.RED + "Expiration: " + ChatColor.YELLOW + Utils.formatTimeFull(player.getPunishExpiry()) + "\n"
                        + ChatColor.RED + "Appeal on discord: http://kineticraft.net");
    }

    @AllArgsConstructor @Data
    public static class Mute implements Jsonable {
        private long expiry;
        private String reason = "No reason specified.";
        private String source;

        public Mute() {

        }

        public String untilExpiry() {
            return Utils.formatTime(getExpiry() - System.currentTimeMillis());
        }

        public boolean isExpired() {
            return getExpiry() < System.currentTimeMillis();
        }
    }

    @Data
    public static class Punishment implements Jsonable {
        private PunishmentType type;
        private String source;
        private long timestamp;
        private boolean valid;

        public Punishment() {

        }

        public Punishment(PunishmentType type, String source) {
            setType(type);
            setSource(source);
            setTimestamp(System.currentTimeMillis());
            setValid(true);
        }

        /**
         * Create an item to represent this punishment
         * @return item
         */
        public ItemStack getItem() {
            return ItemManager.createItem(isValid() ? getType().getIcon() : Material.BARRIER, getType().getDisplay(),
                    "Source: " + ChatColor.WHITE + getSource(),
                    "Date: " + ChatColor.WHITE + new Date(timestamp).toString(),
                    "Click here to " + (isValid() ? "in" : "") + "validate this punishment.");
        }

        @Override
        public String toString() {
            return getType().getDisplay() + " (" + new Date(getTimestamp()).toString() + ")";
        }
    }

    @AllArgsConstructor @Getter
    public enum PunishmentType {

        SPAM("Spamming", 2, Material.PORK),
        HARASSMENT("Harassment", 2, Material.LAVA_BUCKET),
        ADVERTISING("Advertising", 2, Material.BOOK_AND_QUILL),
        AFK_BYPASS("AFK Bypass", 2, Material.IRON_DOOR),
        TOXICITY("Toxicity", 3, Material.POISONOUS_POTATO),
        SLURS("Slurs", 7, Material.GOLD_HOE),
        THREATS("Threats", 7, Material.IRON_SWORD),
        ADULT("Adult Content", 7, Material.BED),
        HACKING("General Hacks", 10, Material.PAINTING),
        FLYING("Flying", 10, Material.ELYTRA),
        XRAY("Xraying", 10, Material.DIAMOND),
        GRIEF("Griefing (Minor)", 9, Material.FLINT_AND_STEEL),
        GRIEF_LARGE("Griefing (Major)", 30, Material.TNT),
        ALT_ACCOUNT("Ban Evasion", -1, Material.ARMOR_STAND),
        PERMANENT("The Ban Hammer has spoken!", -1, Material.GOLD_AXE);

        private final String display;
        private final int punishLength;
        private final Material icon;

        /**
         * Get the initial amount of hours this punishment stands for.
         * -1 = Forever.
         * @return initial
         */
        public int getInitialTime() {
            return getPunishLength() == -1 ? -1 : 24;
        }
    }
}
