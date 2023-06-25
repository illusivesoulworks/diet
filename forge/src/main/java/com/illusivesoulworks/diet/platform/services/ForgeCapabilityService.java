package com.illusivesoulworks.diet.platform.services;

import com.illusivesoulworks.diet.api.type.IDietTracker;
import com.illusivesoulworks.diet.common.capability.DietCapability;
import com.illusivesoulworks.diet.common.data.group.DietGroups;
import com.illusivesoulworks.diet.common.data.suite.DietSuites;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public class ForgeCapabilityService implements ICapabilityService {

  @Override
  public Optional<? extends IDietTracker> get(Player player) {
    return DietCapability.get(player).resolve();
  }

  @Override
  public DietGroups getGroupsListener() {
    return new DietGroups();
  }

  @Override
  public DietSuites getSuitesListener() {
    return new DietSuites();
  }
}
