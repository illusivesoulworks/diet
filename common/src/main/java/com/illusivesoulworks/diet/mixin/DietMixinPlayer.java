package com.illusivesoulworks.diet.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.illusivesoulworks.diet.common.util.PlayerSensitive;

@SuppressWarnings("unused")
@Mixin(Player.class)
public class DietMixinPlayer {

  @Shadow
  protected FoodData foodData;

  @SuppressWarnings("ConstantConditions")
  @Inject(at = @At("RETURN"), method = "<init>*")
  public void diet$constructPlayer(CallbackInfo ci) {
    ((PlayerSensitive) foodData).setPlayer((Player) (Object) this);
  }
}