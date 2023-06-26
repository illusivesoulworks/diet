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

package com.illusivesoulworks.diet.common.config;

import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.platform.Services;
import com.illusivesoulworks.spectrelib.config.SpectreConfig;
import com.illusivesoulworks.spectrelib.config.SpectreConfigLoader;
import com.illusivesoulworks.spectrelib.config.SpectreConfigSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.tuple.Pair;

public class DietConfig {

  public static final SpectreConfigSpec CLIENT_SPEC;
  public static final Client CLIENT;

  public static final SpectreConfigSpec SERVER_SPEC;
  public static final Server SERVER;

  private static final String CONFIG_PREFIX = "gui." + DietConstants.MOD_ID + ".config.";

  static {
    final Pair<Client, SpectreConfigSpec> clientPair =
        new SpectreConfigSpec.Builder().configure(Client::new);
    CLIENT_SPEC = clientPair.getRight();
    CLIENT = clientPair.getLeft();
    final Pair<Server, SpectreConfigSpec> generalPair = new SpectreConfigSpec.Builder()
        .configure(Server::new);
    SERVER_SPEC = generalPair.getRight();
    SERVER = generalPair.getLeft();
  }

  public static class Client {

    public final SpectreConfigSpec.IntValue buttonX;
    public final SpectreConfigSpec.IntValue buttonY;
    public final SpectreConfigSpec.ConfigValue<String> textColor;
    public final SpectreConfigSpec.BooleanValue addButton;

    public Client(SpectreConfigSpec.Builder builder) {
      addButton =
          builder.comment("If enabled, a button to the Diet GUI appears in player inventories.")
              .translation(CONFIG_PREFIX + "addButton").define("addButton", true);

      buttonX = builder.comment("The x-position of the Diet GUI button in player inventories.")
          .translation(CONFIG_PREFIX + "buttonX").defineInRange("buttonX", 126, -10000, 10000);

      buttonY = builder.comment("The y-position of the Diet GUI button in player inventories.")
          .translation(CONFIG_PREFIX + "buttonY").defineInRange("buttonY", -22, -10000, 10000);

      textColor =
          builder.comment("The primary text color of the Diet GUI, as an integer or hex code.")
              .translation(CONFIG_PREFIX + "textColor").define("textColor", "4210752");
    }
  }

  public static class Server {

    public final SpectreConfigSpec.IntValue deathPenaltyMin;
    public final SpectreConfigSpec.IntValue deathPenaltyLoss;
    public final SpectreConfigSpec.EnumValue<DeathPenaltyMethod> deathPenaltyMethod;

    public final SpectreConfigSpec.IntValue gainPenaltyPerGroup;
    public final SpectreConfigSpec.IntValue decayPenaltyPerGroup;
    public final SpectreConfigSpec.ConfigValue<List<? extends String>> foodOverrides;

    public final SpectreConfigSpec.BooleanValue hideTooltipsUntilEaten;
    public final SpectreConfigSpec.BooleanValue generateGroupsForEmptyItems;

    public Server(SpectreConfigSpec.Builder builder) {
      deathPenaltyMin =
          builder.comment("The minimum percentage that diet groups can be reduced to upon death.")
              .translation(CONFIG_PREFIX + "deathPenaltyMin")
              .defineInRange("deathPenaltyMin", 0, 0, 100);

      deathPenaltyLoss =
          builder.comment("The reduction in percentage applied to all diet groups upon death.")
              .translation(CONFIG_PREFIX + "deathPenaltyLoss")
              .defineInRange("deathPenaltyLoss", 100, 0, 100);

      deathPenaltyMethod = builder
          .comment("""
              The method to apply for losses due to death penalties.
              AMOUNT = Reduce by a flat percentage amount
              PERCENT = Reduce by a percent of the current value
              RESET = Reset value to defaults""")
          .translation(CONFIG_PREFIX + "deathPenaltyMethod")
          .defineEnum("deathPenaltyMethod", DeathPenaltyMethod.AMOUNT);

      gainPenaltyPerGroup = builder
          .comment("The percentage reduction in total gain for each diet group consumed at once.")
          .translation(CONFIG_PREFIX + "gainPenaltyPerGroup")
          .defineInRange("gainPenaltyPerGroup", 15, 0, 100);

      decayPenaltyPerGroup = builder
          .comment("The percentage reduction in total decay for each diet group decayed at once.")
          .translation(CONFIG_PREFIX + "decayPenaltyPerGroup")
          .defineInRange("decayPenaltyPerGroup", 15, 0, 100);

      foodOverrides = builder.comment(
              "List of food quality overrides for diet gain values." +
                  "\nFormat: \"modid:name;quality\"")
          .translation(CONFIG_PREFIX + "foodOverrides")
          .defineList("foodOverrides", new ArrayList<>(), s -> s instanceof String);

      generateGroupsForEmptyItems = builder.comment(
              "If enabled, food groups are assigned to unclassified items based on ingredients.")
          .translation(CONFIG_PREFIX + "generateGroupsForEmptyItems")
          .define("generateGroupsForEmptyItems", true);

      hideTooltipsUntilEaten = builder.comment(
              "If enabled, food group tooltips are hidden until player has eaten that type of item.")
          .translation(CONFIG_PREFIX + "hideTooltipsUntilEaten")
          .define("hideTooltipsUntilEaten", false);
    }

    private final Map<Item, Float> overrideMap = new HashMap<>();
    boolean initializedFoodOverrides = false;

    public Float getFoodOverride(Item item) {

      if (!initializedFoodOverrides) {
        overrideMap.clear();
        initializedFoodOverrides = true;

        for (String s : foodOverrides.get()) {
          String[] split = s.split(";");
          Services.REGISTRY.getItem(new ResourceLocation(split[0]))
              .ifPresent(item1 -> overrideMap.put(item1, Float.parseFloat(split[1])));
        }
      }
      return overrideMap.get(item);
    }
  }

  public enum DeathPenaltyMethod {
    AMOUNT,
    PERCENT,
    RESET
  }
}
