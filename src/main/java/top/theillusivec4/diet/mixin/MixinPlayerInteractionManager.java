package top.theillusivec4.diet.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.DietCapability;

@SuppressWarnings("unused")
@Mixin(PlayerInteractionManager.class)
public class MixinPlayerInteractionManager {

  @Inject(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/block/BlockState.onBlockActivated(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/math/BlockRayTraceResult;)Lnet/minecraft/util/ActionResultType;"),
      method = "func_219441_a")
  public void diet$preBlockActivated(ServerPlayerEntity player, World world, ItemStack stack,
                                     Hand hand, BlockRayTraceResult result,
                                     CallbackInfoReturnable<ActionResultType> cir) {
    DietCapability.get(player).ifPresent(tracker -> {
      BlockPos pos = result.getPos();
      BlockState state = player.world.getBlockState(pos);
      ItemStack blockStack;
      try {
        blockStack = state.getPickBlock(result, world, pos, player);
      } catch (Exception e) {
        DietMod.LOGGER.debug("Error getting stack from {}", state);
        return;
      }
      tracker.captureStack(blockStack);
    });
  }

  @Inject(
      at = @At(
          value = "INVOKE_ASSIGN",
          target = "net/minecraft/block/BlockState.onBlockActivated(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/math/BlockRayTraceResult;)Lnet/minecraft/util/ActionResultType;"),
      method = "func_219441_a")
  public void diet$postBlockActivated(ServerPlayerEntity player, World world, ItemStack stack,
                                      Hand hand, BlockRayTraceResult result,
                                      CallbackInfoReturnable<ActionResultType> cir) {
    DietCapability.get(player).ifPresent(tracker -> tracker.captureStack(ItemStack.EMPTY));
  }
}
