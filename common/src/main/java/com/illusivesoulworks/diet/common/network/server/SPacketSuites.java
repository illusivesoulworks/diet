package com.illusivesoulworks.diet.common.network.server;

import com.illusivesoulworks.diet.api.type.IDietTracker;
import com.illusivesoulworks.diet.common.impl.group.DietGroups;
import com.illusivesoulworks.diet.common.impl.suite.DietSuites;
import com.illusivesoulworks.diet.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public record SPacketSuites(CompoundTag suites) {

  public static void encode(SPacketSuites msg, FriendlyByteBuf buf) {
    buf.writeNbt(msg.suites());
  }

  public static SPacketSuites decode(FriendlyByteBuf buf) {
    return new SPacketSuites(buf.readNbt());
  }

  public static void handle(SPacketSuites msg) {
    DietSuites.CLIENT.load(msg.suites());
    Services.CAPABILITY.get(Minecraft.getInstance().player).ifPresent(IDietTracker::initSuite);
  }
}
