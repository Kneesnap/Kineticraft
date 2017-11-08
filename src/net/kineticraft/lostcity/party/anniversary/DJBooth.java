package net.kineticraft.lostcity.party.anniversary;

import com.destroystokyo.paper.Title;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.party.games.SinglePlayerGame;
import net.kineticraft.lostcity.utils.ColorConverter;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;

/**
 * Dance with DJ Khaled.
 * Created by Kneesnap on 9/15/2017.
 */
public class DJBooth extends SinglePlayerGame {
    private byte woolGoal;
    private long startTime;
    private boolean lost;
    private static final int TIME = 45;

    public DJBooth() {
        addSpawnLocation(21, 64, 81, -90, 0);
    }

    @Override
    protected void onStart() {
        broadcast(ChatColor.GREEN + getPlayer().getName() + ChatColor.BLUE + " has entered my DJ Booth!");
        drawBoard(false);
        playMusic("Everybody Dance Now", false);
        countdown(() -> {
            getPlayer().sendTitle(new Title(ChatColor.GREEN + "Start"));
            lost = false; // Initiate game variables and timer.
            woolGoal = -1;
            getScheduler().runTaskTimer(this::updateGoal, 20L, 20L);
            getScheduler().runTaskTimer(this::djTick, 0L, 60L);
            startTime = System.currentTimeMillis();
            getScheduler().runTaskLater(this::stop, TIME * 20); // Stop the game / the player wins.
        }, 3);
    }

    @Override
    public void onStop() {
        drawBoard(true);
        boolean win = !lost;
        getPlayer().sendTitle(new Title(win ? ChatColor.GOLD + "You smart, you very smart." : ChatColor.RED + "Congratulations", win ? null : ChatColor.GRAY + "You played yourself!"));
        broadcast(getPlayer().getName() + " has " + (win ? ChatColor.GREEN + "defeated" + ChatColor.BLUE : ChatColor.RED + "lost" + ChatColor.BLUE + " to") + " the mighty DJ Khaled!");
        if (win) {
            Utils.giveItem(getPlayer(), ItemManager.createItem(Material.BEETROOT, ChatColor.RED + "Sick Beats", "\"Don't ever play yourself.\""));
        } else {
            broadcast("Progress: " + ChatColor.YELLOW + Math.min((((System.currentTimeMillis() - startTime) / 10) / TIME), 100) + "%");
        }
    }

    private void djTick() {
        Location pLoc = getPlayer().getLocation();
        pLoc.setY(63);

        if (startTime + (TIME * 1000) <= System.currentTimeMillis()) {
            stop(); // It seems for some reason sometimes stop() does not call. Call it here if that happens.
            return;
        }

        if (woolGoal == -1 || woolGoal == pLoc.getBlock().getData()) {
            drawBoard (false);
            woolGoal = randColor(false); // Generate a new goal.
            sendMessage("Nice. Now get on " + getGoal() + ChatColor.BLUE + " wool.");
            updateGoal();
        } else {
            sendMessage("You call those moves? Get lost fool!");
            lost = true;
            stop();
        }
    }

    private String getGoal() {
        return ColorConverter.getDye(DyeColor.getByWoolData(woolGoal)).getDisplayName();
    }

    private void updateGoal() {
        if (isGoing() && woolGoal != -1)
            getPlayer().sendTitle(null, ChatColor.GRAY + "Stand on " + getGoal(), 0, 60, 4);
    }

    @SuppressWarnings("deprecation")
    private void drawBoard(boolean disable) {
        for (int x = 22; x < 32; x++) {
            for (int z = 73; z < 87; z++) {
                Block bk = getWorld().getBlockAt(x, 63, z);
                if (bk.getType() == Material.WOOL && (bk.getData() != 15 || !isGoing())) // If the game isn't going, that means we should reset all parts of the board.
                    bk.setData(disable ? (byte) 15 : randColor(true)); // If the board is being disabled, set everything to black.
            }
        }
    }

    private byte randColor(boolean allowBlack) {
        int id = Utils.randInt(0, DyeColor.values().length); // There are certain colors we don't allow below.
        return id == 2 || id == 7 || id == 8 || id == 12 || (id == 15 && !allowBlack) ? randColor(allowBlack) : (byte) id;
    }

    @Override
    public String getName() {
        return "DJ Not Nice";
    }

    @Override
    protected String getPrefix() {
        return "DJ Khaled>";
    }
}