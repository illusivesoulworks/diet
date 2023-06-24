package com.illusivesoulworks.diet.platform;

import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.platform.services.IClientService;
import java.util.ServiceLoader;

public class ClientServices {

  public static final IClientService INSTANCE = load(IClientService.class);

  public static <T> T load(Class<T> clazz) {

    final T loadedService = ServiceLoader.load(clazz)
        .findFirst()
        .orElseThrow(
            () -> new NullPointerException("Failed to load service for " + clazz.getName()));
    DietConstants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
    return loadedService;
  }
}
