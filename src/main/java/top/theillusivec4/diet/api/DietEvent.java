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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

public class DietEvent extends PlayerEvent {

  public DietEvent(PlayerEntity player) {
    super(player);
  }

  @Cancelable
  public static class ConsumeItem extends DietEvent {

    private final ItemStack stack;

    public ConsumeItem(ItemStack stackIn, PlayerEntity player) {
      super(player);
      stack = stackIn;
    }

    public ItemStack getStack() {
      return stack;
    }
  }

  @Cancelable
  public static class ConsumeBlock extends DietEvent {

    private final BlockPos pos;
    private final Hand hand;
    private final Direction direction;

    public ConsumeBlock(BlockPos posIn, Hand handIn, Direction directionIn, PlayerEntity player) {
      super(player);
      pos = posIn;
      hand = handIn;
      direction = directionIn;
    }

    public BlockPos getPos() {
      return pos;
    }

    public Hand getHand() {
      return hand;
    }

    public Direction getDirection() {
      return direction;
    }
  }

  @Cancelable
  public static class ApplyDecay extends DietEvent {

    public ApplyDecay(PlayerEntity player) {
      super(player);
    }
  }
}
