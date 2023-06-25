package com.illusivesoulworks.diet.common.config;

import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.spectrelib.config.SpectreConfig;
import com.illusivesoulworks.spectrelib.config.SpectreConfigLoader;

public class DietConfigLoader {

  public static void setup() {
    SpectreConfigLoader.add(SpectreConfig.Type.CLIENT, DietConfig.CLIENT_SPEC, DietConstants.MOD_ID);
    SpectreConfig cfg =
        SpectreConfigLoader.add(SpectreConfig.Type.SERVER, DietConfig.SERVER_SPEC, DietConstants.MOD_ID);
    cfg.addLoadListener(config -> DietConfig.SERVER.initializedFoodOverrides = false);
    cfg.addReloadListener(config -> DietConfig.SERVER.initializedFoodOverrides = false);
  }
}
