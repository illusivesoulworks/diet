package com.illusivesoulworks.diet.api.type;

import com.illusivesoulworks.diet.api.util.DietColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IDietGroup {

  String getName();

  Item getIcon();

  DietColor getColor();

  float getDefaultValue();

  int getOrder();

  double getGainMultiplier();

  double getDecayMultiplier();

  boolean isBeneficial();

  TagKey<Item> getTag();

  boolean contains(ItemStack stack);

  CompoundTag save();
}
