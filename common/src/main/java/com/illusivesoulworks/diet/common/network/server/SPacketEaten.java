package com.illusivesoulworks.diet.common.network.server;

import com.illusivesoulworks.diet.client.DietClientPacketReceiver;
import com.illusivesoulworks.diet.platform.Services;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record SPacketEaten(Set<Item> items) {

  public static void encode(SPacketEaten msg, FriendlyByteBuf buf) {

    for (Item item : msg.items) {
      ResourceLocation rl = Services.REGISTRY.getItemKey(item);

      if (rl != null) {
        buf.writeResourceLocation(rl);
      }
    }
  }

  public static SPacketEaten decode(FriendlyByteBuf buf) {
    Set<Item> items = new HashSet<>();

    while (buf.isReadable()) {
      ResourceLocation rl = buf.readResourceLocation();
      Services.REGISTRY.getItem(rl).ifPresent(items::add);

    }
    return new SPacketEaten(items);
  }

  public static void handle(SPacketEaten msg) {
    DietClientPacketReceiver.handleEaten(msg);
  }
}
