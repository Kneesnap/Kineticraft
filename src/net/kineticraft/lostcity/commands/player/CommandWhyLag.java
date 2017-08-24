package net.kineticraft.lostcity.commands.player;

import lombok.AllArgsConstructor;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.mechanics.ServerManager;
import net.kineticraft.lostcity.utils.PlayerUtils;
import net.kineticraft.lostcity.utils.ServerUtils;
import net.kineticraft.lostcity.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Get a status report of potential lag.
 * Created by Kneesnap on 6/28/2017.
 */
public class CommandWhyLag extends PlayerCommand {

    private static final int ENTITIES_PER_PLAYER = 200;

    private static final String[] LAG_SETTING = new String[] {ChatColor.DARK_GREEN + "Lowest", ChatColor.GREEN + "Low",
            ChatColor.YELLOW + "Medium", ChatColor.RED + "High", ChatColor.DARK_RED + "Maximum"};

    public CommandWhyLag() {
        super("", "View server lag information.", "lag", "whylag", "gc", "tps", "ping");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        double tps = ServerManager.getTPS();
        double tpsLag = TextUtils.toFixed((20 - tps) * 5, 2);
        sender.sendMessage(ChatColor.GOLD + "Server TPS: " + TextUtils.colorValue(tps, 20) + " (" + tpsLag + " % lag)");
        sender.sendMessage(ChatColor.GOLD + "Anti-Lag Setting: " + LAG_SETTING[ServerManager.getLagSetting()]
                + " (" + ServerManager.getRenderDistance() + ")");

        if (sender instanceof Player) {
            int ping = PlayerUtils.getCraftPlayer((Player) sender).spigot().getPing();
            if (ping >= 0) // Don't show negative pings.
                sender.sendMessage(ChatColor.GOLD + "Network Connection: " + TextUtils.colorValue(ping, 500, true)
                        + "ms (" + (ping > 150 ?  (ping - 150) + "ms too many" : "all good") + ")");
            sender.sendMessage(ChatColor.GREEN + (ping > 150 ? "Your connection is slow."
                    : "You shouldn't be lagging, maybe your FPS (frames per second) is low?"));
        }

        if (tps < 18) {
            sender.sendMessage(ChatColor.GOLD + "Possible Lag Causes: ");
            Arrays.stream(LagCause.values()).filter(LagCause::isPossible).map(LagCause::getMessage).forEach(sender::sendMessage);
        }
    }

    /**
     * Return the number of entities needed before the server considers itself overpopulated.
     * @return threshold
     */
    private static int getEntityThreshold() {
        return ENTITIES_PER_PLAYER * Bukkit.getOnlinePlayers().size();
    }

    /**
     * Get the total number of entities loaded in all worlds.
     * @return entities
     */
    private static int getEntityCount() {
        return Bukkit.getWorlds().stream().map(World::getEntities).mapToInt(List::size).sum();
    }

    /**
     * Get the number of players using elytra.
     * @return flyers
     */
    private static int getFlying() {
        return (int) Bukkit.getOnlinePlayers().stream().filter(Player::isGliding).count();
    }

    @AllArgsConstructor
    private enum LagCause {

        ELYTRA(() -> getFlying() > 0, () -> getFlying() + " flying players"),
        BACKUP(ServerUtils::isBackingUp, () -> "Server is backing up"),
        ENTITY(() -> getEntityCount() > getEntityThreshold(), () -> "Too many entities (&e" + getEntityCount() + "&c)");

        private final Supplier<Boolean> possible;
        private final Supplier<String> message;

        public boolean isPossible() {
            return possible.get();
        }

        public String getMessage() {
            return ChatColor.RED + " - " + ChatColor.translateAlternateColorCodes('&', message.get()) + ".";
        }
    }
}
