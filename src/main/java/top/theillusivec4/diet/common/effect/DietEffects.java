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

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.common.config.data.EffectConfig;

public class DietEffects {

  private static final List<DietEffect> effects = new ArrayList<>();
  private static final String UUID_PREFIX = "ea4130c8-9065-48a6-9207-ddc020fb9fc8";

  public static void build(List<EffectConfig> configs) {
    effects.clear();

    if (configs != null) {
      int uuidSuffix = 0;

      for (EffectConfig config : configs) {

        if (config.conditions == null) {
          DietMod.LOGGER.error("Found empty condition in diet effect, skipping...");
          continue;
        }

        if (config.status_effects == null && config.attributes == null) {
          DietMod.LOGGER.error("Found empty effect in diet effect, skipping...");
          continue;
        }
        List<EffectConfig.ConditionConfig> conditionConfigs = config.conditions;
        List<EffectConfig.AttributeConfig> attributeConfigs = new ArrayList<>();
        List<EffectConfig.StatusEffectConfig> statusEffectConfigs = new ArrayList<>();

        if (config.attributes != null) {
          attributeConfigs.addAll(config.attributes);
        }

        if (config.status_effects != null) {
          statusEffectConfigs.addAll(config.status_effects);
        }
        List<DietEffect.Condition> conditions = new ArrayList<>();

        for (EffectConfig.ConditionConfig conditionConfig : conditionConfigs) {
          double above = conditionConfig.above != null ? conditionConfig.above : 0.0d;
          double below = conditionConfig.below != null ? conditionConfig.below : 1.0d;

          if (conditionConfig.groups == null || conditionConfig.groups.isEmpty()) {
            DietMod.LOGGER.error("Found empty groups in conditions config, skipping...");
            continue;
          }
          Set<String> groups = new HashSet<>(conditionConfig.groups);
          DietEffect.MatchMethod match = conditionConfig.match != null ? DietEffect.MatchMethod
              .findOrDefault(conditionConfig.match, DietEffect.MatchMethod.AVERAGE) :
              DietEffect.MatchMethod.AVERAGE;
          Set<String> origins =
              conditionConfig.origins != null ? new HashSet<>(conditionConfig.origins) : null;
          DietEffect.OriginsMatchMethod originsMatchMethod = conditionConfig.matchOrigins != null ?
              DietEffect.OriginsMatchMethod
                  .findOrDefault(conditionConfig.matchOrigins, DietEffect.OriginsMatchMethod.ANY) :
              DietEffect.OriginsMatchMethod.ANY;
          Set<String> powers =
              conditionConfig.powers != null ? new HashSet<>(conditionConfig.powers) : null;
          DietEffect.OriginsMatchMethod powersMatchMethod = conditionConfig.matchPowers != null ?
              DietEffect.OriginsMatchMethod
                  .findOrDefault(conditionConfig.matchPowers, DietEffect.OriginsMatchMethod.ANY) :
              DietEffect.OriginsMatchMethod.ANY;
          conditions.add(
              new DietEffect.Condition(groups, match, above, below, origins, originsMatchMethod,
                  powers, powersMatchMethod));
        }
        List<DietEffect.DietAttribute> attributes = new ArrayList<>();

        for (EffectConfig.AttributeConfig attributeConfig : attributeConfigs) {

          if (attributeConfig.name == null || attributeConfig.amount == null ||
              attributeConfig.operation == null) {
            DietMod.LOGGER.error("Found missing values in attributes config, skipping...");
            continue;
          }
          Attribute att =
              ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeConfig.name));

          if (att == null) {
            DietMod.LOGGER
                .error("Found invalid attribute name " + attributeConfig.name + ", skipping...");
            continue;
          }
          AttributeModifier.Operation operation = getOperation(attributeConfig.operation);
          double amount = attributeConfig.amount;
          double increment = attributeConfig.increment != null ? attributeConfig.increment : amount;
          attributes.add(new DietEffect.DietAttribute(att, operation, amount, increment));
        }
        List<DietEffect.DietStatusEffect> statusEffects = new ArrayList<>();

        for (EffectConfig.StatusEffectConfig statusEffectConfig : statusEffectConfigs) {

          if (statusEffectConfig.name == null) {
            DietMod.LOGGER.error("Found missing name for status effect config, skipping...");
            continue;
          }
          MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(
              statusEffectConfig.name));

          if (effect == null) {
            DietMod.LOGGER.error(
                "Found invalid status effect name " + statusEffectConfig.name + ", skipping...");
            continue;
          }
          int power = statusEffectConfig.power != null ? statusEffectConfig.power : 1;
          int increment =
              statusEffectConfig.increment != null ? statusEffectConfig.increment : power;
          statusEffects.add(new DietEffect.DietStatusEffect(effect, power, increment));
        }
        UUID uuid = UUID.nameUUIDFromBytes((UUID_PREFIX + uuidSuffix).getBytes());
        uuidSuffix++;
        effects.add(new DietEffect(uuid, attributes, statusEffects, conditions));
      }
    }
  }

  public static List<DietEffect> get() {
    return ImmutableList.copyOf(effects);
  }

  private static AttributeModifier.Operation getOperation(String name) {
    if (name.equals("multiply_total")) {
      return AttributeModifier.Operation.MULTIPLY_TOTAL;
    } else if (name.equals("multiply_base")) {
      return AttributeModifier.Operation.MULTIPLY_BASE;
    }
    return AttributeModifier.Operation.ADDITION;
  }
}
