package net.kineticraft.lostcity.dungeons.puzzle;

import net.kineticraft.lostcity.cutscenes.Cutscene;
import net.kineticraft.lostcity.cutscenes.actions.ActionBlockUpdate;
import net.kineticraft.lostcity.cutscenes.actions.ActionPlayNBS;
import net.kineticraft.lostcity.cutscenes.actions.ActionPlaySound;
import net.kineticraft.lostcity.cutscenes.actions.GenericAction;
import net.kineticraft.lostcity.cutscenes.actions.entity.ActionTeleportEntity;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Brings down the door at the end of a puzzle
 * Constructor accepts the block under the middle of the gate.
 * Created by Kneesnap on 8/1/2017.
 */
public class PuzzleDoorCutscene extends Cutscene {
    private Location gate;
    private BlockFace face;

    public PuzzleDoorCutscene(Location gate, BlockFace playerFacing) {
        this.gate = gate;
        this.face = playerFacing;

        addStage(40, new ActionTeleportEntity(getCameraPosition()));

        for (int y = 0; y < 5; y++) {
            Location l = this.gate.clone().add(0, y + 1, 0);
            addStage(10, new GenericAction(ce -> {
               Block b = l.getBlock();
               Utils.FACES.stream().map(b::getRelative).forEach(bk -> bk.setType(Material.AIR));
            }), new ActionPlaySound(l, Sound.BLOCK_GRAVEL_BREAK, (float) Math.max(0.8, (.7 + y) / 10), 1));
        }

        addStage(80, new ActionPlayNBS("solve"),
                new ActionBlockUpdate(this.gate.clone().add(0, -1, 0), Material.REDSTONE_BLOCK));
    }

    private Location getCameraPosition() {
        Location camera = gate.clone();
        camera.add(face.getModX() * -4, 2, face.getModZ() * -4);
        camera.setPitch(10);
        camera.setYaw(face.ordinal() * 90);
        return camera;
    }
}
