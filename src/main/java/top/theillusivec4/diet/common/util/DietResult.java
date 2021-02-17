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

package top.theillusivec4.diet.common.util;

import java.util.HashMap;
import java.util.Map;
import top.theillusivec4.diet.common.group.DietGroup;

public class DietResult {

  public static final DietResult EMPTY = new DietResult();

  private final Map<DietGroup, Float> groups;

  private DietResult() {
    this(new HashMap<>());
  }

  public DietResult(Map<DietGroup, Float> groups) {
    this.groups = groups;
  }

  public Map<DietGroup, Float> get() {
    return groups;
  }

  public float get(DietGroup group) {
    return groups.getOrDefault(group, 0.0f);
  }
}
