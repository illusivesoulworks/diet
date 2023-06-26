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

package com.illusivesoulworks.diet.common.component;

import com.illusivesoulworks.diet.common.capability.PlayerDietTracker;
import dev.onyxstudios.cca.api.v3.component.Component;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class DietComponent extends PlayerDietTracker implements Component {

  public DietComponent(Player player) {
    super(player);
  }

  @Override
  public void readFromNbt(@Nonnull CompoundTag tag) {
    this.load(tag);
  }

  @Override
  public void writeToNbt(@Nonnull CompoundTag tag) {
    this.save(tag);
  }
}
