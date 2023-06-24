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

package com.illusivesoulworks.diet.common.network;

import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.common.network.server.SPacketActivate;
import com.illusivesoulworks.diet.common.network.server.SPacketDiet;
import com.illusivesoulworks.diet.common.network.server.SPacketEaten;
import com.illusivesoulworks.diet.common.network.server.SPacketEffectsInfo;
import com.illusivesoulworks.diet.common.network.server.SPacketGeneratedValues;
import com.illusivesoulworks.diet.common.network.server.SPacketGroups;
import com.illusivesoulworks.diet.common.network.server.SPacketSuites;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class DietForgeNetwork {

  private static final String PTC_VERSION = "1";

  public static SimpleChannel instance;

  private static int id = 0;

  public static void setup() {
    instance =
        NetworkRegistry.ChannelBuilder.named(new ResourceLocation(DietConstants.MOD_ID, "main"))
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
    register(SPacketGroups.class, SPacketGroups::encode, SPacketGroups::decode,
        SPacketGroups::handle);
    register(SPacketSuites.class, SPacketSuites::encode, SPacketSuites::decode,
        SPacketSuites::handle);
  }

  public static <M> void register(Class<M> clazz, BiConsumer<M, FriendlyByteBuf> encoder,
                                  Function<FriendlyByteBuf, M> decoder, Consumer<M> handler) {
    instance.registerMessage(id++, clazz, encoder, decoder, (message, contextSupplier) -> {
      NetworkEvent.Context context = contextSupplier.get();
      context.enqueueWork(
          () -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handler.accept(message)));
      context.setPacketHandled(true);
    });
  }
}
