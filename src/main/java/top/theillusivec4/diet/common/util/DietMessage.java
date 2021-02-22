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

package top.theillusivec4.diet.common.util;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.InterModComms;
import org.apache.commons.lang3.tuple.Triple;
import top.theillusivec4.diet.DietMod;

public class DietMessage {

  public static void enqueue() {
    InterModComms.sendTo(DietMod.MOD_ID, Type.BLOCK.id,
        () -> new Tuple<Block, BiFunction<BlockPos, PlayerEntity, BiFunction<Hand, Direction, Tuple<Integer, Float>>>>(
            Blocks.CAKE,
            (pos, player) -> (hand, direction) -> player.canEat(false) ? new Tuple<>(2, 0.1f) :
                new Tuple<>(0, 0.0f)));
  }

  @SuppressWarnings("unchecked")
  public static void process(Stream<InterModComms.IMCMessage> messages) {
    messages.forEach(message -> {
      String method = message.getMethod();
      Object object = message.getMessageSupplier().get();

      if (method.equals(Type.ITEM.id)) {
        Tuple<Item, BiFunction<PlayerEntity, ItemStack, Triple<List<ItemStack>, Integer, Float>>>
            value =
            (Tuple<Item, BiFunction<PlayerEntity, ItemStack, Triple<List<ItemStack>, Integer, Float>>>) object;
        DietCalculator.items.put(value.getA(), value.getB());
      } else if (method.equals(Type.BLOCK.id)) {
        Tuple<Block, BiFunction<BlockPos, PlayerEntity, BiFunction<Hand, Direction, Tuple<Integer, Float>>>>
            value =
            (Tuple<Block, BiFunction<BlockPos, PlayerEntity, BiFunction<Hand, Direction, Tuple<Integer, Float>>>>) object;
        DietCalculator.blocks.put(value.getA(), value.getB());
      }
    });
  }

  public enum Type {
    ITEM("item"),
    BLOCK("block");

    public final String id;

    Type(String id) {
      this.id = id;
    }
  }
}
