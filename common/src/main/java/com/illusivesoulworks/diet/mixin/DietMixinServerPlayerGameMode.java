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
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(ServerPlayerGameMode.class)
public class DietMixinServerPlayerGameMode {

  @Inject(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/world/level/block/state/BlockState.use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"),
      method = "useItemOn")
  public void diet$preBlockActivated(ServerPlayer player, Level world, ItemStack stack,
                                     InteractionHand hand, BlockHitResult result,
                                     CallbackInfoReturnable<InteractionResult> cir) {
    Services.CAPABILITY.get(player).ifPresent(tracker -> {
      BlockPos pos = result.getBlockPos();
      BlockState state = player.level().getBlockState(pos);
      ItemStack blockStack;
      try {
        blockStack = Services.REGISTRY.getPickStack(state, result, world, pos, player);
      } catch (Exception e) {
        DietConstants.LOG.debug("Error getting stack from {}", state);
        return;
      }
      tracker.captureStack(blockStack);
    });
  }

  @Inject(
      at = @At(
          value = "INVOKE_ASSIGN",
          target = "net/minecraft/world/level/block/state/BlockState.use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"),
      method = "useItemOn")
  public void diet$postBlockActivated(ServerPlayer player, Level world, ItemStack stack,
                                      InteractionHand hand, BlockHitResult result,
                                      CallbackInfoReturnable<InteractionResult> cir) {
    Services.CAPABILITY.get(player).ifPresent(tracker -> tracker.captureStack(ItemStack.EMPTY));
  }
}
