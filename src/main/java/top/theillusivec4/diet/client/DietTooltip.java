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

package top.theillusivec4.diet.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import top.theillusivec4.diet.common.effect.DietEffectsInfo;

public class DietTooltip {

  public static List<ITextComponent> getEffects() {
    List<DietEffectsInfo.AttributeModifier> modifiers = DietScreen.tooltip.getModifiers();
    List<DietEffectsInfo.StatusEffect> effects = DietScreen.tooltip.getEffects();

    if (modifiers.isEmpty() && effects.isEmpty()) {
      return new ArrayList<>();
    }
    List<ITextComponent> tooltips = new ArrayList<>();
    tooltips.add(new TranslationTextComponent("tooltip.diet.effects"));
    tooltips.add(StringTextComponent.EMPTY);
    Map<Attribute, AttributeTooltip> mergedAttributes = new HashMap<>();

    for (DietEffectsInfo.AttributeModifier modifier : modifiers) {
      mergedAttributes.computeIfAbsent(modifier.getAttribute(), (k) -> new AttributeTooltip())
          .merge(modifier);
    }

    for (Map.Entry<Attribute, AttributeTooltip> attribute : mergedAttributes.entrySet()) {
      AttributeTooltip info = attribute.getValue();
      Attribute key = attribute.getKey();
      addAttributeTooltip(tooltips, info.added, AttributeModifier.Operation.ADDITION, key);
      addAttributeTooltip(tooltips, info.baseMultiplier, AttributeModifier.Operation.MULTIPLY_BASE,
          key);
      addAttributeTooltip(tooltips, info.totalMultiplier - 1.0f,
          AttributeModifier.Operation.MULTIPLY_TOTAL, key);
    }
    Map<Effect, Integer> mergedEffects = new HashMap<>();

    for (DietEffectsInfo.StatusEffect effect : effects) {
      mergedEffects.compute(effect.getEffect(),
          (k, v) -> v == null ? effect.getAmplifier() : Math.max(v, effect.getAmplifier()));
    }

    for (Map.Entry<Effect, Integer> effect : mergedEffects.entrySet()) {
      Effect effect1 = effect.getKey();
      IFormattableTextComponent iformattabletextcomponent =
          new TranslationTextComponent(effect1.getName());

      if (effect.getValue() > 0) {
        iformattabletextcomponent =
            new TranslationTextComponent("potion.withAmplifier", iformattabletextcomponent,
                new TranslationTextComponent("potion.potency." + effect.getValue()));
      }
      tooltips.add(iformattabletextcomponent.mergeStyle(effect1.getEffectType().getColor()));
    }
    return tooltips;
  }

  private static void addAttributeTooltip(List<ITextComponent> tooltips, float amount,
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
      tooltips.add((new TranslationTextComponent("attribute.modifier.plus." + operation.getId(),
          ItemStack.DECIMALFORMAT.format(formattedAmount),
          new TranslationTextComponent(attribute.getAttributeName())))
          .mergeStyle(TextFormatting.BLUE));
    } else if (amount < 0.0D) {
      formattedAmount = formattedAmount * -1.0D;
      tooltips.add((new TranslationTextComponent("attribute.modifier.take." + operation.getId(),
          ItemStack.DECIMALFORMAT.format(formattedAmount),
          new TranslationTextComponent(attribute.getAttributeName())))
          .mergeStyle(TextFormatting.RED));
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
