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

package com.illusivesoulworks.diet.common.util;

import java.util.HashMap;
import java.util.Map;
import com.illusivesoulworks.diet.api.type.IDietGroup;
import com.illusivesoulworks.diet.api.type.IDietResult;

public class DietResult implements IDietResult {

  public static final IDietResult EMPTY = new DietResult();

  private final Map<IDietGroup, Float> groups;

  private DietResult() {
    this(new HashMap<>());
  }

  public DietResult(Map<IDietGroup, Float> groups) {
    this.groups = groups;
  }

  @Override
  public Map<IDietGroup, Float> get() {
    return groups;
  }

  public float get(IDietGroup group) {
    return groups.getOrDefault(group, 0.0f);
  }
}
