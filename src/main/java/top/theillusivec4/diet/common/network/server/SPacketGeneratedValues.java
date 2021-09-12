package top.theillusivec4.diet.common.network.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.common.group.DietGroups;
import top.theillusivec4.diet.common.util.DietValueGenerator;

public class SPacketGeneratedValues {

  public final Map<Item, Set<IDietGroup>> generated;

  public SPacketGeneratedValues(Map<Item, Set<IDietGroup>> generated) {
    this.generated = generated;
  }

  public static void encode(SPacketGeneratedValues msg, PacketBuffer buf) {
    CompoundNBT compoundNBT = new CompoundNBT();

    for (Map.Entry<Item, Set<IDietGroup>> entry : msg.generated.entrySet()) {
      ListNBT listNBT = new ListNBT();

      for (IDietGroup group : entry.getValue()) {
        listNBT.add(StringNBT.valueOf(group.getName()));
      }
      compoundNBT.put(Objects.requireNonNull(entry.getKey().getRegistryName()).toString(), listNBT);
    }
    buf.writeCompoundTag(compoundNBT);
  }

  public static SPacketGeneratedValues decode(PacketBuffer buf) {
    CompoundNBT compoundNBT = buf.readCompoundTag();
    Map<Item, Set<IDietGroup>> generated = new HashMap<>();
    Map<String, IDietGroup> groups = new HashMap<>();

    for (IDietGroup group : DietGroups.get()) {
      groups.put(group.getName(), group);
    }

    if (compoundNBT != null) {

      for (String name : compoundNBT.keySet()) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));

        if (item != null) {
          ListNBT listNBT = compoundNBT.getList(name, Constants.NBT.TAG_STRING);
          Set<IDietGroup> found = new HashSet<>();

          for (INBT nbt : listNBT) {
            String entry = nbt.getString();
            IDietGroup group = groups.get(entry);

            if (group != null) {
              found.add(group);
            }
          }
          generated.put(item, found);
        }
      }
    }
    return new SPacketGeneratedValues(generated);
  }

  public static void handle(SPacketGeneratedValues msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> DietValueGenerator.sync(msg));
    ctx.get().setPacketHandled(true);
  }
}
