/*
 * Copyright (C) 2021-2023 Illusive Soulworks
 *
 * Diet is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Diet is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Diet.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.diet.common.network.server;

import com.illusivesoulworks.diet.client.DietClientPacketReceiver;
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

public record SPacketGroups(CompoundTag groups, Map<Item, Set<String>> generated) {

  public static void encode(SPacketGroups msg, FriendlyByteBuf buf) {
    CompoundTag compoundNBT = new CompoundTag();

    for (Map.Entry<Item, Set<String>> entry : msg.generated().entrySet()) {
      ListTag listNBT = new ListTag();

      for (String s : entry.getValue()) {
        listNBT.add(StringTag.valueOf(s));
      }
      compoundNBT.put(
          Objects.requireNonNull(Services.REGISTRY.getItemKey(entry.getKey())).toString(), listNBT);
    }
    buf.writeNbt(compoundNBT);
    buf.writeNbt(msg.groups());
  }

  public static SPacketGroups decode(FriendlyByteBuf buf) {
    CompoundTag compoundNBT = buf.readNbt();
    Map<Item, Set<String>> generated = new HashMap<>();

    if (compoundNBT != null) {

      for (String name : compoundNBT.getAllKeys()) {
        Item item = Services.REGISTRY.getItem(new ResourceLocation(name)).orElse(null);

        if (item != null) {
          ListTag listNBT = compoundNBT.getList(name, Tag.TAG_STRING);
          Set<String> found = new HashSet<>();

          for (Tag nbt : listNBT) {
            String entry = nbt.getAsString();
            found.add(entry);
          }
          generated.put(item, found);
        }
      }
    }
    return new SPacketGroups(buf.readNbt(), generated);
  }

  public static void handle(SPacketGroups msg) {
    DietClientPacketReceiver.handleGroups(msg.groups(), msg.generated());
  }
}
