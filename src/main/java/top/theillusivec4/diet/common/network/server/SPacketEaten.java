package top.theillusivec4.diet.common.network.server;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.diet.client.DietClientPacketReceiver;

public class SPacketEaten {

  public final Set<Item> items;

  public SPacketEaten(Set<Item> items) {
    this.items = items;
  }

  public static void encode(SPacketEaten msg, FriendlyByteBuf buf) {

    for (Item item : msg.items) {
      ResourceLocation rl = item.getRegistryName();

      if (rl != null) {
        buf.writeResourceLocation(rl);
      }
    }
  }

  public static SPacketEaten decode(FriendlyByteBuf buf) {
    Set<Item> items = new HashSet<>();

    while (buf.isReadable()) {
      ResourceLocation rl = buf.readResourceLocation();
      Item item = ForgeRegistries.ITEMS.getValue(rl);

      if (item != null) {
        items.add(item);
      }
    }
    return new SPacketEaten(items);
  }

  public static void handle(SPacketEaten msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> DietClientPacketReceiver.handleEaten(msg));
    ctx.get().setPacketHandled(true);
  }
}
