package net.kineticraft.lostcity.cutscenes.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allow a field to be null in a CutsceneAction.
 * Created by Kneesnap on 7/29/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AllowNull {
}
