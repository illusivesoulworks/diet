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

package top.theillusivec4.diet.common.network.server;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.diet.api.DietCapability;

public class SPacketDiet {

  private final Map<String, Float> groups;

  public SPacketDiet(Map<String, Float> groups) {
    this.groups = groups;
  }

  public static void encode(SPacketDiet msg, PacketBuffer buf) {

    for (Map.Entry<String, Float> entry : msg.groups.entrySet()) {
      buf.writeString(entry.getKey());
      buf.writeFloat(entry.getValue());
    }
  }

  public static SPacketDiet decode(PacketBuffer buf) {
    Map<String, Float> groups = new TreeMap<>();

    while (buf.isReadable()) {
      String name = buf.readString();
      float value = buf.readFloat();
      groups.put(name, value);
    }
    return new SPacketDiet(groups);
  }

  public static void handle(SPacketDiet msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientPlayerEntity player = Minecraft.getInstance().player;

      if (player != null) {
        DietCapability.get(player).ifPresent(diet -> {
          for (Map.Entry<String, Float> entry : msg.groups.entrySet()) {
            diet.setValue(entry.getKey(), entry.getValue());
          }
        });
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
