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

package com.illusivesoulworks.diet.common.network.server;

import com.illusivesoulworks.diet.client.DietClientPacketReceiver;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.network.FriendlyByteBuf;

public record SPacketDiet(String suite, Map<String, Float> groups) {

  public static void encode(SPacketDiet msg, FriendlyByteBuf buf) {
    buf.writeUtf(msg.suite);

    for (Map.Entry<String, Float> entry : msg.groups.entrySet()) {
      buf.writeUtf(entry.getKey());
      buf.writeFloat(entry.getValue());
    }
  }

  public static SPacketDiet decode(FriendlyByteBuf buf) {
    Map<String, Float> groups = new TreeMap<>();
    String suite = buf.readUtf();

    while (buf.isReadable()) {
      String name = buf.readUtf();
      float value = buf.readFloat();
      groups.put(name, value);
    }
    return new SPacketDiet(suite, groups);
  }

  public static void handle(SPacketDiet msg) {
    DietClientPacketReceiver.handleDiet(msg);
  }
}
