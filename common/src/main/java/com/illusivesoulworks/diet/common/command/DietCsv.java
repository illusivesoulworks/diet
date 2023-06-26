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

package com.illusivesoulworks.diet.common.command;

import com.google.common.collect.Lists;
import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.api.DietApi;
import com.illusivesoulworks.diet.api.type.IDietGroup;
import com.illusivesoulworks.diet.common.util.DietValueGenerator;
import com.illusivesoulworks.diet.platform.Services;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DietCsv {

  public static void write(Player player, String modId) {
    List<String[]> data = new ArrayList<>();

    for (Item item : Services.REGISTRY.getItems()) {
      ResourceLocation rl = Services.REGISTRY.getItemKey(item);

      if (rl != null) {

        if (!modId.isEmpty() && !rl.getNamespace().equals(modId)) {
          continue;
        }
        writeStack(player, item.getDefaultInstance(), data);
      }
    }
    write(data);
  }

  public static void writeGroup(Player player, IDietGroup group) {
    List<String[]> data = new ArrayList<>();
    for (Item tagItem : Services.REGISTRY.getTagItems(group.getTag())) {
      writeStack(player, tagItem.getDefaultInstance(), data);
    }
    write(data);
  }

  public static void writeUncategorized(Player player) {
    List<String[]> data = new ArrayList<>();

    for (Item item : Services.REGISTRY.getItems()) {
      ItemStack stack = item.getDefaultInstance();
      FoodProperties food = Services.REGISTRY.getFoodProperties(stack, player);

      if (food != null && food.getNutrition() > 0 &&
          DietApi.getInstance().getGroups(player, stack).isEmpty()) {
        data.add(new String[] {getName(item)});
      }
    }
    write(data);
  }

  public static void writeTrails(Player player) {
    List<String[]> data = new ArrayList<>();

    for (Item item : Services.REGISTRY.getItems()) {

      if (!DietApi.getInstance().getGroups(player, item.getDefaultInstance()).isEmpty()) {
        List<Item> trail = DietValueGenerator.getTrail(item);

        if (!trail.isEmpty()) {
          data.add(new String[] {getName(item), getName(trail.get(0))});

          for (int i = 1; i < trail.size(); i++) {
            data.add(new String[] {"", getName(trail.get(i))});
          }
        }
      }
    }
    write(data);
  }

  private static void writeStack(Player player, ItemStack stack, List<String[]> data) {
    Map<IDietGroup, Float> result = DietApi.getInstance().get(player, stack).get();
    List<Map.Entry<IDietGroup, Float>> list = Lists.newArrayList(result.entrySet());

    if (!list.isEmpty()) {
      Map.Entry<IDietGroup, Float> entry = list.get(0);
      data.add(new String[] {getName(stack.getItem()), entry.getKey().getName(),
          entry.getValue().toString()});

      for (int i = 1; i < list.size(); i++) {
        Map.Entry<IDietGroup, Float> entry2 = list.get(i);
        data.add(new String[] {"", entry2.getKey().getName(), entry2.getValue().toString()});
      }
    }
  }

  private static void write(List<String[]> data) {
    Path output = Paths.get(Services.PLATFORM.getGameDir() + "/logs/diet.csv");

    try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {

      for (String[] datum : data) {
        writer.write(toCsv(datum));
        writer.newLine();
      }
    } catch (IOException e) {
      DietConstants.LOG.error("Error attempting to export diet information, aborting...");
      e.printStackTrace();
    }
  }

  private static String toCsv(String[] data) {
    return String.join(",", data);
  }

  private static String getName(Item item) {
    return Objects.requireNonNull(Services.REGISTRY.getItemKey(item)).toString();
  }

  public enum ExportMode {
    ALL("all"),
    TRAILS("trails"),
    MOD_ID("mod_id"),
    GROUP("group"),
    UNCATEGORIZED("uncategorized");

    private final String id;

    ExportMode(String idIn) {
      id = idIn;
    }

    public String getId() {
      return id;
    }
  }
}
