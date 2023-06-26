package com.illusivesoulworks.diet.mixin;

import com.illusivesoulworks.diet.api.DietApi;
import com.illusivesoulworks.diet.api.type.IDietTracker;
import com.illusivesoulworks.diet.common.component.DietComponents;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class MixinPlayer {

  @Inject(at = @At("RETURN"), method = "createAttributes()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;")
  private static void diet$createAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
    cir.getReturnValue().add(DietApi.getInstance().getNaturalRegeneration());
  }

  @Inject(at = @At("TAIL"), method = "tick")
  public void diet$tick(CallbackInfo ci) {
    DietComponents.DIET_TRACKER.maybeGet(this).ifPresent(IDietTracker::tick);
  }
}
