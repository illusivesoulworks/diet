package top.theillusivec4.diet.common.network.server;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.diet.client.DietClientPacketReceiver;

public class SPacketEaten {

  public final Set<Item> items;

  public SPacketEaten(Set<Item> items) {
    this.items = items;
  }

  public static void encode(SPacketEaten msg, PacketBuffer buf) {

    for (Item item : msg.items) {
      ResourceLocation rl = item.getRegistryName();

      if (rl != null) {
        buf.writeResourceLocation(rl);
      }
    }
  }

  public static SPacketEaten decode(PacketBuffer buf) {
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
