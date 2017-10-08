package net.kineticraft.lostcity.party;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.function.Consumer;

/**
 * An arena with basic utilities for parties or other use.
 * Created by Kneesnap on 9/16/2017.
 */
@Getter @AllArgsConstructor
public class Arena {
    private int xMin;
    private int yMin;
    private int zMin;
    private int xMax;
    private int yMax;
    private int zMax;
    private World world;

    public Arena(int xMin, int zMin, int xMax, int zMax) {
        this(xMin, xMax, zMin, zMax, Core.getMainWorld());
    }

    public Arena(int xMin, int zMin, int xMax, int zMax, World world) {
        this(xMin, 0, zMin, xMax, 256, zMax, world);
    }

    public Arena(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        this(xMin, yMin, zMin, xMax, yMax, zMax, Core.getMainWorld());
    }

    /**
     * Return if a location is contained within this area.
     * @param loc
     * @return contains
     */
    public boolean contains(Location loc) {
        return getWorld().equals(loc.getWorld()) && Utils.inArea(loc, getXMin(), getYMin(), getZMin(), getXMax(), getYMax(), getZMax());
    }

    /**
     * Perform an action for each block in this region.
     * @param cb
     */
    public void forEachBlock(Consumer<Location> cb) {
        for (int y = getYMin(); y <= getYMax(); y++)
            forEachBlock(cb, y);
    }

    /**
     * Perform an action for each block in this region, that meet a given Y value.
     * @param cb
     * @param yLevel
     */
    public void forEachBlock(Consumer<Location> cb, int yLevel) {
        for (int x = getXMin(); x <= getXMax(); x++)
                for (int z = getZMin(); z <= getZMax(); z++)
                    cb.accept(new Location(getWorld(), x, yLevel, z));
    }

    public Location randSpot(int yLevel) {
        return new Location(getWorld(), Utils.randInt(getXMin(), getXMax()), yLevel, Utils.randInt(getZMin(), getZMax()));
    }
}
