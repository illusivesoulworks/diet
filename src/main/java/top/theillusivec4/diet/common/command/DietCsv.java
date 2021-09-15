package top.theillusivec4.diet.common.command;

import com.google.common.collect.Lists;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.DietApi;
import top.theillusivec4.diet.api.IDietGroup;

public class DietCsv {

  public static void write(PlayerEntity player, String modId) {
    List<String[]> data = new ArrayList<>();

    for (Item item : ForgeRegistries.ITEMS) {
      ResourceLocation rl = item.getRegistryName();

      if (rl != null) {

        if (!modId.isEmpty() && !rl.getNamespace().equals(modId)) {
          continue;
        }
        writeStack(player, item.getDefaultInstance(), data);
      }
    }
    write(data);
  }

  public static void writeGroup(PlayerEntity player, IDietGroup group) {
    List<String[]> data = new ArrayList<>();

    for (Item item : group.getTag().getAllElements()) {
      writeStack(player, item.getDefaultInstance(), data);
    }
    write(data);
  }

  public static void writeUncategorized(PlayerEntity player) {
    List<String[]> data = new ArrayList<>();

    for (Item item : ForgeRegistries.ITEMS) {
      Food food = item.getFood();

      if (food != null && food.getHealing() > 0 &&
          DietApi.getInstance().getGroups(player, item.getDefaultInstance()).isEmpty()) {
        data.add(new String[] {Objects.requireNonNull(item.getRegistryName()).toString()});
      }
    }
    write(data);
  }

  private static void writeStack(PlayerEntity player, ItemStack stack, List<String[]> data) {
    Map<IDietGroup, Float> result = DietApi.getInstance().get(player, stack).get();
    List<Map.Entry<IDietGroup, Float>> list = Lists.newArrayList(result.entrySet());

    if (!list.isEmpty()) {
      Map.Entry<IDietGroup, Float> entry = list.get(0);
      data.add(new String[] {Objects.requireNonNull(stack.getItem().getRegistryName()).toString(),
          entry.getKey().getName(), entry.getValue().toString()});

      for (int i = 1; i < list.size(); i++) {
        Map.Entry<IDietGroup, Float> entry2 = list.get(i);
        data.add(new String[] {"", entry2.getKey().getName(), entry2.getValue().toString()});
      }
    }
  }

  private static void write(List<String[]> data) {
    Path output = Paths.get(FMLPaths.GAMEDIR.get() + "/logs/diet.csv");

    try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {

      for (String[] datum : data) {
        writer.write(toCsv(datum));
        writer.newLine();
      }
    } catch (IOException e) {
      DietMod.LOGGER.error("Error attempting to export diet information, aborting...");
      e.printStackTrace();
    }
  }

  private static String toCsv(String[] data) {
    return String.join(",", data);
  }

  public enum ExportMode {
    ALL("all"),
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
