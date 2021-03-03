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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;
import top.theillusivec4.diet.common.config.DietServerConfig;
import top.theillusivec4.diet.common.group.DietGroup;
import top.theillusivec4.diet.common.group.DietGroups;

public class DietCalculator {

  static final Map<Item, BiFunction<PlayerEntity, ItemStack, Triple<List<ItemStack>, Integer, Float>>>
      items = new HashMap<>();

  public static DietResult get(PlayerEntity player, ItemStack input) {
    Set<DietGroup> groups = getGroups(player, input);

    if (groups.isEmpty()) {
      return DietResult.EMPTY;
    }
    int healing;
    float saturation;
    Item item = input.getItem();
    Food food = item.getFood();
    BiFunction<PlayerEntity, ItemStack, Triple<List<ItemStack>, Integer, Float>> func =
        items.get(item);

    if (func != null) {
      Triple<List<ItemStack>, Integer, Float> apply = func.apply(player, input);
      healing = apply.getMiddle();
      saturation = apply.getRight();
    } else if (food != null) {
      healing = food.getHealing();
      saturation = food.getSaturation();
    } else {
      Map<DietGroup, Float> result = new HashMap<>();

      for (DietGroup group : groups) {
        result.put(group, 0.0f);
      }
      return new DietResult(result);
    }
    return new DietResult(calculate(healing, saturation, groups));
  }

  public static DietResult get(PlayerEntity player, ItemStack input, int healing,
                               float saturation) {
    Set<DietGroup> groups = getGroups(player, input);

    if (groups.isEmpty()) {
      return DietResult.EMPTY;
    }
    return new DietResult(calculate(healing, saturation, groups));
  }

  private static Map<DietGroup, Float> calculate(int healing, float saturation,
                                                 Set<DietGroup> groups) {
    float quality = (healing + (healing * saturation)) / groups.size();
    float gain = (quality * 0.2f) / (quality + 15.0f);
    gain *= Math.pow(1.0f - DietServerConfig.gainPenaltyPerGroup, groups.size() - 1);
    Map<DietGroup, Float> result = new HashMap<>();

    for (DietGroup group : groups) {
      float value = (float) (gain * group.getGainMultiplier());
      value = Math.max(0.005f, Math.round(value * 200) / 200.0f);
      result.put(group, value);
    }
    return result;
  }

  private static Set<DietGroup> getGroups(PlayerEntity player, ItemStack input) {
    Set<DietGroup> groups = new HashSet<>();
    Set<ItemStack> processed = new HashSet<>();
    List<ItemStack> stacks = new ArrayList<>();
    Queue<ItemStack> queue = new ArrayDeque<>();
    queue.add(input);

    while (!queue.isEmpty()) {
      ItemStack next = queue.poll();
      BiFunction<PlayerEntity, ItemStack, Triple<List<ItemStack>, Integer, Float>> func =
          items.get(next.getItem());

      if (func != null) {
        List<ItemStack> candidates = func.apply(player, next).getLeft();

        if (candidates.isEmpty()) {
          stacks.add(next);
        } else {

          for (ItemStack candidate : candidates) {

            if (processed.add(candidate)) {
              queue.add(candidate);
            } else {
              stacks.add(candidate);
            }
          }
        }
      } else {
        stacks.add(next);
      }
    }

    for (DietGroup group : DietGroups.get()) {

      for (ItemStack stack : stacks) {

        if (group.contains(stack)) {
          groups.add(group);
        }
      }
    }
    return groups;
  }
}
