package com.illusivesoulworks.diet.platform.services;

import com.illusivesoulworks.diet.api.type.IDietTracker;
import com.illusivesoulworks.diet.common.capability.DietCapability;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public class ForgeCapabilityService implements ICapabilityService {

  @Override
  public Optional<? extends IDietTracker> get(Player player) {
    return DietCapability.get(player).resolve();
  }
}
