/*
 * Copyright (C) 2021 C4
 *
 * This file is part of Diet, a mod made for Minecraft.
 *
 * Diet is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Diet is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Diet.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package top.theillusivec4.diet.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@SuppressWarnings("unused")
public class DietEvent extends PlayerEvent {

  public DietEvent(Player player) {
    super(player);
  }

  @Cancelable
  public static class ConsumeItemStack extends DietEvent {

    private final ItemStack stack;

    public ConsumeItemStack(ItemStack stackIn, Player player) {
      super(player);
      stack = stackIn;
    }

    public ItemStack getStack() {
      return stack;
    }
  }

  @Cancelable
  public static class ApplyDecay extends DietEvent {

    public ApplyDecay(Player player) {
      super(player);
    }
  }

  @Cancelable
  public static class ApplyEffect extends DietEvent {

    public ApplyEffect(Player player) {
      super(player);
    }
  }
}
