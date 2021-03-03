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

import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.diet.api.DietCapability;

@SuppressWarnings("unused")
@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {

  @Inject(at = @At("HEAD"), method = "onBlockActivated(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/math/BlockRayTraceResult;)Lnet/minecraft/util/ActionResultType;")
  public void _diet_preBlockActivated(World world, PlayerEntity player, Hand hand,
                                      BlockRayTraceResult result,
                                      CallbackInfoReturnable<ActionResultType> cir) {
    DietCapability.get(player).ifPresent(tracker -> tracker.captureStack(
        player.world.getBlockState(result.getPos())
            .getPickBlock(result, world, result.getPos(), player)));
  }

  @Inject(at = @At("TAIL"), method = "onBlockActivated(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/math/BlockRayTraceResult;)Lnet/minecraft/util/ActionResultType;")
  public void _diet_postBlockActivated(World world, PlayerEntity player, Hand hand,
                                       BlockRayTraceResult result,
                                       CallbackInfoReturnable<ActionResultType> cir) {
    DietCapability.get(player).ifPresent(tracker -> tracker.captureStack(ItemStack.EMPTY));
  }
}
