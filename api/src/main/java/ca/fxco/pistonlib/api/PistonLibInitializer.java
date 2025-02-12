package ca.fxco.pistonlib.api;

import ca.fxco.pistonlib.api.pistonLogic.families.PistonFamilies;
import ca.fxco.pistonlib.api.pistonLogic.sticky.StickyGroups;

/**
 * Interface for registering custom piston families and sticky groups.
 *
 * @author Space Walker
 * @since 1.0.4
 */
public interface PistonLibInitializer {

    /**
     * The first method to be called.
     *
     * @since 1.0.4
     */
    default void initialize() {}

    /**
     * Register custom piston families through {@link PistonFamilies#register}
     *
     * @since 1.0.4
     */
    default void registerPistonFamilies() {}

    /**
     * Register custom sticky groups through {@link StickyGroups#register}
     *
     * @since 1.0.4
     */
    default void registerStickyGroups() {}

    /**
     * Initialize custom registries, blocks, items, etc.
     *
     * @since 1.0.4
     */
    default void bootstrap() {}

}
