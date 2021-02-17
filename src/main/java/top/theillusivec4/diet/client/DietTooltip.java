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
import java.util.List;
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

    for (DietEffectsInfo.AttributeModifier modifier : modifiers) {
      double amount = modifier.getAmount();

      double formattedAmount;
      if (modifier.getOperation() !=
          net.minecraft.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_BASE &&
          modifier.getOperation() !=
              net.minecraft.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_TOTAL) {

        if (modifier.getAttribute().equals(Attributes.KNOCKBACK_RESISTANCE)) {
          formattedAmount = amount * 10.0D;
        } else {
          formattedAmount = amount;
        }
      } else {
        formattedAmount = amount * 100.0D;
      }

      if (amount > 0.0D) {
        tooltips.add((new TranslationTextComponent(
            "attribute.modifier.plus." + modifier.getOperation().getId(),
            ItemStack.DECIMALFORMAT.format(formattedAmount),
            new TranslationTextComponent(modifier.getAttribute().getAttributeName())))
            .mergeStyle(TextFormatting.BLUE));
      } else if (amount < 0.0D) {
        formattedAmount = formattedAmount * -1.0D;
        tooltips.add((new TranslationTextComponent(
            "attribute.modifier.take." + modifier.getOperation().getId(),
            ItemStack.DECIMALFORMAT.format(formattedAmount),
            new TranslationTextComponent(modifier.getAttribute().getAttributeName())))
            .mergeStyle(TextFormatting.RED));
      }
    }

    for (DietEffectsInfo.StatusEffect effect : effects) {
      Effect effect1 = effect.getEffect();
      IFormattableTextComponent iformattabletextcomponent =
          new TranslationTextComponent(effect1.getName());

      if (effect.getAmplifier() > 0) {
        iformattabletextcomponent =
            new TranslationTextComponent("potion.withAmplifier", iformattabletextcomponent,
                new TranslationTextComponent("potion.potency." + effect.getAmplifier()));
      }
      tooltips.add(iformattabletextcomponent.mergeStyle(effect1.getEffectType().getColor()));
    }
    return tooltips;
  }
}
