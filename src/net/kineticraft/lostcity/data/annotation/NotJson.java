package net.kineticraft.lostcity.data.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as NOT automatically serialized.
 * (Overrides classes marked as @Json instead of fields)
 *
 * Created by Kneesnap on 6/10/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotJson {

}
