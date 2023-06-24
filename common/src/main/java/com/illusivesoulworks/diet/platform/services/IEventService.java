package com.illusivesoulworks.diet.platform.services;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IEventService {

  boolean fireApplyDecayEvent(Player player);

  boolean fireApplyEffectEvent(Player player);

  boolean fireConsumeStackEvent(ItemStack stack, Player player);
}
