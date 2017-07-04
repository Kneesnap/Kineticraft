package net.kineticraft.lostcity.data.reflect;

import net.kineticraft.lostcity.data.JsonData;

/**
 * Base for dynamic byte-code generation for json serialization.
 *
 * Created by Kneesnap on 7/3/2017.
 */
public abstract class Serializable {

    /**
     * Serialize this class as JsonData.
     * @return data
     */
    public abstract JsonData save();

    /**
     * Deserialize this class from JsonData.
     * @param data
     */
    //public abstract void load(JsonData data);
}
