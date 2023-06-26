package com.illusivesoulworks.diet.platform.services;

import com.illusivesoulworks.diet.common.DietQuiltNetwork;
import com.illusivesoulworks.diet.common.data.effect.DietEffectsInfo;
import com.illusivesoulworks.diet.common.network.server.SPacketActivate;
import com.illusivesoulworks.diet.common.network.server.SPacketDiet;
import com.illusivesoulworks.diet.common.network.server.SPacketEaten;
import com.illusivesoulworks.diet.common.network.server.SPacketEffectsInfo;
import com.illusivesoulworks.diet.common.network.server.SPacketGroups;
import com.illusivesoulworks.diet.common.network.server.SPacketSuites;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class QuiltNetworkService implements INetworkService {

  @Override
  public void sendEffectsInfoS2C(ServerPlayer player, DietEffectsInfo info) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketEffectsInfo.encode(new SPacketEffectsInfo(info), buf);
    ServerPlayNetworking.send(player, DietQuiltNetwork.EFFECTS_INFO, buf);
  }

  @Override
  public void sendDietS2C(ServerPlayer player, String suite, Map<String, Float> groups) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketDiet.encode(new SPacketDiet(suite, groups), buf);
    ServerPlayNetworking.send(player, DietQuiltNetwork.DIET, buf);
  }

  @Override
  public void sendActivationS2C(ServerPlayer player, boolean flag) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketActivate.encode(new SPacketActivate(flag), buf);
    ServerPlayNetworking.send(player, DietQuiltNetwork.ACTIVATION, buf);
  }

  @Override
  public void sendEatenS2C(ServerPlayer player, Set<Item> items) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketEaten.encode(new SPacketEaten(items), buf);
    ServerPlayNetworking.send(player, DietQuiltNetwork.EATEN, buf);
  }

  @Override
  public void sendDietGroupsS2C(ServerPlayer player, CompoundTag groups,
                                Map<Item, Set<String>> generated) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketGroups.encode(new SPacketGroups(groups, generated), buf);
    ServerPlayNetworking.send(player, DietQuiltNetwork.GROUPS, buf);
  }

  @Override
  public void sendDietSuitesS2C(ServerPlayer player, CompoundTag suites) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketSuites.encode(new SPacketSuites(suites), buf);
    ServerPlayNetworking.send(player, DietQuiltNetwork.SUITES, buf);
  }
}
