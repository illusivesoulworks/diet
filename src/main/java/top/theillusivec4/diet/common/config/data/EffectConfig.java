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

package top.theillusivec4.diet.common.config.data;

import java.util.List;

public class EffectConfig {

  public List<AttributeConfig> attributes;
  public List<StatusEffectConfig> status_effects;
  public List<ConditionConfig> conditions;

  public static class AttributeConfig {
    public String name;
    public String operation;
    public Double amount;
  }

  public static class StatusEffectConfig {
    public String name;
    public Integer power;
  }

  public static class ConditionConfig {
    public List<String> groups;
    public String match;
    public Double above;
    public Double below;
  }
}
