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

import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.diet.client.DietClientEventsListener;
import top.theillusivec4.diet.client.DietScreen;
import top.theillusivec4.diet.common.effect.DietEffectsInfo;

public class SPacketEffectsInfo {

  private final DietEffectsInfo info;

  public SPacketEffectsInfo(DietEffectsInfo infoIn) {
    info = infoIn;
  }

  public static void encode(SPacketEffectsInfo msg, PacketBuffer buf) {
    buf.writeCompoundTag(msg.info.write());
  }

  public static SPacketEffectsInfo decode(PacketBuffer buf) {
    return new SPacketEffectsInfo(DietEffectsInfo.read(
        Objects.requireNonNull(buf.readCompoundTag())));
  }

  public static void handle(SPacketEffectsInfo msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientPlayerEntity player = Minecraft.getInstance().player;

      if (player != null) {
        DietScreen.tooltip = msg.info;
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
