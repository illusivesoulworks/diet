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

package com.illusivesoulworks.diet.client.screen;

import com.illusivesoulworks.diet.api.DietApi;
import com.illusivesoulworks.diet.common.impl.effect.DietEffectsInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

public class DietTooltip {

  public static List<Component> getEffects() {
    List<DietEffectsInfo.AttributeModifier> modifiers = DietScreen.tooltip.getModifiers();
    List<DietEffectsInfo.StatusEffect> effects = DietScreen.tooltip.getEffects();

    if (modifiers.isEmpty() && effects.isEmpty()) {
      return new ArrayList<>();
    }
    List<Component> tooltips = new ArrayList<>();
    tooltips.add(Component.translatable("tooltip.diet.effects"));
    tooltips.add(Component.empty());
    Map<Attribute, AttributeTooltip> mergedAttributes = new HashMap<>();

    for (DietEffectsInfo.AttributeModifier modifier : modifiers) {
      mergedAttributes.computeIfAbsent(modifier.getAttribute(), (k) -> new AttributeTooltip())
          .merge(modifier);
    }

    for (Map.Entry<Attribute, AttributeTooltip> attribute : mergedAttributes.entrySet()) {
      AttributeTooltip info = attribute.getValue();
      Attribute key = attribute.getKey();

      if (key == DietApi.getInstance().getNaturalRegeneration()) {
        float val = (info.added + info.added * info.baseMultiplier) * info.totalMultiplier;

        if (val < 1.0f) {
          tooltips.add(Component.translatable("attribute.diet.modifier.disabled",
              Component.translatable(key.getDescriptionId())).withStyle(ChatFormatting.RED));
        }
      } else {
        addAttributeTooltip(tooltips, info.added, AttributeModifier.Operation.ADDITION, key);
        addAttributeTooltip(tooltips, info.baseMultiplier,
            AttributeModifier.Operation.MULTIPLY_BASE, key);
        addAttributeTooltip(tooltips, info.totalMultiplier - 1.0f,
            AttributeModifier.Operation.MULTIPLY_TOTAL, key);
      }
    }
    Map<MobEffect, Integer> mergedEffects = new HashMap<>();

    for (DietEffectsInfo.StatusEffect effect : effects) {
      mergedEffects.compute(effect.getEffect(),
          (k, v) -> v == null ? effect.getAmplifier() : Math.max(v, effect.getAmplifier()));
    }

    for (Map.Entry<MobEffect, Integer> effect : mergedEffects.entrySet()) {
      MobEffect effect1 = effect.getKey();
      MutableComponent iformattabletextcomponent =
          Component.translatable(effect1.getDescriptionId());

      if (effect.getValue() > 0) {
        iformattabletextcomponent =
            Component.translatable("potion.withAmplifier", iformattabletextcomponent,
                Component.translatable("potion.potency." + effect.getValue()));
      }
      tooltips.add(
          iformattabletextcomponent.withStyle(effect1.getCategory().getTooltipFormatting()));
    }
    return tooltips;
  }

  private static void addAttributeTooltip(List<Component> tooltips, float amount,
                                          AttributeModifier.Operation operation,
                                          Attribute attribute) {
    double formattedAmount;

    if (operation != AttributeModifier.Operation.MULTIPLY_BASE &&
        operation != AttributeModifier.Operation.MULTIPLY_TOTAL) {

      if (attribute.equals(Attributes.KNOCKBACK_RESISTANCE)) {
        formattedAmount = amount * 10.0D;
      } else {
        formattedAmount = amount;
      }
    } else {
      formattedAmount = amount * 100.0D;
    }

    if (amount > 0.0D) {
      tooltips.add((Component.translatable("attribute.modifier.plus." + operation.toValue(),
          ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(formattedAmount),
          Component.translatable(attribute.getDescriptionId()))).withStyle(ChatFormatting.BLUE));
    } else if (amount < 0.0D) {
      formattedAmount = formattedAmount * -1.0D;
      tooltips.add((Component.translatable("attribute.modifier.take." + operation.toValue(),
          ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(formattedAmount),
          Component.translatable(attribute.getDescriptionId()))).withStyle(ChatFormatting.RED));
    }
  }

  private static class AttributeTooltip {

    float added = 0;
    float baseMultiplier = 0.0f;
    float totalMultiplier = 1.0f;

    void merge(DietEffectsInfo.AttributeModifier modifier) {
      float amount = modifier.getAmount();

      if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE) {
        baseMultiplier += amount;
      } else if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
        totalMultiplier *= 1.0f + amount;
      } else {
        added += amount;
      }
    }
  }
}
