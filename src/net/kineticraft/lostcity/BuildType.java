package net.kineticraft.lostcity;

import lombok.Getter;
import net.kineticraft.lostcity.mechanics.Mechanic;

import java.util.Arrays;
import java.util.List;

/**
 * Determines which deployment environment we are in, and what mechanics to register.
 *
 * Created by Kneesnap on 6/28/2017.
 */
@Getter
public enum BuildType {
    PRODUCTION,
    BETA,
    DEV;

    private List<Mechanic> dontRegister;

    BuildType(Mechanic... skip) {
        this.dontRegister = Arrays.asList(skip);
    }
}
