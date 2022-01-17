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

package top.theillusivec4.diet.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.diet.DietMod;

public class DietCapability {

  @CapabilityInject(IDietTracker.class)
  public static final Capability<IDietTracker> DIET_TRACKER;

  public static final ResourceLocation DIET_TRACKER_ID =
      new ResourceLocation(DietMod.MOD_ID, "diet_tracker");

  static {
    DIET_TRACKER = null;
  }

  public static LazyOptional<IDietTracker> get(PlayerEntity player) {
    return player != null ? player.getCapability(DIET_TRACKER) : LazyOptional.empty();
  }
}
