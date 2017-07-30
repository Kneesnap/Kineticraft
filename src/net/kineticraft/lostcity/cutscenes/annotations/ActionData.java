package net.kineticraft.lostcity.cutscenes.annotations;

import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Data about a cutscene action, shows in the editor.
 * Created by Kneesnap on 7/28/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ActionData {
    Material value() default Material.DIRT;
    int meta() default 0;
}
