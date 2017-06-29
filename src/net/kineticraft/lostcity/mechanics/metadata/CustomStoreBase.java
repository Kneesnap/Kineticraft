package net.kineticraft.lostcity.mechanics.metadata;

import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;

/**
 * Holds metadata values.
 *
 * Created by Kneesnap on 6/28/2017.
 */
public class CustomStoreBase extends MetadataStoreBase<MetadataHolder> implements MetadataStore<MetadataHolder> {
    @Override
    protected String disambiguate(MetadataHolder holder, String key) {
        return holder.getName() + ":" + key;
    }
}
