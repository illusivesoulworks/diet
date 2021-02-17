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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.potion.Effect;
import top.theillusivec4.diet.DietMod;

public class DietEffect {

  public final List<DietAttribute> attributes;
  public final List<DietStatusEffect> statusEffects;
  public final List<Condition> conditions;
  public final UUID uuid;

  public DietEffect(UUID uuid, List<DietAttribute> attributes, List<DietStatusEffect> statusEffects,
                    List<Condition> conditions) {
    this.attributes = attributes;
    this.statusEffects = statusEffects;
    this.conditions = conditions;
    this.uuid = uuid;
  }

  public static class DietAttribute {
    public final Attribute attribute;
    public final AttributeModifier.Operation operation;
    public final double amount;

    public DietAttribute(Attribute attribute, AttributeModifier.Operation operation,
                         double amount) {
      this.attribute = attribute;
      this.operation = operation;
      this.amount = amount;
    }
  }

  public static class DietStatusEffect {
    public final Effect effect;
    public final int power;

    public DietStatusEffect(Effect effect, int power) {
      this.effect = effect;
      this.power = power;
    }
  }

  public static class Condition {
    public final Set<String> groups;
    public final MatchMethod match;
    public final double above;
    public final double below;

    public Condition(Set<String> groups, MatchMethod match, double above, double below) {
      this.groups = groups;
      this.match = match;
      this.above = above;
      this.below = below;
    }

    public int getMatches(Map<String, Float> values) {
      return match.getMatches(groups, values, (float) above, (float) below);
    }
  }

  public enum MatchMethod {
    EVERY {
      @Override
      int getMatches(Set<String> groups, Map<String, Float> values, float above, float below) {
        int count = 0;

        for (String group : groups) {
          Float value = values.get(group);

          if (value != null && MatchMethod.inRange(value, above, below)) {
            count++;
          }
        }
        return count;
      }
    },
    ANY {
      @Override
      int getMatches(Set<String> groups, Map<String, Float> values, float above, float below) {

        for (String group : groups) {
          Float value = values.get(group);

          if (value != null && MatchMethod.inRange(value, above, below)) {
            return 1;
          }
        }
        return 0;
      }
    },
    AVERAGE {
      @Override
      int getMatches(Set<String> groups, Map<String, Float> values, float above, float below) {
        float sum = 0;

        for (String group : groups) {
          Float value = values.get(group);

          if (value != null) {
            sum += value;
          }
        }
        return MatchMethod.inRange(sum / (float) groups.size(), above, below) ? 1 : 0;
      }
    },
    ALL {
      @Override
      int getMatches(Set<String> groups, Map<String, Float> values, float above, float below) {

        for (String group : groups) {
          Float value = values.get(group);

          if (value == null || !MatchMethod.inRange(value, above, below)) {
            return 0;
          }
        }
        return 1;
      }
    },
    NONE {
      @Override
      int getMatches(Set<String> groups, Map<String, Float> values, float above, float below) {

        for (String group : groups) {
          Float value = values.get(group);

          if (value != null && MatchMethod.inRange(value, above, below)) {
            return 0;
          }
        }
        return 1;
      }
    };

    abstract int getMatches(Set<String> groups, Map<String, Float> values, float above,
                            float below);

    public static MatchMethod findOrDefault(String val, MatchMethod def) {
      try {
        return MatchMethod.valueOf(val.toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException e) {
        DietMod.LOGGER.error("No such match method " + val);
      }
      return def;
    }

    private static boolean inRange(float value, float above, float below) {
      return value >= above && value <= below;
    }
  }
}
