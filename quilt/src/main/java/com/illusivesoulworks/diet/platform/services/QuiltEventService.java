package com.illusivesoulworks.diet.platform.services;

import com.illusivesoulworks.diet.api.DietEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class QuiltEventService implements IEventService {

  @Override
  public boolean fireApplyDecayEvent(Player player) {
    return DietEvents.APPLY_DECAY.invoker().applyDecay(player);
  }

  @Override
  public boolean fireApplyEffectEvent(Player player) {
    return DietEvents.APPLY_EFFECT.invoker().applyEffect(player);
  }

  @Override
  public boolean fireConsumeStackEvent(ItemStack stack, Player player) {
    return DietEvents.CONSUME_STACK.invoker().consumeItemStack(stack, player);
  }
}
