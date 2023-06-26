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

package com.illusivesoulworks.diet.common.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import org.apache.commons.lang3.tuple.Triple;

public class DietOverride {

  private static final Map<Item, BiFunction<Player, ItemStack, Triple<List<ItemStack>, Integer, Float>>>
      items = new HashMap<>();

  @SuppressWarnings("unchecked")
  public static void process(Stream<InterModComms.IMCMessage> messages) {
    messages.forEach(message -> {
      String method = message.method();
      Object object = message.messageSupplier().get();

      if (method.equals(Type.ITEM.id)) {
        Tuple<Item, BiFunction<Player, ItemStack, Triple<List<ItemStack>, Integer, Float>>>
            value =
            (Tuple<Item, BiFunction<Player, ItemStack, Triple<List<ItemStack>, Integer, Float>>>) object;
        items.put(value.getA(), value.getB());
      }
    });
  }

  public static BiFunction<Player, ItemStack, Triple<List<ItemStack>, Integer, Float>> get(
      Item item) {
    return items.get(item);
  }

  public enum Type {
    ITEM("item");

    public final String id;

    Type(String id) {
      this.id = id;
    }
  }
}
