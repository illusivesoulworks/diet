/*
 * Copyright (C) 2021-2023 Illusive Soulworks
 *
 * Diet is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Diet is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Diet.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.diet.common.data.effect;

import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.api.type.IDietAttribute;
import com.illusivesoulworks.diet.api.type.IDietCondition;
import com.illusivesoulworks.diet.api.type.IDietEffect;
import com.illusivesoulworks.diet.api.type.IDietStatusEffect;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public class DietEffect implements IDietEffect {

  public final List<IDietAttribute> attributes;
  public final List<IDietStatusEffect> statusEffects;
  public final List<IDietCondition> conditions;
  public final UUID uuid;

  public DietEffect(UUID uuid, List<IDietAttribute> attributes,
                    List<IDietStatusEffect> statusEffects, List<IDietCondition> conditions) {
    this.attributes = attributes;
    this.statusEffects = statusEffects;
    this.conditions = conditions;
    this.uuid = uuid;
  }

  @Override
  public List<IDietCondition> getConditions() {
    return this.conditions;
  }

  @Override
  public List<IDietAttribute> getAttributes() {
    return this.attributes;
  }

  @Override
  public List<IDietStatusEffect> getStatusEffects() {
    return this.statusEffects;
  }

  @Override
  public UUID getUuid() {
    return this.uuid;
  }

  public static class DietAttribute implements IDietAttribute {

    public final Attribute attribute;
    public final AttributeModifier.Operation operation;
    public final double amount;
    public final double increment;

    public DietAttribute(Attribute attribute, AttributeModifier.Operation operation,
                         double amount) {
      this(attribute, operation, amount, amount);
    }

    public DietAttribute(Attribute attribute, AttributeModifier.Operation operation,
                         double amount, double increment) {
      this.attribute = attribute;
      this.operation = operation;
      this.amount = amount;
      this.increment = increment;
    }

    @Override
    public Attribute getAttribute() {
      return this.attribute;
    }

    @Override
    public AttributeModifier.Operation getOperation() {
      return this.operation;
    }

    @Override
    public double getBaseAmount() {
      return this.amount;
    }

    @Override
    public double getIncrement() {
      return this.increment;
    }
  }

  public static class DietStatusEffect implements IDietStatusEffect {

    public final MobEffect effect;
    public final int power;
    public final int increment;

    public DietStatusEffect(MobEffect effect, int power) {
      this(effect, power, power);
    }

    public DietStatusEffect(MobEffect effect, int power, int increment) {
      this.effect = effect;
      this.power = power;
      this.increment = increment;
    }

    @Override
    public MobEffect getEffect() {
      return this.effect;
    }

    @Override
    public int getBasePower() {
      return this.power;
    }

    @Override
    public int getIncrement() {
      return this.increment;
    }
  }

  public static class DietCondition implements IDietCondition {

    public final Set<String> groups;
    public final MatchMethod match;
    public final double above;
    public final double below;

    public DietCondition(Set<String> groups, MatchMethod match, double above, double below) {
      this.groups = groups;
      this.match = match;
      this.above = above;
      this.below = below;
    }

    @Override
    public MatchMethod getMatchMethod() {
      return this.match;
    }

    public int getMatches(Player player, Map<String, Float> values) {
      return this.match.getMatches(this.groups, values, (float) this.above, (float) this.below);
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
        DietConstants.LOG.error("No such match method " + val);
      }
      return def;
    }

    private static boolean inRange(float value, float above, float below) {
      return value >= above && value <= below;
    }
  }
}
