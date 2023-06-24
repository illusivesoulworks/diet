/*
 * Copyright (C) 2021 C4
 *
 * This file is part of Diet, a mod made for Minecraft.
 *
 * Diet is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Diet is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Diet.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.illusivesoulworks.diet.common.capability;

import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.api.type.IDietTracker;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

public class DietCapability {

  public static final Capability<IDietTracker> DIET_TRACKER =
      CapabilityManager.get(new CapabilityToken<>() {
      });

  public static final ResourceLocation DIET_TRACKER_ID =
      new ResourceLocation(DietConstants.MOD_ID, "diet_tracker");

  private static final Map<UUID, LazyOptional<IDietTracker>> SERVER_CACHE = new HashMap<>();
  private static final Map<UUID, LazyOptional<IDietTracker>> CLIENT_CACHE = new HashMap<>();

  public static LazyOptional<IDietTracker> get(final Player player) {
//    UUID key = player.getUUID();
//    Map<UUID, LazyOptional<IDietTracker>> cache =
//        player.getLevel().isClientSide() ? CLIENT_CACHE : SERVER_CACHE;
//    return cache.computeIfAbsent(key, (k) -> {
//      LazyOptional<IDietTracker> opt = player.getCapability(DIET_TRACKER);
//      opt.addListener((v) -> cache.remove(key));
//      return opt;
//    });
    return player.getCapability(DIET_TRACKER);
  }
}
