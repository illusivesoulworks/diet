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
public class MixinFoodStats implements PlayerSensitive {

  PlayerEntity player;

  @Inject(at = @At("TAIL"), method = "addStats(IF)V")
  public void diet$addStats(int healing, float saturationModifier, CallbackInfo ci) {
    DietCapability.get(player).ifPresent(tracker -> {
      ItemStack captured = tracker.getCapturedStack();

      if (!captured.isEmpty()) {
        tracker.consume(captured, healing, saturationModifier);
        tracker.captureStack(ItemStack.EMPTY);
      }
    });
  }

  @Override
  public void setPlayer(PlayerEntity playerIn) {
    player = playerIn;
  }
}
