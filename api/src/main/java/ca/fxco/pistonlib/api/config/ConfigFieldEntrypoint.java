package ca.fxco.pistonlib.api.config;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Use this to add/override other mod config fields.
 *
 * @author Foxy
 * @since 1.0.4
 */
public interface ConfigFieldEntrypoint {

    /**
     * Used to add custom parsedValues to other mod's configManager
     *
     * @return map with modid of the mod as a key and list of parsedValues to add as a value
     * @since 1.0.4
     */
    Map<String, List<Field>> getConfigFields();

}
