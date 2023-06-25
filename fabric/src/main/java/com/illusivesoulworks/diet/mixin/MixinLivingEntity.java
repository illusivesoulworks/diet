package com.illusivesoulworks.diet.mixin;

import com.illusivesoulworks.diet.common.component.DietComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

  @Shadow
  protected ItemStack useItem;

  @Unique
  private ItemStack diet$copy = ItemStack.EMPTY;

  @Inject(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/world/item/ItemStack.finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"),
      method = "completeUsingItem()V")
  private void diet$completeUsingItem1(CallbackInfo cb) {
    this.diet$copy = this.useItem.copy();
  }

  @Inject(
      at = @At(
          value = "INVOKE_ASSIGN",
          target = "net/minecraft/world/item/ItemStack.finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"),
      method = "completeUsingItem()V")
  private void diet$completeUsingItem2(CallbackInfo cb) {

    if (!((LivingEntity) (Object) this).level.isClientSide() &&
        ((LivingEntity) (Object) this) instanceof Player) {
      FoodProperties food = diet$copy.getItem().getFoodProperties();

      if (food != null) {
        DietComponents.DIET_TRACKER.maybeGet(this).ifPresent(diet -> diet.consume(diet$copy));
      }
    }
    diet$copy = ItemStack.EMPTY;
  }
}
