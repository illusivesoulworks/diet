/*
 * Copyright (C) 2021-2023 Illusive Soulworks
 *
 * Diet is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Diet is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Diet.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.diet.platform.services;

import com.illusivesoulworks.diet.api.type.IDietTracker;
import com.illusivesoulworks.diet.common.component.DietComponents;
import com.illusivesoulworks.diet.common.data.DietFabricGroups;
import com.illusivesoulworks.diet.common.data.DietFabricSuites;
import com.illusivesoulworks.diet.common.data.group.DietGroups;
import com.illusivesoulworks.diet.common.data.suite.DietSuites;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public class FabricCapabilityService implements ICapabilityService {

  @Override
  public Optional<? extends IDietTracker> get(Player player) {
    return DietComponents.DIET_TRACKER.maybeGet(player);
  }

  @Override
  public DietGroups getGroupsListener() {
    return new DietFabricGroups();
  }

  @Override
  public DietSuites getSuitesListener() {
    return new DietFabricSuites();
  }
}
