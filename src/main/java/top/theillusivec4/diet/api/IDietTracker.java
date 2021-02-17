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

package top.theillusivec4.diet.api;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public interface IDietTracker {

  void tick();

  void consume(ItemStack stack);

  void consume(BlockPos pos, Hand hand, Direction direction);

  float getValue(String group);

  void setValue(String group, float amount);

  Map<String, Float> getValues();

  void setValues(Map<String, Float> groups);

  Map<Attribute, Set<UUID>> getModifiers();

  void setModifiers(Map<Attribute, Set<UUID>> modifiers);

  boolean isActive();

  void setActive(boolean active);

  PlayerEntity getPlayer();

  void sync();
}
