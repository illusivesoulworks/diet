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

package com.illusivesoulworks.diet.common.data.group;

import com.illusivesoulworks.diet.DietCommonMod;
import com.illusivesoulworks.diet.api.type.IDietGroup;
import com.illusivesoulworks.diet.api.util.DietColor;
import com.illusivesoulworks.diet.platform.Services;
import java.util.Objects;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class DietGroup implements IDietGroup {

  private final String name;
  private final Item icon;
  private final DietColor color;
  private final float defaultValue;
  private final int order;
  private final double gainMultiplier;
  private final double decayMultiplier;
  private final boolean beneficial;
  private final TagKey<Item> tag;

  private DietGroup(Builder builder) {
    this.name = builder.name;
    this.icon = builder.icon;
    this.color = builder.color;
    this.defaultValue = builder.defaultValue;
    this.order = builder.order;
    this.gainMultiplier = builder.gainMultiplier;
    this.decayMultiplier = builder.decayMultiplier;
    this.beneficial = builder.beneficial;
    this.tag = TagKey.create(Registry.ITEM_REGISTRY, DietCommonMod.resource(this.name));
  }

  public static IDietGroup load(CompoundTag tag) {
    String name = tag.getString("Name");
    String icon = tag.getString("Icon");
    Item item = Services.REGISTRY.getItem(new ResourceLocation(icon)).orElse(null);
    int order = tag.getInt("Order");
    boolean beneficial = tag.getBoolean("Beneficial");
    int color = tag.getInt("Color");
    int r = ((color >> 16) & 0xff);
    int g = ((color >> 8) & 0xff);
    int b = ((color) & 0xff);
    Builder builder = new Builder(name);
    builder.icon(item);
    builder.order(order);
    builder.beneficial(beneficial);
    builder.color(new DietColor(r, g, b));
    builder.gainMultiplier((float) tag.getDouble("Gain"));
    builder.decayMultiplier((float) tag.getDouble("Decay"));
    return builder.build();
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
  public DietColor getColor() {
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
  public TagKey<Item> getTag() {
    return tag;
  }

  @Override
  public boolean contains(ItemStack stack) {
    return stack.is(this.tag);
  }

  @Override
  public CompoundTag save() {
    CompoundTag tag = new CompoundTag();
    tag.putString("Name", this.name);
    tag.putString("Icon", Services.REGISTRY.getItemKey(this.icon).toString());
    tag.putInt("Order", this.order);
    tag.putBoolean("Beneficial", this.beneficial);
    tag.putInt("Color", this.color.getRGB());
    tag.putDouble("Gain", this.gainMultiplier);
    tag.putDouble("Decay", this.decayMultiplier);
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
    DietGroup dietGroup = (DietGroup) o;
    return name.equals(dietGroup.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  public static class Builder {

    private final String name;
    private Item icon = Items.AIR;
    private DietColor color = new DietColor(255, 255, 255);
    private float defaultValue = 0.0f;
    private int order = 1;
    private float gainMultiplier = 1.0f;
    private float decayMultiplier = 1.0f;
    private boolean beneficial = true;

    public Builder(String name) {
      this.name = name;
    }

    public Builder icon(Item icon) {
      this.icon = icon;
      return this;
    }

    public Builder color(DietColor color) {
      this.color = color;
      return this;
    }

    public Builder defaultValue(float defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

    public Builder order(int order) {
      this.order = order;
      return this;
    }

    public Builder gainMultiplier(float gainMultiplier) {
      this.gainMultiplier = gainMultiplier;
      return this;
    }

    public Builder decayMultiplier(float decayMultiplier) {
      this.decayMultiplier = decayMultiplier;
      return this;
    }

    public Builder beneficial(boolean beneficial) {
      this.beneficial = beneficial;
      return this;
    }

    public IDietGroup build() {
      return new DietGroup(this);
    }
  }
}
