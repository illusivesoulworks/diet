package com.illusivesoulworks.diet;

import com.illusivesoulworks.diet.common.config.DietConfigLoader;
import com.illusivesoulworks.spectrelib.config.SpectreConfigInitializer;
import org.quiltmc.loader.api.ModContainer;

public class DietConfigInitializer implements SpectreConfigInitializer {

  @Override
  public void onInitializeConfig(ModContainer modContainer) {
    DietConfigLoader.setup();
  }

  // Hotfix to avoid crashing when loaded with the Fabric-version of SpectreLib
  public void onInitializeConfig() {
    onInitializeConfig(null);
  }
}
