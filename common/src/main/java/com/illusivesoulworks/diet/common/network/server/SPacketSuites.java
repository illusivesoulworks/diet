package com.illusivesoulworks.diet.common.network.server;

import com.illusivesoulworks.diet.client.DietClientPacketReceiver;
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
    DietClientPacketReceiver.handleSuites(msg.suites());
  }
}
