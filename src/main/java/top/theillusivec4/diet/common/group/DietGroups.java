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

package top.theillusivec4.diet.common.group;

import com.google.common.collect.ImmutableSet;
import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.common.config.data.GroupConfig;

public class DietGroups {

  private static final Set<IDietGroup> groups = new TreeSet<>(
      Comparator.comparingInt(IDietGroup::getOrder).thenComparing(IDietGroup::getName));

  public static Set<IDietGroup> get() {
    return ImmutableSet.copyOf(groups);
  }

  public static void build(List<GroupConfig> configs) {
    groups.clear();

    if (configs != null) {

      for (GroupConfig config : configs) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(config.icon));

        if (item != null) {
          String name = config.name;
          float defaultValue = (float) (config.default_value != null ? config.default_value : 0);
          int order = config.order != null ? config.order : 0;
          boolean beneficial = config.beneficial != null ? config.beneficial : true;
          IDietGroup group =
              new DietGroup(name, item, Color.decode(config.color), defaultValue, order,
                  config.gain_multiplier, config.decay_multiplier, beneficial);

          if (!groups.add(group)) {
            DietMod.LOGGER.error("Found duplicate id in diet groups config: " + name);
          }
        } else {
          DietMod.LOGGER.error("Found unknown item in diet groups config: " + config.icon);
        }
      }
    }
  }
}
