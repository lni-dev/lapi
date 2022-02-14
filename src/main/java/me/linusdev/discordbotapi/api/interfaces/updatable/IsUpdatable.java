package me.linusdev.discordbotapi.api.interfaces.updatable;

import org.jetbrains.annotations.NonNls;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotates, that the return value of a field or method in an {@link Updatable} class is updatable
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface IsUpdatable {
    /**
     * @return textual explanation, for documentation purposes.
     */
    @NonNls String value() default "";
}
