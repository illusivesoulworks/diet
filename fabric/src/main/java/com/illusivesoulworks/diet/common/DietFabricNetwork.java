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

package com.illusivesoulworks.diet.common;

import com.illusivesoulworks.diet.DietCommonMod;
import com.illusivesoulworks.diet.common.network.server.SPacketActivate;
import com.illusivesoulworks.diet.common.network.server.SPacketDiet;
import com.illusivesoulworks.diet.common.network.server.SPacketEaten;
import com.illusivesoulworks.diet.common.network.server.SPacketEffectsInfo;
import com.illusivesoulworks.diet.common.network.server.SPacketGroups;
import com.illusivesoulworks.diet.common.network.server.SPacketSuites;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class DietFabricNetwork {

  public static final ResourceLocation EFFECTS_INFO = DietCommonMod.resource("effects_info");
  public static final ResourceLocation DIET = DietCommonMod.resource("diet");
  public static final ResourceLocation ACTIVATION = DietCommonMod.resource("activation");
  public static final ResourceLocation EATEN = DietCommonMod.resource("eaten");
  public static final ResourceLocation GROUPS = DietCommonMod.resource("groups");
  public static final ResourceLocation SUITES = DietCommonMod.resource("suites");

  public static void setup() {
    registerClientReceiver(EFFECTS_INFO, SPacketEffectsInfo::decode, SPacketEffectsInfo::handle);
    registerClientReceiver(DIET, SPacketDiet::decode, SPacketDiet::handle);
    registerClientReceiver(ACTIVATION, SPacketActivate::decode, SPacketActivate::handle);
    registerClientReceiver(EATEN, SPacketEaten::decode, SPacketEaten::handle);
    registerClientReceiver(GROUPS, SPacketGroups::decode, SPacketGroups::handle);
    registerClientReceiver(SUITES, SPacketSuites::decode, SPacketSuites::handle);
  }

  private static <M> void registerClientReceiver(ResourceLocation resourceLocation,
                                                 Function<FriendlyByteBuf, M> decoder,
                                                 Consumer<M> handler) {
    ClientPlayNetworking.registerGlobalReceiver(resourceLocation,
        (client, listener, buf, responseSender) -> {
          M packet = decoder.apply(buf);
          client.execute(() -> handler.accept(packet));
        });
  }
}
