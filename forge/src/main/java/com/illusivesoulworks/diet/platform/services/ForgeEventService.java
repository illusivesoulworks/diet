/*
 * Copyright (C) 2021-2023 Illusive Soulworks
 *
 * Diet is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Diet is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Diet.  If not, see <https://www.gnu.org/licenses/>.
 */

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
