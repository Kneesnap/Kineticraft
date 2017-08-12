package net.kineticraft.lostcity.dungeons.dungeons.barleyshope;

import net.kineticraft.lostcity.dungeons.puzzle.Puzzle;
import net.kineticraft.lostcity.dungeons.puzzle.PuzzleTrigger;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.item.display.GenericItem;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;

/**
 * The second puzzle of "Barley's Hope."
 * Created by Kneesnap on 8/9/2017.
 */
public class GatherPuzzle extends Puzzle {
    private int buttons = 6;

    @Override
    protected void onInit() {
        replaceNear(new Location(null, -47, 14, -78), Material.REDSTONE_TORCH_ON, Material.REDSTONE_TORCH_OFF, 10);
    }

    @PuzzleTrigger
    public void lightTorch(CommandBlock cmd) {
        Location loc = cmd.getLocation();
        for (int i = 0; i < 10; i++) {
            loc.add(0, 1, 0);
            if (loc.getBlock().getType() == Material.REDSTONE_TORCH_ON)
                resetFakeBlock(loc.getBlock());
        }

        getDungeon().playSound(Sound.BLOCK_NOTE_HARP, 1.25F);
        getDungeon().playSound(Sound.BLOCK_NOTE_HARP, 1F);
    }

    @PuzzleTrigger
    public void bPress(CommandBlock cmd) {
        buttons--;

        // Play sound.
        cmd.getBlock().getWorld().playSound(cmd.getBlock().getLocation(), Sound.BLOCK_NOTE_PLING, 1F, 2F);

        // Get rid of button
        for (BlockFace face : Utils.CUBE_FACES) {
            Block bk = cmd.getBlock().getRelative(face, 2);
            if (bk.getType() == Material.WOOD_BUTTON)
                bk.setType(Material.AIR);
        }

        if (buttons == 0) {// If there are no buttons left, activate the gold torch.
            GenericItem iw = new GenericItem(ItemManager.createItem(Material.RED_ROSE, ChatColor.RED + "Red Key"));
            iw.setTagString("id", "d1_red");
            Utils.giveItem(Utils.getNearestPlayer(cmd.getLocation(), 15), iw.generateItem());
            cmd.getBlock().getWorld().playSound(cmd.getBlock().getLocation(), Sound.BLOCK_NOTE_PLING, 1F, 0.5F);
        }
    }
}
