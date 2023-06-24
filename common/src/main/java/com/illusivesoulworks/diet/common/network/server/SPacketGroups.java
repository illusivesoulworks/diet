package com.illusivesoulworks.diet.common.network.server;

import com.illusivesoulworks.diet.common.impl.group.DietGroups;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public record SPacketGroups(CompoundTag groups) {

  public static void encode(SPacketGroups msg, FriendlyByteBuf buf) {
    buf.writeNbt(msg.groups());
  }

  public static SPacketGroups decode(FriendlyByteBuf buf) {
    return new SPacketGroups(buf.readNbt());
  }

  public static void handle(SPacketGroups msg) {
    DietGroups.CLIENT.load(msg.groups());
  }
}
