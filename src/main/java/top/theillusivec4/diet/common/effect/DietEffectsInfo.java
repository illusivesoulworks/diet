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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;

public class DietEffectsInfo {

  private final List<AttributeModifier> modifiers = new ArrayList<>();
  private final List<StatusEffect> effects = new ArrayList<>();

  public void addModifier(Attribute attribute,
                          net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation operation,
                          float amount) {
    modifiers.add(new AttributeModifier(attribute, operation, amount));
  }

  public void addModifier(Attribute attribute,
                          net.minecraft.world.entity.ai.attributes.AttributeModifier modifier) {
    modifiers.add(new AttributeModifier(attribute, modifier));
  }

  public void addEffect(MobEffect effect, int amplifier) {
    effects.add(new StatusEffect(effect, amplifier));
  }

  public void addEffect(MobEffectInstance effectInstance) {
    effects.add(new StatusEffect(effectInstance));
  }

  public List<AttributeModifier> getModifiers() {
    return modifiers;
  }

  public List<StatusEffect> getEffects() {
    return effects;
  }

  public CompoundTag write() {
    CompoundTag tag = new CompoundTag();
    ListTag modifiersList = new ListTag();

    for (AttributeModifier modifier : modifiers) {
      CompoundTag modifierTag = new CompoundTag();
      modifierTag.putString("AttributeName",
          Objects.requireNonNull(ForgeRegistries.ATTRIBUTES.getKey(modifier.attribute).getNamespace().toString()));
      modifierTag.putFloat("Amount", modifier.amount);
      modifierTag.putInt("Operation", modifier.operation.toValue());
      modifiersList.add(modifierTag);
    }
    ListTag effectsList = new ListTag();

    for (StatusEffect effect : effects) {
      CompoundTag effectTag = new CompoundTag();
      effectTag.putString("EffectName",
          Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.getKey(effect.effect).getNamespace().toString()));
      effectTag.putInt("Amplifier", effect.amplifier);
      effectsList.add(effectTag);
    }
    tag.put("Modifiers", modifiersList);
    tag.put("Effects", effectsList);
    return tag;
  }

  public static DietEffectsInfo read(CompoundTag tag) {
    DietEffectsInfo info = new DietEffectsInfo();
    ListTag modifiersList = tag.getList("Modifiers", Tag.TAG_COMPOUND);

    for (int i = 0; i < modifiersList.size(); i++) {
      CompoundTag modifierTag = modifiersList.getCompound(i);
      String name = modifierTag.getString("AttributeName");
      Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(name));

      if (attribute == null) {
        continue;
      }
      float amount = modifierTag.getFloat("Amount");
      net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation operation =
          net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation
              .fromValue(modifierTag.getInt("Operation"));
      info.addModifier(attribute, operation, amount);
    }
    ListTag effectsList = tag.getList("Effects", Tag.TAG_COMPOUND);

    for (int i = 0; i < effectsList.size(); i++) {
      CompoundTag effectTag = effectsList.getCompound(i);
      String name = effectTag.getString("EffectName");
      MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(name));

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
    private final net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation operation;
    private final float amount;

    private AttributeModifier(Attribute attributeIn,
                              net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation operationIn,
                              float amountIn) {
      attribute = attributeIn;
      operation = operationIn;
      amount = amountIn;
    }

    private AttributeModifier(Attribute attributeIn,
                              net.minecraft.world.entity.ai.attributes.AttributeModifier modifier) {
      attribute = attributeIn;
      operation = modifier.getOperation();
      amount = (float) modifier.getAmount();
    }

    public Attribute getAttribute() {
      return attribute;
    }

    public net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation getOperation() {
      return operation;
    }

    public float getAmount() {
      return amount;
    }
  }

  public static final class StatusEffect {

    private final MobEffect effect;
    private final int amplifier;

    private StatusEffect(MobEffect effectIn, int amplifierIn) {
      effect = effectIn;
      amplifier = amplifierIn;
    }

    private StatusEffect(MobEffectInstance effectInstance) {
      effect = effectInstance.getEffect();
      amplifier = effectInstance.getAmplifier();
    }

    public MobEffect getEffect() {
      return effect;
    }

    public int getAmplifier() {
      return amplifier;
    }
  }
}