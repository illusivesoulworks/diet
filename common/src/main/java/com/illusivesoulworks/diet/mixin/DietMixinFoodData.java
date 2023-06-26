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

package com.illusivesoulworks.diet.mixin;

import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.platform.Services;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.illusivesoulworks.diet.common.util.DietRegeneration;
import com.illusivesoulworks.diet.common.util.PlayerSensitive;

@SuppressWarnings("unused")
@Mixin(FoodData.class)
public class DietMixinFoodData implements PlayerSensitive {

  Player diet_player;

  @Inject(at = @At("TAIL"), method = "eat(IF)V")
  public void diet$eat(int healing, float saturationModifier, CallbackInfo ci) {

    if (diet_player == null) {
      DietConstants.LOG.error("Attempted to add food stats to a null player!");
      return;
    }
    Services.CAPABILITY.get(diet_player).ifPresent(tracker -> {
      ItemStack captured = tracker.getCapturedStack();

      if (!captured.isEmpty()) {
        tracker.consume(captured, healing, saturationModifier);
        tracker.captureStack(ItemStack.EMPTY);
      }
    });
  }

  @ModifyVariable(
      at = @At(
          value = "INVOKE_ASSIGN",
          target = "net/minecraft/world/level/GameRules.getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"),
      method = "tick(Lnet/minecraft/world/entity/player/Player;)V")
  public boolean diet$tick(boolean flag) {
    return diet_player != null ? DietRegeneration.hasRegen(diet_player, flag) : flag;
  }

  @Override
  public void setPlayer(Player playerIn) {
    diet_player = playerIn;
  }
}
