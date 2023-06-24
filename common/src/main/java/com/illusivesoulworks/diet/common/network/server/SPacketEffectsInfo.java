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

package com.illusivesoulworks.diet.common.network.server;

import com.illusivesoulworks.diet.client.screen.DietScreen;
import com.illusivesoulworks.diet.common.impl.effect.DietEffectsInfo;
import java.util.Objects;
import net.minecraft.network.FriendlyByteBuf;

public class SPacketEffectsInfo {

  private final DietEffectsInfo info;

  public SPacketEffectsInfo(DietEffectsInfo infoIn) {
    info = infoIn;
  }

  public static void encode(SPacketEffectsInfo msg, FriendlyByteBuf buf) {
    buf.writeNbt(msg.info.write());
  }

  public static SPacketEffectsInfo decode(FriendlyByteBuf buf) {
    return new SPacketEffectsInfo(DietEffectsInfo.read(
        Objects.requireNonNull(buf.readNbt())));
  }

  public static void handle(SPacketEffectsInfo msg) {
    DietScreen.tooltip = msg.info;
  }
}
