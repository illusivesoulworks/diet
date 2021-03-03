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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.InterModComms;
import org.apache.commons.lang3.tuple.Triple;

public class DietMessage {

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
      }
    });
  }

  public enum Type {
    ITEM("item");

    public final String id;

    Type(String id) {
      this.id = id;
    }
  }
}
