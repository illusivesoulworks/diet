package com.illusivesoulworks.diet;

import com.illusivesoulworks.diet.common.config.DietConfigLoader;
import com.illusivesoulworks.spectrelib.config.SpectreConfigInitializer;

public class DietConfigInitializer implements SpectreConfigInitializer {

  @Override
  public void onInitialize() {
    // NO-OP
  }

  @Override
  public void onInitializeConfig() {
    DietConfigLoader.setup();
  }
}
