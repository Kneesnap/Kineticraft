package net.kineticraft.lostcity.mechanics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

/**
 * Punishments - Port of our old JS punishment system.
 *
 * Created by Kneesnap on 6/17/2017.
 */
public class Punishments extends Mechanic {

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
            return " - " + getType().getDisplay() + " (" + new Date(getTimestamp()).toString() + ")";
        }
    }

    @AllArgsConstructor @Getter
    public enum PunishmentType {

        SPAM("Spamming", 1, Material.PORK),
        HARASSMENT("Harassment", 1, Material.LAVA_BUCKET),
        ADVERTISING("Advertising", 1, Material.IRON_DOOR),
        AUTO_RELOG("Auto Relogger", 1, Material.IRON_INGOT),
        SLURS("Slurs", 3, Material.REDSTONE),
        TOXICITY("Toxicity", 3, Material.REDSTONE_BLOCK),
        THREATS("Threats", 3, Material.GOLD_SWORD),
        ADULT("Adult Content", 3, Material.EXP_BOTTLE),
        HACKING("General Hacks", 9, Material.PAINTING),
        FLYING("Flying", 9, Material.ELYTRA),
        XRAY("Xraying", 9, Material.DIAMOND),
        GRIEF("Griefing (Minor)", 5, Material.FLINT_AND_STEEL),
        GRIEF_LARGE("Griefing (Major)", 20, Material.TNT),
        ALT_ACCOUNT("Ban Evasion", -1, Material.SEEDS);

        private final String display;
        private final int punishLength;
        private final Material icon;

        /**
         * Get the initial amount of hours this punishment stands for.
         * -1 = Forever.
         *
         * @return initial
         */
        public int getInitialTime() {
            return getPunishLength() == -1 ? -1 : 8;
        }
    }
}
