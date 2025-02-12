package ca.fxco.pistonlib;

import ca.fxco.pistonlib.api.config.ConfigManager;
import ca.fxco.pistonlib.api.config.ConfigManagerEntrypoint;

public class PistonLibConfigGetter implements ConfigManagerEntrypoint {

    @Override
    public ConfigManager getConfigManager() {
        return PistonLib.getConfigManager();
    }

}
