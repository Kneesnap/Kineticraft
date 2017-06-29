package net.kineticraft.lostcity.mechanics.metadata;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * An easy interface for allowing objects to have metadata
 *
 * Created by Kneesnap on 6/28/2017.
 */
public interface MetadataHolder extends Metadatable {

    @Override
    default void setMetadata(String key, MetadataValue value) {
        MetadataManager.getStoreBase().setMetadata(this, key, value);
    }

    @Override
    default List<MetadataValue> getMetadata(String key) {
        return MetadataManager.getStoreBase().getMetadata(this, key);
    }

    @Override
    default boolean hasMetadata(String key) {
        return MetadataManager.getStoreBase().hasMetadata(this, key);
    }

    @Override
    default void removeMetadata(String key, Plugin plugin) {
        MetadataManager.getStoreBase().removeMetadata(this, key, plugin);
    }

    /**
     * Returns a unique identifier for disambiguation.
     * Any object that returns the same value as this will share the same metadata.
     *
     * @return name
     */
    String getName();
}
