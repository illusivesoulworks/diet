package com.illusivesoulworks.diet.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.illusivesoulworks.diet.api.DietApi;
import com.illusivesoulworks.diet.common.impl.DietApiImpl;

@SuppressWarnings("unused")
@Mixin(DietApi.class)
public class DietMixinDietApi {

  private static final DietApi IMPL = new DietApiImpl();

  @Inject(at = @At("HEAD"), method = "getInstance", cancellable = true, remap = false)
  private static void diet$getInstance(CallbackInfoReturnable<DietApi> cir) {
    cir.setReturnValue(IMPL);
  }
}
