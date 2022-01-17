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

import java.awt.Color;
import java.util.Objects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.IDietGroup;

public final class DietGroup implements IDietGroup {

  private final String name;
  private final Item icon;
  private final Color color;
  private final float defaultValue;
  private final int order;
  private final double gainMultiplier;
  private final double decayMultiplier;
  private final boolean beneficial;
  private final Tags.IOptionalNamedTag<Item> tag;

  public DietGroup(String name, Item icon, Color color, float defaultValue, int order,
                   double gainMultiplier, double decayMultiplier, boolean beneficial) {
    this.name = name;
    this.icon = icon;
    this.color = color;
    this.defaultValue = defaultValue;
    this.order = order;
    this.gainMultiplier = gainMultiplier;
    this.decayMultiplier = decayMultiplier;
    this.beneficial = beneficial;
    this.tag = ItemTags.createOptional(new ResourceLocation(DietMod.id(name)));
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Item getIcon() {
    return icon;
  }

  @Override
  public Color getColor() {
    return color;
  }

  @Override
  public float getDefaultValue() {
    return defaultValue;
  }

  @Override
  public int getOrder() {
    return order;
  }

  @Override
  public double getGainMultiplier() {
    return gainMultiplier;
  }

  @Override
  public double getDecayMultiplier() {
    return decayMultiplier;
  }

  @Override
  public boolean isBeneficial() {
    return beneficial;
  }

  @Override
  public Tags.IOptionalNamedTag<Item> getTag() {
    return tag;
  }

  @Override
  public boolean contains(ItemStack stack) {
    return tag.contains(stack.getItem());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DietGroup dietGroup = (DietGroup) o;
    return name.equals(dietGroup.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
