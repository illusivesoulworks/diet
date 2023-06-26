package com.illusivesoulworks.diet.platform.services;

import java.nio.file.Path;
import org.quiltmc.loader.api.QuiltLoader;

public class QuiltPlatformService implements IPlatformService {

  @Override
  public String getPlatformName() {
    return "Quilt";
  }

  @Override
  public boolean isModLoaded(String modId) {
    return QuiltLoader.isModLoaded(modId);
  }

  @Override
  public boolean isDevelopmentEnvironment() {
    return QuiltLoader.isDevelopmentEnvironment();
  }

  @Override
  public Path getGameDir() {
    return QuiltLoader.getGameDir();
  }
}
