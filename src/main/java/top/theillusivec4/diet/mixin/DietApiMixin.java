package top.theillusivec4.diet.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.diet.api.DietApi;
import top.theillusivec4.diet.common.impl.DietApiImpl;

@SuppressWarnings("unused")
@Mixin(DietApi.class)
public class DietApiMixin {

  private static final DietApi IMPL = new DietApiImpl();

  @Inject(at = @At("HEAD"), method = "getInstance", cancellable = true, remap = false)
  private static void _diet_impl(CallbackInfoReturnable<DietApi> cir) {
    cir.setReturnValue(IMPL);
  }
}
