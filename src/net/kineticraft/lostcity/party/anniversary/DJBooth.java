package net.kineticraft.lostcity.party.anniversary;

import com.destroystokyo.paper.Title;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.party.games.SinglePlayerGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * Dance with DJ Khaled.
 * Created by Kneesnap on 9/15/2017.
 */
public class DJBooth extends SinglePlayerGame {
    private byte woolGoal;
    private long startTime;
    private boolean lost;

    public DJBooth() {
        addSpawnLocation(26, 69, 80,-96, 90);
    }

    @Override
    protected void onStart() {
        broadcast(ChatColor.GREEN + getPlayer().getName() + ChatColor.BLUE + " has entered my DJ Booth!");
        //TODO: Fill Board
        //TODO: Play Everybody Dance Now
        countdown();
    }

    @Override
    public void onStop() {
        if (lost)
            return;
    }

    private void countdown() {
        for (int i = 0; i < 3; i++) {
            final int sec = i + 1;
            Bukkit.getScheduler().runTaskLater(Core.getInstance(),
                    () -> getPlayer().sendTitle(new Title(ChatColor.YELLOW.toString() + sec + "...")), i * 20);
        }

        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            getPlayer().sendTitle(new Title(ChatColor.GREEN + "Start"));
            lost = false;
            woolGoal = -1;
            //TODO: Start Ticking the djTick
            startTime = System.currentTimeMillis();
            //TODO: djTick
            //TODO: Send Wool hotbar
            //TODO: Automatically end in one minute.

        }, 60L);
    }

    private void djTick() {
        Location pLoc = getPlayer().getLocation();
        pLoc.setY(63);
        if (woolGoal == -1 || woolGoal == pLoc.getBlock().getData()) {
            //TODO: Draw Board
            //TODO: Random wool color.
            sendMessage("Nice. Now get on " + "TODO" + ChatColor.BLUE + " wool.");
            //TODO: Send wool hotbar.
        } else {
            sendMessage("You call those moves? Get lost fool!");
            lost = true;
            stop();
        }
    }

    @Override
    public String getName() {
        return "DJ Not Nice";
    }

    @Override
    protected String getPrefix() {
        return getName() + ":";
    }
}
