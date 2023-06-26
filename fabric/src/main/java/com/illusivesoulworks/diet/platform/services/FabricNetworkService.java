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

package com.illusivesoulworks.diet.platform.services;

import com.illusivesoulworks.diet.common.DietFabricNetwork;
import com.illusivesoulworks.diet.common.data.effect.DietEffectsInfo;
import com.illusivesoulworks.diet.common.network.server.SPacketActivate;
import com.illusivesoulworks.diet.common.network.server.SPacketDiet;
import com.illusivesoulworks.diet.common.network.server.SPacketEaten;
import com.illusivesoulworks.diet.common.network.server.SPacketEffectsInfo;
import com.illusivesoulworks.diet.common.network.server.SPacketGroups;
import com.illusivesoulworks.diet.common.network.server.SPacketSuites;
import java.util.Map;
import java.util.Set;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

public class FabricNetworkService implements INetworkService {

  @Override
  public void sendEffectsInfoS2C(ServerPlayer player, DietEffectsInfo info) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketEffectsInfo.encode(new SPacketEffectsInfo(info), buf);
    ServerPlayNetworking.send(player, DietFabricNetwork.EFFECTS_INFO, buf);
  }

  @Override
  public void sendDietS2C(ServerPlayer player, String suite, Map<String, Float> groups) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketDiet.encode(new SPacketDiet(suite, groups), buf);
    ServerPlayNetworking.send(player, DietFabricNetwork.DIET, buf);
  }

  @Override
  public void sendActivationS2C(ServerPlayer player, boolean flag) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketActivate.encode(new SPacketActivate(flag), buf);
    ServerPlayNetworking.send(player, DietFabricNetwork.ACTIVATION, buf);
  }

  @Override
  public void sendEatenS2C(ServerPlayer player, Set<Item> items) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketEaten.encode(new SPacketEaten(items), buf);
    ServerPlayNetworking.send(player, DietFabricNetwork.EATEN, buf);
  }

  @Override
  public void sendDietGroupsS2C(ServerPlayer player, CompoundTag groups,
                                Map<Item, Set<String>> generated) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketGroups.encode(new SPacketGroups(groups, generated), buf);
    ServerPlayNetworking.send(player, DietFabricNetwork.GROUPS, buf);
  }

  @Override
  public void sendDietSuitesS2C(ServerPlayer player, CompoundTag suites) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketSuites.encode(new SPacketSuites(suites), buf);
    ServerPlayNetworking.send(player, DietFabricNetwork.SUITES, buf);
  }
}
