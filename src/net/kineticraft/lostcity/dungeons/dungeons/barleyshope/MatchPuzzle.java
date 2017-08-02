package net.kineticraft.lostcity.dungeons.dungeons.barleyshope;

import net.kineticraft.lostcity.dungeons.puzzle.Puzzle;
import net.kineticraft.lostcity.dungeons.puzzle.PuzzleTrigger;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;

import java.util.Arrays;
import java.util.List;

/**
 * Get the player to match displays.
 * Created by Kneesnap on 8/2/2017.
 */
@SuppressWarnings("deprecation")
public class MatchPuzzle extends Puzzle {
    private static final List<DyeColor> COLORS = Arrays.asList(DyeColor.YELLOW, DyeColor.LIGHT_BLUE, DyeColor.RED, DyeColor.GREEN);

    public MatchPuzzle() {
        super(null, BlockFace.NORTH);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @PuzzleTrigger
    public void changeColor(CommandBlock block) {
        Block wool = block.getBlock().getRelative(BlockFace.UP);
        int index = COLORS.indexOf(wool.getData()) + 1;
        wool.setData((byte) (index >= COLORS.size() ? 0 : index));
        scanBoard();
    }

    /**
     * Scan the board to check if it's correct.
     */
    private void scanBoard() {
        for (int x = -58; x < -52; x++) {
            for (int z = 259; z < 267; z++) {
                Block b = new Location(getDungeon().getWorld(), x, 9, z).getBlock();
                if (b.getType() == Material.WOOL && b.getData() != b.getLocation().add(0, -2, 0).getBlock().getData())
                    return;
            }
        }
        complete(); // All of the board mataches.
    }
}
