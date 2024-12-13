package ca.fxco.api.pistonlib.config;

import net.fabricmc.api.EnvType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that this field is a Config Value
 * All config value fields must be static and not final
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigValue {

    /**
     * The config description, what does this config do?
     */
    String desc() default "";

    /**
     * More information about the config
     */
    String[] more() default {};

    /**
     * List of keywords that fit the config value
     */
    String[] keyword() default {};

    /**
     * The categories that this config value fits into
     */
    Category[] category() default {};

    /**
     * Config options that are required in order to work
     */
    String[] requires() default {};

    /**
     * Config options that conflict with each other
     */
    String[] conflict() default {};

    /**
     * If the config option requires a restart to work
     */
    boolean requiresRestart() default false;

    /**
     * If this config value fixes a vanilla bug, you can set the bug id's it fixes here
     * Just a default mojira id without the `MC-`
     */
    int[] fixes() default {};

    /**
     * Checks multiple conditions before loading the config option into the config manager
     * @see Condition
     */
    Class<? extends Condition>[] condition() default {};

    /**
     * This class will make sure that the config value is valid, and will convert string inputs to a valid type.
     * @see Parser
     */
    Class<? extends Parser>[] parser() default {};

    /**
     * On which side should option be loaded.
     */
    EnvType[] envType() default {EnvType.SERVER, EnvType.CLIENT};

    /**
     * The class of the condition checked when the rule is parsed, before being added
     * to the Settings Manager.
     * @see Observer
     */
    Class<? extends Observer>[] observer() default {};
}
