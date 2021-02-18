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

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.diet.client.DietClientPacketReceiver;

public class SPacketActivate {

  public final boolean flag;

  public SPacketActivate(boolean flag) {
    this.flag = flag;
  }

  public static void encode(SPacketActivate msg, PacketBuffer buf) {
    buf.writeBoolean(msg.flag);
  }

  public static SPacketActivate decode(PacketBuffer buf) {
    return new SPacketActivate(buf.readBoolean());
  }

  public static void handle(SPacketActivate msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> DietClientPacketReceiver.handleActivate(msg));
    ctx.get().setPacketHandled(true);
  }
}
