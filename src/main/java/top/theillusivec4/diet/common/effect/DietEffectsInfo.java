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

package top.theillusivec4.diet.common.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

public class DietEffectsInfo {

  private final List<AttributeModifier> modifiers = new ArrayList<>();
  private final List<StatusEffect> effects = new ArrayList<>();

  public void addModifier(Attribute attribute,
                          net.minecraft.entity.ai.attributes.AttributeModifier.Operation operation,
                          float amount) {
    modifiers.add(new AttributeModifier(attribute, operation, amount));
  }

  public void addModifier(Attribute attribute,
                          net.minecraft.entity.ai.attributes.AttributeModifier modifier) {
    modifiers.add(new AttributeModifier(attribute, modifier));
  }

  public void addEffect(Effect effect, int amplifier) {
    effects.add(new StatusEffect(effect, amplifier));
  }

  public void addEffect(EffectInstance effectInstance) {
    effects.add(new StatusEffect(effectInstance));
  }

  public List<AttributeModifier> getModifiers() {
    return modifiers;
  }

  public List<StatusEffect> getEffects() {
    return effects;
  }

  public CompoundNBT write() {
    CompoundNBT tag = new CompoundNBT();
    ListNBT modifiersList = new ListNBT();

    for (AttributeModifier modifier : modifiers) {
      CompoundNBT modifierTag = new CompoundNBT();
      modifierTag.putString("AttributeName",
          Objects.requireNonNull(modifier.attribute.getRegistryName()).toString());
      modifierTag.putFloat("Amount", modifier.amount);
      modifierTag.putInt("Operation", modifier.operation.getId());
      modifiersList.add(modifierTag);
    }
    ListNBT effectsList = new ListNBT();

    for (StatusEffect effect : effects) {
      CompoundNBT effectTag = new CompoundNBT();
      effectTag.putString("EffectName",
          Objects.requireNonNull(effect.effect.getRegistryName()).toString());
      effectTag.putInt("Amplifier", effect.amplifier);
      effectsList.add(effectTag);
    }
    tag.put("Modifiers", modifiersList);
    tag.put("Effects", effectsList);
    return tag;
  }

  public static DietEffectsInfo read(CompoundNBT tag) {
    DietEffectsInfo info = new DietEffectsInfo();
    ListNBT modifiersList = tag.getList("Modifiers", Constants.NBT.TAG_COMPOUND);

    for (int i = 0; i < modifiersList.size(); i++) {
      CompoundNBT modifierTag = modifiersList.getCompound(i);
      String name = modifierTag.getString("AttributeName");
      Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(name));

      if (attribute == null) {
        continue;
      }
      float amount = modifierTag.getFloat("Amount");
      net.minecraft.entity.ai.attributes.AttributeModifier.Operation operation =
          net.minecraft.entity.ai.attributes.AttributeModifier.Operation
              .byId(modifierTag.getInt("Operation"));
      info.addModifier(attribute, operation, amount);
    }
    ListNBT effectsList = tag.getList("Effects", Constants.NBT.TAG_COMPOUND);

    for (int i = 0; i < effectsList.size(); i++) {
      CompoundNBT effectTag = effectsList.getCompound(i);
      String name = effectTag.getString("EffectName");
      Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(name));

      if (effect == null) {
        continue;
      }
      int amplifier = effectTag.getInt("Amplifier");
      info.addEffect(effect, amplifier);
    }
    return info;
  }

  public static final class AttributeModifier {

    private final Attribute attribute;
    private final net.minecraft.entity.ai.attributes.AttributeModifier.Operation operation;
    private final float amount;

    private AttributeModifier(Attribute attributeIn,
                              net.minecraft.entity.ai.attributes.AttributeModifier.Operation operationIn,
                              float amountIn) {
      attribute = attributeIn;
      operation = operationIn;
      amount = amountIn;
    }

    private AttributeModifier(Attribute attributeIn,
                              net.minecraft.entity.ai.attributes.AttributeModifier modifier) {
      attribute = attributeIn;
      operation = modifier.getOperation();
      amount = (float) modifier.getAmount();
    }

    public Attribute getAttribute() {
      return attribute;
    }

    public net.minecraft.entity.ai.attributes.AttributeModifier.Operation getOperation() {
      return operation;
    }

    public float getAmount() {
      return amount;
    }
  }

  public static final class StatusEffect {

    private final Effect effect;
    private final int amplifier;

    private StatusEffect(Effect effectIn, int amplifierIn) {
      effect = effectIn;
      amplifier = amplifierIn;
    }

    private StatusEffect(EffectInstance effectInstance) {
      effect = effectInstance.getPotion();
      amplifier = effectInstance.getAmplifier();
    }

    public Effect getEffect() {
      return effect;
    }

    public int getAmplifier() {
      return amplifier;
    }
  }
}
