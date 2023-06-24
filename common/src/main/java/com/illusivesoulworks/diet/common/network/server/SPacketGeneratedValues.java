package com.illusivesoulworks.diet.common.network.server;

import com.illusivesoulworks.diet.api.type.IDietGroup;
import com.illusivesoulworks.diet.common.impl.group.DietGroups;
import com.illusivesoulworks.diet.common.util.DietValueGenerator;
import com.illusivesoulworks.diet.platform.Services;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record SPacketGeneratedValues(Map<Item, Set<IDietGroup>> generated) {

  public static void encode(SPacketGeneratedValues msg, FriendlyByteBuf buf) {
    CompoundTag compoundNBT = new CompoundTag();

    for (Map.Entry<Item, Set<IDietGroup>> entry : msg.generated.entrySet()) {
      ListTag listNBT = new ListTag();

      for (IDietGroup group : entry.getValue()) {
        listNBT.add(StringTag.valueOf(group.getName()));
      }
      compoundNBT.put(
          Objects.requireNonNull(Services.REGISTRY.getItemKey(entry.getKey())).toString(), listNBT);
    }
    buf.writeNbt(compoundNBT);
  }

  public static SPacketGeneratedValues decode(FriendlyByteBuf buf) {
    CompoundTag compoundNBT = buf.readNbt();
    Map<Item, Set<IDietGroup>> generated = new HashMap<>();
    Map<String, IDietGroup> groups = new HashMap<>();

    for (IDietGroup group : DietGroups.CLIENT.getGroups()) {
      groups.put(group.getName(), group);
    }

    if (compoundNBT != null) {

      for (String name : compoundNBT.getAllKeys()) {
        Item item = Services.REGISTRY.getItem(new ResourceLocation(name)).orElse(null);

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

  public static void handle(SPacketGeneratedValues msg) {
    DietValueGenerator.sync(msg);
  }
}
