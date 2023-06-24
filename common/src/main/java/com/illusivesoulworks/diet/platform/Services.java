package com.illusivesoulworks.diet.platform;

import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.platform.services.ICapabilityService;
import com.illusivesoulworks.diet.platform.services.IEventService;
import com.illusivesoulworks.diet.platform.services.INetworkService;
import com.illusivesoulworks.diet.platform.services.IPlatformService;
import com.illusivesoulworks.diet.platform.services.IRegistryService;
import java.util.ServiceLoader;

public class Services {

  public static final IPlatformService PLATFORM = load(IPlatformService.class);
  public static final ICapabilityService CAPABILITY = load(ICapabilityService.class);
  public static final IRegistryService REGISTRY = load(IRegistryService.class);
  public static final IEventService EVENT = load(IEventService.class);
  public static final INetworkService NETWORK = load(INetworkService.class);

  public static <T> T load(Class<T> clazz) {

    final T loadedService = ServiceLoader.load(clazz)
        .findFirst()
        .orElseThrow(
            () -> new NullPointerException("Failed to load service for " + clazz.getName()));
    DietConstants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
    return loadedService;
  }
}
