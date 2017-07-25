package net.kineticraft.lostcity.mechanics.system;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark which build types a module should not be enabled on.
 * Created by Kneesnap on 7/20/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Restrict {

    /**
     * An array of build types this should not register on.
     * @return types
     */
    BuildType[] value();
}
