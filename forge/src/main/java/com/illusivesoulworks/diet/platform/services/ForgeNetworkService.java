package com.illusivesoulworks.diet.platform.services;

import com.illusivesoulworks.diet.api.type.IDietGroup;
import com.illusivesoulworks.diet.common.impl.effect.DietEffectsInfo;
import com.illusivesoulworks.diet.common.network.DietForgeNetwork;
import com.illusivesoulworks.diet.common.network.server.SPacketActivate;
import com.illusivesoulworks.diet.common.network.server.SPacketDiet;
import com.illusivesoulworks.diet.common.network.server.SPacketEaten;
import com.illusivesoulworks.diet.common.network.server.SPacketEffectsInfo;
import com.illusivesoulworks.diet.common.network.server.SPacketGroups;
import com.illusivesoulworks.diet.common.network.server.SPacketSuites;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.PacketDistributor;

public class ForgeNetworkService implements INetworkService {

  @Override
  public void sendEffectsInfoS2C(ServerPlayer player, DietEffectsInfo info) {
    DietForgeNetwork.instance.send(PacketDistributor.PLAYER.with(() -> player),
        new SPacketEffectsInfo(info));
  }

  @Override
  public void sendDietS2C(ServerPlayer player, String suite, Map<String, Float> groups) {
    DietForgeNetwork.instance.send(PacketDistributor.PLAYER.with(() -> player),
        new SPacketDiet(suite, groups));
  }

  @Override
  public void sendActivationS2C(ServerPlayer player, boolean flag) {
    DietForgeNetwork.instance.send(PacketDistributor.PLAYER.with(() -> player),
        new SPacketActivate(flag));
  }

  @Override
  public void sendEatenS2C(ServerPlayer player, Set<Item> items) {
    DietForgeNetwork.instance.send(PacketDistributor.PLAYER.with(() -> player),
        new SPacketEaten(items));
  }

  @Override
  public void sendDietGroupsS2C(ServerPlayer player, CompoundTag groups,
                                Map<Item, Set<String>> generated) {
    DietForgeNetwork.instance.send(PacketDistributor.PLAYER.with(() -> player),
        new SPacketGroups(groups, generated));
  }

  @Override
  public void sendDietSuitesS2C(ServerPlayer player, CompoundTag suites) {
    DietForgeNetwork.instance.send(PacketDistributor.PLAYER.with(() -> player),
        new SPacketSuites(suites));
  }
}
