package com.illusivesoulworks.diet.platform.services;

import com.illusivesoulworks.diet.api.DietEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ForgeEventService implements IEventService {

  @Override
  public boolean fireApplyDecayEvent(Player player) {
    return MinecraftForge.EVENT_BUS.post(new DietEvent.ApplyDecay(player));
  }

  @Override
  public boolean fireApplyEffectEvent(Player player) {
    return MinecraftForge.EVENT_BUS.post(new DietEvent.ApplyEffect(player));
  }

  @Override
  public boolean fireConsumeStackEvent(ItemStack stack, Player player) {
    return MinecraftForge.EVENT_BUS.post(new DietEvent.ConsumeItemStack(stack, player));
  }
}
