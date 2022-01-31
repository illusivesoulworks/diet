package top.theillusivec4.diet.mixin;

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
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.DietCapability;

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
    DietCapability.get(player).ifPresent(tracker -> {
      BlockPos pos = result.getBlockPos();
      BlockState state = player.level.getBlockState(pos);
      ItemStack blockStack;
      try {
        blockStack = state.getCloneItemStack(result, world, pos, player);
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
          target = "net/minecraft/world/level/block/state/BlockState.use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"),
      method = "useItemOn")
  public void diet$postBlockActivated(ServerPlayer player, Level world, ItemStack stack,
                                      InteractionHand hand, BlockHitResult result,
                                      CallbackInfoReturnable<InteractionResult> cir) {
    DietCapability.get(player).ifPresent(tracker -> tracker.captureStack(ItemStack.EMPTY));
  }
}
