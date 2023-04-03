package top.theillusivec4.diet.common.network.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.common.group.DietGroups;
import top.theillusivec4.diet.common.util.DietValueGenerator;

public class SPacketGeneratedValues {

  public final Map<Item, Set<IDietGroup>> generated;

  public SPacketGeneratedValues(Map<Item, Set<IDietGroup>> generated) {
    this.generated = generated;
  }

  public static void encode(SPacketGeneratedValues msg, FriendlyByteBuf buf) {
    CompoundTag compoundNBT = new CompoundTag();

    for (Map.Entry<Item, Set<IDietGroup>> entry : msg.generated.entrySet()) {
      ListTag listNBT = new ListTag();

      for (IDietGroup group : entry.getValue()) {
        listNBT.add(StringTag.valueOf(group.getName()));
      }
      compoundNBT.put(Objects.requireNonNull(entry.getKey().builtInRegistryHolder().key().location()).toString(), listNBT);
    }
    buf.writeNbt(compoundNBT);
  }

  public static SPacketGeneratedValues decode(FriendlyByteBuf buf) {
    CompoundTag compoundNBT = buf.readNbt();
    Map<Item, Set<IDietGroup>> generated = new HashMap<>();
    Map<String, IDietGroup> groups = new HashMap<>();

    for (IDietGroup group : DietGroups.get()) {
      groups.put(group.getName(), group);
    }

    if (compoundNBT != null) {

      for (String name : compoundNBT.getAllKeys()) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));

        if (item != null) {
          ListTag listNBT = compoundNBT.getList(name, Tag.TAG_STRING);
          Set<IDietGroup> found = new HashSet<>();

          for (Tag nbt : listNBT) {
            String entry = nbt.getAsString();
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