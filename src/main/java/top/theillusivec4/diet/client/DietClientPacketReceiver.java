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

package top.theillusivec4.diet.client;

import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import top.theillusivec4.diet.api.DietCapability;
import top.theillusivec4.diet.common.network.server.SPacketActivate;
import top.theillusivec4.diet.common.network.server.SPacketDiet;
import top.theillusivec4.diet.common.network.server.SPacketEaten;

public class DietClientPacketReceiver {

  public static void handleActivate(SPacketActivate msg) {
    PlayerEntity player = Minecraft.getInstance().player;

    if (player != null) {
      DietCapability.get(player).ifPresent(diet -> diet.setActive(msg.flag));
    }
  }

  public static void handleDiet(SPacketDiet msg) {
    PlayerEntity player = Minecraft.getInstance().player;

    if (player != null) {
      DietCapability.get(player).ifPresent(diet -> {
        for (Map.Entry<String, Float> entry : msg.groups.entrySet()) {
          diet.setValue(entry.getKey(), entry.getValue());
        }
      });
    }
  }

  public static void handleEaten(SPacketEaten msg) {
    PlayerEntity player = Minecraft.getInstance().player;

    if (player != null) {
      DietCapability.get(player).ifPresent(diet -> {

        for (Item item : msg.items) {
          diet.addEaten(item);
        }
      });
    }
  }
}
