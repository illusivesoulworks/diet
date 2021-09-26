package top.theillusivec4.diet.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.diet.common.util.PlayerSensitive;

@SuppressWarnings("unused")
@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

  @Shadow
  FoodStats foodStats;

  @SuppressWarnings("ConstantConditions")
  @Inject(at = @At("RETURN"), method = "<init>*")
  public void diet$constructPlayer(CallbackInfo ci) {
    ((PlayerSensitive) foodStats).setPlayer((PlayerEntity) (Object) this);
  }
}