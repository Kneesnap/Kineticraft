package net.kineticraft.lostcity.crake.internal;

import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * A detection object that holds any object.
 * Created by Kneesnap on 7/16/2017.
 */
@Getter
public class GeneralDetection<T> extends Detection {

    private T object;

    public GeneralDetection(Player player, T obj) {
        super(player);
        this.object = obj;
    }

    @Override
    public void onExpire() {
        if (getObject() instanceof Detection)
            ((Detection) getObject()).onExpire();
    }
}
