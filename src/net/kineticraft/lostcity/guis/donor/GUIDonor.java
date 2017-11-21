package net.kineticraft.lostcity.guis.donor;

import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.guis.GUI;
import net.kineticraft.lostcity.guis.GUIType;
import net.kineticraft.lostcity.mechanics.Callbacks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * GUI that lets donors control their effects.
 * Created by Kneesnap on 6/11/2017.
 */
public class GUIDonor extends GUI {

    public GUIDonor(Player player) {
        super(player, "Donor Menu");
    }

    @Override
    public void addItems() {
        addItem(Material.EMPTY_MAP, ChatColor.GOLD + "Donor Perks", "Other perks:",
                "",
                " - /kittycannon");
        KCPlayer player = getWrapper();

        nextSlot();
        addItem(Material.APPLE, ChatColor.GOLD + "Set Icon", "Click here to change your icon.",
                "",
                "Current Icon: " + (player.getIcon() != null ? player.getIcon() : ChatColor.RED + "None"),
                "",
                "Left-Click: " + ChatColor.WHITE + "Change Icon",
                "Right-Click: " + ChatColor.WHITE + "Reset Icon")
                .leftClick(e -> {
                    getPlayer().sendMessage(ChatColor.GREEN + "Please enter your new icon.");
                    getPlayer().sendMessage(ChatColor.GREEN + "Visit http://azuliadesigns.com/html-character-codes-ascii-entity-unicode-symbols for examples.");
                    Callbacks.listenForChat(getPlayer(), msg -> {
                        String icon = ChatColor.translateAlternateColorCodes('&', msg);
                        String noColor = ChatColor.stripColor(icon);

                        if (noColor.length() > 1) {
                            getPlayer().sendMessage(ChatColor.RED + "Icons may only be 1 character.");
                            return;
                        }

                        if (!isAllowed(noColor)) {
                            getPlayer().sendMessage(ChatColor.RED + "'" + noColor + "' is not a valid icon.");
                            return;
                        }

                        player.setIcon(icon);
                        getPlayer().sendMessage(ChatColor.GREEN + "Icon updated.");
                        player.updatePlayer();
                    });
                }).rightClick(e -> {
                    if (player.getIcon() == null)
                        return;
                    player.setIcon(null);
                    player.updatePlayer();
                    getPlayer().sendMessage(ChatColor.GREEN + "Icon removed.");
                    reconstruct();
                });

        nextSlot();
        addItem(Material.STICK, ChatColor.GREEN + "Particles", "Click here to show particles.").opens(GUIType.PARTICLES);
    }

    private boolean isAllowed(String icon) {
        return !Configs.getMainConfig().getFilter().keySet().contains(icon);
    }
}
