package com.illusivesoulworks.diet.common.impl;

import com.illusivesoulworks.diet.api.DietApi;
import com.illusivesoulworks.diet.api.type.IDietGroup;
import com.illusivesoulworks.diet.api.type.IDietResult;
import com.illusivesoulworks.diet.common.config.DietConfig;
import com.illusivesoulworks.diet.common.impl.group.DietGroups;
import com.illusivesoulworks.diet.common.util.DietResult;
import com.illusivesoulworks.diet.common.util.DietValueGenerator;
import com.illusivesoulworks.diet.platform.Services;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

public class DietApiImpl extends DietApi {

  private static final Attribute NATURAL_REGEN =
      new RangedAttribute("diet.naturalRegeneration", 1.0d, 0.0d, 2.0d).setSyncable(true);

  @Override
  public Set<IDietGroup> getGroups(Player player, ItemStack input) {
    Set<IDietGroup> groups = new HashSet<>();
    Set<ItemStack> processed = new HashSet<>();
    List<ItemStack> stacks = new ArrayList<>();
    Queue<ItemStack> queue = new ArrayDeque<>();
    queue.add(input);

    while (!queue.isEmpty()) {
      ItemStack next = queue.poll();
      BiFunction<Player, ItemStack, Triple<List<ItemStack>, Integer, Float>> func =
          Services.REGISTRY.getOverride(next.getItem());

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

    for (ItemStack stack : stacks) {
      Set<IDietGroup> found = new HashSet<>();

      for (IDietGroup group : DietGroups.getGroups(player.getLevel())) {

        if (group.contains(stack)) {
          found.add(group);
        }
      }

      if (found.isEmpty()) {
        groups.addAll(DietValueGenerator.get(stack.getItem()).orElse(new HashSet<>()));
      } else {
        groups.addAll(found);
      }
    }
    return groups.isEmpty() ? DietValueGenerator.get(input.getItem()).orElse(new HashSet<>()) :
        groups;
  }

  @Override
  public IDietResult get(Player player, ItemStack input) {
    Set<IDietGroup> groups = getGroups(player, input);

    if (groups.isEmpty()) {
      return DietResult.EMPTY;
    }
    float healing;
    float saturation;
    Item item = input.getItem();
    FoodProperties food = Services.REGISTRY.getFoodProperties(input, player);
    BiFunction<Player, ItemStack, Triple<List<ItemStack>, Integer, Float>> func =
        Services.REGISTRY.getOverride(item);
    Float override = DietConfig.SERVER.getFoodOverride(item);

    if (override != null) {
      healing = override;
      saturation = 0.0f;
    } else if (func != null) {
      Triple<List<ItemStack>, Integer, Float> apply = func.apply(player, input);
      healing = apply.getMiddle();
      saturation = apply.getRight();
    } else if (food != null) {
      healing = food.getNutrition();
      saturation = food.getSaturationModifier();

      if (healing == 0) {
        return DietResult.EMPTY;
      }
    } else {
      Map<IDietGroup, Float> result = new HashMap<>();

      for (IDietGroup group : groups) {
        result.put(group, 0.0f);
      }
      return new DietResult(result);
    }
    return new DietResult(calculate(healing, saturation, groups));
  }

  @Override
  public IDietResult get(Player player, ItemStack input, int healing, float saturation) {
    return get(player, Collections.singletonList(input), healing, saturation);
  }

  @Override
  public IDietResult get(Player player, List<ItemStack> stacks, int food, float saturation) {
    Set<IDietGroup> groups = new HashSet<>();

    for (ItemStack stack : stacks) {
      groups.addAll(getGroups(player, stack));
    }

    if (groups.isEmpty()) {
      return DietResult.EMPTY;
    }
    return new DietResult(calculate(food, saturation, groups));
  }

  private static Map<IDietGroup, Float> calculate(float healing, float saturation,
                                                  Set<IDietGroup> groups) {
    float quality = (healing + (healing * saturation)) / groups.size();
    float gain = (quality * 0.25f) / (quality + 15.0f);
    gain *= Math.pow(1.0f - DietConfig.SERVER.gainPenaltyPerGroup.get(), groups.size() - 1);
    Map<IDietGroup, Float> result = new HashMap<>();

    for (IDietGroup group : groups) {
      float value = (float) (gain * group.getGainMultiplier());
      value = Math.max(0.005f, Math.round(value * 200) / 200.0f);
      result.put(group, value);
    }
    return result;
  }

  @Override
  public Attribute getNaturalRegeneration() {
    return NATURAL_REGEN;
  }
}
