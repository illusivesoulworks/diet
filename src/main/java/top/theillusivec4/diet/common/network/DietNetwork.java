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

package top.theillusivec4.diet.common.network;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.common.effect.DietEffectsInfo;
import top.theillusivec4.diet.common.network.server.SPacketActivate;
import top.theillusivec4.diet.common.network.server.SPacketDiet;
import top.theillusivec4.diet.common.network.server.SPacketEaten;
import top.theillusivec4.diet.common.network.server.SPacketEffectsInfo;
import top.theillusivec4.diet.common.network.server.SPacketGeneratedValues;

public class DietNetwork {

  private static final String PTC_VERSION = "1";

  public static SimpleChannel INSTANCE;

  private static int id = 0;

  public static void setup() {
    INSTANCE = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(DietMod.MOD_ID, "main"))
        .networkProtocolVersion(() -> PTC_VERSION).clientAcceptedVersions(PTC_VERSION::equals)
        .serverAcceptedVersions(PTC_VERSION::equals).simpleChannel();

    // Server Packets
    register(SPacketDiet.class, SPacketDiet::encode, SPacketDiet::decode, SPacketDiet::handle);
    register(SPacketEffectsInfo.class, SPacketEffectsInfo::encode, SPacketEffectsInfo::decode,
        SPacketEffectsInfo::handle);
    register(SPacketActivate.class, SPacketActivate::encode, SPacketActivate::decode,
        SPacketActivate::handle);
    register(SPacketGeneratedValues.class, SPacketGeneratedValues::encode,
        SPacketGeneratedValues::decode, SPacketGeneratedValues::handle);
    register(SPacketEaten.class, SPacketEaten::encode, SPacketEaten::decode, SPacketEaten::handle);
  }

  public static void sendEffectsInfoS2C(ServerPlayer player, DietEffectsInfo info) {
    INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SPacketEffectsInfo(info));
  }

  public static void sendDietS2C(ServerPlayer player, Map<String, Float> groups) {
    INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SPacketDiet(groups));
  }

  public static void sendActivationS2C(ServerPlayer player, boolean flag) {
    INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SPacketActivate(flag));
  }

  public static void sendGeneratedValuesS2C(ServerPlayer player,
                                            Map<Item, Set<IDietGroup>> generated) {
    INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
        new SPacketGeneratedValues(generated));
  }

  public static void sendEatenS2C(ServerPlayer player, Set<Item> items) {
    INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SPacketEaten(items));
  }

  private static <M> void register(Class<M> messageType, BiConsumer<M, FriendlyByteBuf> encoder,
                                   Function<FriendlyByteBuf, M> decoder,
                                   BiConsumer<M, Supplier<NetworkEvent.Context>> messageConsumer) {
    INSTANCE.registerMessage(id++, messageType, encoder, decoder, messageConsumer);
  }
}
