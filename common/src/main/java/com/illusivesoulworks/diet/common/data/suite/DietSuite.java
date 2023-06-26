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

package com.illusivesoulworks.diet.common.data.suite;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.illusivesoulworks.diet.api.type.IDietEffect;
import com.illusivesoulworks.diet.api.type.IDietGroup;
import com.illusivesoulworks.diet.api.type.IDietSuite;
import com.illusivesoulworks.diet.common.data.group.DietGroup;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.nbt.CompoundTag;

public final class DietSuite implements IDietSuite {

  private final String name;
  private final Set<IDietGroup> groups;
  private final List<IDietEffect> effects;

  private DietSuite(String name, Set<IDietGroup> groups, List<IDietEffect> effects) {
    this.name = name;
    TreeSet<IDietGroup> sorted = new TreeSet<>(
        Comparator.comparing(IDietGroup::getOrder).thenComparing(IDietGroup::getName));
    sorted.addAll(groups);
    this.groups = ImmutableSet.copyOf(sorted);
    this.effects = ImmutableList.copyOf(effects);
  }

  public static IDietSuite load(CompoundTag tag) {
    Set<IDietGroup> set = new HashSet<>();
    CompoundTag groups = (CompoundTag) tag.get("Groups");

    if (groups != null) {

      for (String key : groups.getAllKeys()) {
        set.add(DietGroup.load((CompoundTag) Objects.requireNonNull(groups.get(key))));
      }
    }
    return new DietSuite(tag.getString("Name"), set, new ArrayList<>());
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Set<IDietGroup> getGroups() {
    return this.groups;
  }

  @Override
  public List<IDietEffect> getEffects() {
    return this.effects;
  }

  @Override
  public CompoundTag save() {
    CompoundTag tag = new CompoundTag();
    tag.putString("Name", this.name);
    CompoundTag groups = new CompoundTag();

    for (IDietGroup group : this.groups) {
      groups.put(group.getName(), group.save());
    }
    tag.put("Groups", groups);
    return tag;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DietSuite dietSuite = (DietSuite) o;
    return Objects.equals(name, dietSuite.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  public static class Builder {

    private final String name;
    private final Set<IDietGroup> groups;
    private final List<IDietEffect> effects;

    public Builder(String name) {
      this.name = name;
      this.groups = new TreeSet<>(
          Comparator.comparingInt(IDietGroup::getOrder).thenComparing(IDietGroup::getName));
      this.effects = new ArrayList<>();
    }

    public Builder group(IDietGroup group) {
      this.groups.add(group);
      return this;
    }

    public Builder groups(Set<IDietGroup> groups) {
      this.groups.addAll(groups);
      return this;
    }

    public Builder effect(IDietEffect effect) {
      this.effects.add(effect);
      return this;
    }

    public Builder effects(List<IDietEffect> effects) {
      this.effects.addAll(effects);
      return this;
    }

    public Builder clear() {
      this.groups.clear();
      this.effects.clear();
      return this;
    }

    public IDietSuite build() {
      return new DietSuite(this.name, this.groups, this.effects);
    }
  }
}
