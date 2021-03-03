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

package top.theillusivec4.diet.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.diet.api.DietCapability;
import top.theillusivec4.diet.common.util.PlayerSensitive;

@SuppressWarnings("unused")
@Mixin(FoodStats.class)
public class FoodStatsMixin implements PlayerSensitive {

  PlayerEntity player;

  @Inject(at = @At("HEAD"), method = "addStats(IF)V")
  public void _diet_addStats(int healing, float saturationModifier, CallbackInfo ci) {
    DietCapability.get(player).ifPresent(tracker -> {
      ItemStack captured = tracker.getCapturedStack();

      if (!captured.isEmpty()) {
        tracker.consume(captured, healing, saturationModifier);
      }
    });
  }

  public void setPlayer(PlayerEntity playerIn) {
    player = playerIn;
  }
}
