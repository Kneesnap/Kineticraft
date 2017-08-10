package net.kineticraft.lostcity.dungeons.dungeons.barleyshope;

import net.kineticraft.lostcity.dungeons.puzzle.Puzzle;
import net.kineticraft.lostcity.dungeons.puzzle.PuzzleTrigger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;

/**
 * The second puzzle of "Barley's Hope."
 * TODO: Update Locations
 * Created by Kneesnap on 8/9/2017.
 */
public class GatherPuzzle extends Puzzle {
    public GatherPuzzle() {
        super(new Location(null, -109, 7, 221), BlockFace.WEST);
    }

    @Override
    protected void onInit() {
        replaceNear(new Location(null, -97, 11, 221), Material.REDSTONE_TORCH_ON, Material.REDSTONE_TORCH_OFF, 10);
    }

    @PuzzleTrigger
    public void lightTorch(CommandBlock cmd) {
        Location loc = cmd.getLocation();
        for (int i = 0; i < 10; i++) {
            loc.add(0, 1, 0);
            if (loc.getBlock().getType() == Material.REDSTONE_TORCH_ON)
                resetFakeBlock(loc.getBlock());
        }
    }

    // Thing:
    // Press all buttons -> Opens Secret path, that contains red key.
}
