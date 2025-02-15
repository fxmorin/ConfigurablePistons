package ca.fxco.pistonlib.api.pistonLogic.families;

import ca.fxco.pistonlib.api.PistonLibRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

/**
 * Helper methods for registering and querying piston families.
 *
 * @author Space Walker
 * @since 1.2.0
 */
public class PistonFamilies {

    /**
     * Registers the given piston family to the given namespaced id.
     *
     * @param id     a namespaced id to uniquely identify the piston family
     * @param family the piston family to be registered
     * @return the piston family that was registered
     * @since 1.2.0
     */
    public static PistonFamily register(ResourceLocation id, PistonFamily family) {
        return Registry.register(PistonLibRegistries.PISTON_FAMILY, id, family);
    }

    /**
     * Queries the piston family registered to the given namespaced id.
     *
     * @param id the namespaced id that uniquely identifies the piston family
     * @return the piston family registered to the given namespaced id, or
     *         {@code null} if no piston family is registered to that id
     * @since 1.2.0
     */
    public static PistonFamily get(ResourceLocation id) {
        return PistonLibRegistries.PISTON_FAMILY.getValue(id);
    }

    /**
     * Queries the namespaced id that the given piston family is registered to.
     *
     * @param family the piston family of which the id is queried
     * @return the namespaced id that the piston family is registered to, or
     *         {@code null} if it isn't registered
     * @since 1.2.0
     */
    public static ResourceLocation getId(PistonFamily family) {
        return PistonLibRegistries.PISTON_FAMILY.getKey(family);
    }
}
