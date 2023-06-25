package com.illusivesoulworks.diet.platform.services;

import com.illusivesoulworks.diet.api.type.IDietGroup;
import com.illusivesoulworks.diet.common.impl.effect.DietEffectsInfo;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

public interface INetworkService {

  void sendEffectsInfoS2C(ServerPlayer player, DietEffectsInfo info);

  void sendDietS2C(ServerPlayer player, String suite, Map<String, Float> groups);

  void sendActivationS2C(ServerPlayer player, boolean flag);

  void sendEatenS2C(ServerPlayer player, Set<Item> items);

  void sendDietGroupsS2C(ServerPlayer player, CompoundTag groups,
                         Map<Item, Set<String>> generated);

  void sendDietSuitesS2C(ServerPlayer player, CompoundTag suites);
}
