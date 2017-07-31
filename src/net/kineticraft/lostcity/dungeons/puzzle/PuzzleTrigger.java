package net.kineticraft.lostcity.dungeons.puzzle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method as being executed when a puzzle trigger is called.
 * Created by Kneesnap on 7/30/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PuzzleTrigger {
    boolean skipCheck() default false;
}
