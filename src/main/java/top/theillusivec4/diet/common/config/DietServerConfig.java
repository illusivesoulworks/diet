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

package top.theillusivec4.diet.common.config;

import java.util.List;
import java.util.Map;
import net.minecraft.world.item.Item;
import top.theillusivec4.diet.common.config.data.EffectConfig;
import top.theillusivec4.diet.common.config.data.GroupConfig;

public class DietServerConfig {

  public static List<GroupConfig> groups;
  public static List<EffectConfig> effects;

  public static float deathPenaltyMin;
  public static float deathPenaltyLoss;
  public static DeathPenaltyMethod deathPenaltyMethod;
  public static float decayPenaltyPerGroup;
  public static float gainPenaltyPerGroup;
  public static boolean hideTooltipsUntilEaten;
  public static Map<Item, Float> foodOverrides;
  public static boolean generateGroupsForEmptyItems;

  public enum DeathPenaltyMethod {
    AMOUNT,
    PERCENT,
    RESET
  }
}
