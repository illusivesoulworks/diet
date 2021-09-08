package top.theillusivec4.diet.common.impl;

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
import top.theillusivec4.diet.api.DietApi;
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.api.IDietResult;
import top.theillusivec4.diet.common.config.DietServerConfig;
import top.theillusivec4.diet.common.group.DietGroups;
import top.theillusivec4.diet.common.util.DietValueGenerator;
import top.theillusivec4.diet.common.util.DietOverride;
import top.theillusivec4.diet.common.util.DietResult;

public class DietApiImpl extends DietApi {

  @Override
  public Set<IDietGroup> getGroups(PlayerEntity player, ItemStack input) {
    Set<IDietGroup> groups = new HashSet<>();
    Set<ItemStack> processed = new HashSet<>();
    List<ItemStack> stacks = new ArrayList<>();
    Queue<ItemStack> queue = new ArrayDeque<>();
    queue.add(input);

    while (!queue.isEmpty()) {
      ItemStack next = queue.poll();
      BiFunction<PlayerEntity, ItemStack, Triple<List<ItemStack>, Integer, Float>> func =
          DietOverride.get(next.getItem());

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

    for (IDietGroup group : DietGroups.get()) {

      for (ItemStack stack : stacks) {

        if (group.contains(stack)) {
          groups.add(group);
        }
      }
    }
    return groups.isEmpty() ? DietValueGenerator.get(input.getItem()).orElse(new HashSet<>()) : groups;
  }

  @Override
  public IDietResult get(PlayerEntity player, ItemStack input) {
    Set<IDietGroup> groups = getGroups(player, input);

    if (groups.isEmpty()) {
      return DietResult.EMPTY;
    }
    float healing;
    float saturation;
    Item item = input.getItem();
    Food food = item.getFood();
    BiFunction<PlayerEntity, ItemStack, Triple<List<ItemStack>, Integer, Float>> func =
        DietOverride.get(item);
    Float override = DietServerConfig.foodOverrides.get(item);

    if (override != null) {
      healing = override;
      saturation = 0.0f;
    } else if (func != null) {
      Triple<List<ItemStack>, Integer, Float> apply = func.apply(player, input);
      healing = apply.getMiddle();
      saturation = apply.getRight();
    } else if (food != null) {
      healing = food.getHealing();
      saturation = food.getSaturation();
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
  public IDietResult get(PlayerEntity player, ItemStack input, int healing, float saturation) {
    Set<IDietGroup> groups = DietApi.getInstance().getGroups(player, input);

    if (groups.isEmpty()) {
      return DietResult.EMPTY;
    }
    return new DietResult(calculate(healing, saturation, groups));
  }

  private static Map<IDietGroup, Float> calculate(float healing, float saturation,
                                                  Set<IDietGroup> groups) {
    float quality = (healing + (healing * saturation)) / groups.size();
    float gain = (quality * 0.25f) / (quality + 15.0f);
    gain *= Math.pow(1.0f - DietServerConfig.gainPenaltyPerGroup, groups.size() - 1);
    Map<IDietGroup, Float> result = new HashMap<>();

    for (IDietGroup group : groups) {
      float value = (float) (gain * group.getGainMultiplier());
      value = Math.max(0.005f, Math.round(value * 200) / 200.0f);
      result.put(group, value);
    }
    return result;
  }
}
