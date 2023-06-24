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

package com.illusivesoulworks.diet.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class DietKeys {

  public static final KeyMapping OPEN_GUI =
      new KeyMapping("key.diet.open.desc", InputConstants.UNKNOWN.getValue(),
          "key.diet.category");

  public static KeyMapping get() {
    return OPEN_GUI;
  }
}
