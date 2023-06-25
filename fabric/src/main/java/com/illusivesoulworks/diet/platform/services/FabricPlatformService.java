package com.illusivesoulworks.diet.platform.services;

import com.illusivesoulworks.diet.platform.services.IPlatformService;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformService implements IPlatformService {

  @Override
  public String getPlatformName() {
    return "Fabric";
  }

  @Override
  public boolean isModLoaded(String modId) {
    return FabricLoader.getInstance().isModLoaded(modId);
  }

  @Override
  public boolean isDevelopmentEnvironment() {
    return FabricLoader.getInstance().isDevelopmentEnvironment();
  }

  @Override
  public Path getGameDir() {
    return FabricLoader.getInstance().getGameDir();
  }
}
