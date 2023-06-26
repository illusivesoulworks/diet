package com.illusivesoulworks.diet.platform.services;

import com.illusivesoulworks.diet.api.type.IDietTracker;
import com.illusivesoulworks.diet.common.component.DietComponents;
import com.illusivesoulworks.diet.common.data.DietQuiltGroups;
import com.illusivesoulworks.diet.common.data.DietQuiltSuites;
import com.illusivesoulworks.diet.common.data.group.DietGroups;
import com.illusivesoulworks.diet.common.data.suite.DietSuites;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public class QuiltCapabilityService implements ICapabilityService {

  @Override
  public Optional<? extends IDietTracker> get(Player player) {
    return DietComponents.DIET_TRACKER.maybeGet(player);
  }

  @Override
  public DietGroups getGroupsListener() {
    return new DietQuiltGroups();
  }

  @Override
  public DietSuites getSuitesListener() {
    return new DietQuiltSuites();
  }
}
