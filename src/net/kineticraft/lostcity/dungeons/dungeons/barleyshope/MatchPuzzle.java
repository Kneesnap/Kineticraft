package net.kineticraft.lostcity.dungeons.dungeons.barleyshope;

import net.kineticraft.lostcity.dungeons.puzzle.Puzzle;
import net.kineticraft.lostcity.dungeons.puzzle.PuzzleTrigger;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.material.Wool;

import java.util.Arrays;
import java.util.List;

/**
 * Get the player to match displays.
 * Created by Kneesnap on 8/2/2017.
 */
@SuppressWarnings("deprecation")
public class MatchPuzzle extends Puzzle {
    private static final List<DyeColor> COLORS = Arrays.asList(DyeColor.YELLOW, DyeColor.RED, DyeColor.GREEN);

    @SuppressWarnings("SuspiciousMethodCalls")
    @PuzzleTrigger
    public void changeColor(CommandBlock block) {
        Block wool = block.getBlock().getRelative(BlockFace.UP);
        int index = COLORS.indexOf(((Wool) wool.getState().getData()).getColor()) + 1;
        wool.setData(COLORS.get(index >= COLORS.size() ? 0 : index).getWoolData());
        scanBoard();
    }

    /**
     * Scan the board to check if it's correct.
     */
    private void scanBoard() {
        for (int x = -8; x < -2; x++) {
            for (int z = -40; z < -32; z++) {
                Block b = new Location(getDungeon().getWorld(), x, 10, z).getBlock();
                if (b.getType() == Material.WOOL && b.getData() != b.getLocation().add(0, -2, 0).getBlock().getData())
                    return;
            }
        }
        complete(); // All of the board matches.
    }
}
