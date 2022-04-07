package top.theillusivec4.diet.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.DietCapability;
import top.theillusivec4.diet.common.util.DietRegeneration;
import top.theillusivec4.diet.common.util.PlayerSensitive;

@SuppressWarnings("unused")
// Priority is slightly higher to inject into Tough as Nail's @Overwrite in the same class
// https://github.com/TheIllusiveC4/Diet/issues/123
@Mixin(value = FoodStats.class, priority = 1100)
public class MixinFoodStats implements PlayerSensitive {

  PlayerEntity diet_player;

  @Inject(at = @At("TAIL"), method = "addStats(IF)V")
  public void diet$addStats(int healing, float saturationModifier, CallbackInfo ci) {

    if (diet_player == null) {
      DietMod.LOGGER.error("Attempted to add food stats to a null player!");
      return;
    }
    DietCapability.get(diet_player).ifPresent(tracker -> {
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
          target = "net/minecraft/world/GameRules.getBoolean(Lnet/minecraft/world/GameRules$RuleKey;)Z"),
      method = "tick(Lnet/minecraft/entity/player/PlayerEntity;)V")
  public boolean diet$tick(boolean flag) {
    return diet_player != null ? DietRegeneration.hasRegen(diet_player, flag) : flag;
  }

  @Override
  public void setPlayer(PlayerEntity playerIn) {
    diet_player = playerIn;
  }
}
