/*
 * Copyright (C) 2021 C4
 *
 * This file is part of Diet, a mod made for Minecraft.
 *
 * Diet is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Diet is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Diet.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package top.theillusivec4.diet.common.config.data;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.common.config.DietClientConfig;
import top.theillusivec4.diet.common.config.DietServerConfig;
import top.theillusivec4.diet.common.effect.DietEffects;
import top.theillusivec4.diet.common.group.DietGroups;
import top.theillusivec4.diet.common.util.DietCalculator;

@Mod.EventBusSubscriber(modid = DietMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DietConfigReader {

  public static final ForgeConfigSpec CLIENT_SPEC;
  public static final Client CLIENT;

  public static final ForgeConfigSpec GENERAL_SPEC;
  public static final General GENERAL;

  public static final ForgeConfigSpec GROUPS_SPEC;
  public static final Groups GROUPS;

  public static final ForgeConfigSpec EFFECTS_SPEC;
  public static final Effects EFFECTS;

  private static final String CONFIG_PREFIX = "gui." + DietMod.MOD_ID + ".config.";
  private static final ObjectConverter CONVERTER = new ObjectConverter();

  static {
    final Pair<Client, ForgeConfigSpec> clientPair =
        new ForgeConfigSpec.Builder().configure(Client::new);
    CLIENT_SPEC = clientPair.getRight();
    CLIENT = clientPair.getLeft();
    final Pair<General, ForgeConfigSpec> generalPair = new ForgeConfigSpec.Builder()
        .configure(General::new);
    GENERAL_SPEC = generalPair.getRight();
    GENERAL = generalPair.getLeft();
    final Pair<Groups, ForgeConfigSpec> groupsPair =
        new ForgeConfigSpec.Builder().configure(Groups::new);
    GROUPS_SPEC = groupsPair.getRight();
    GROUPS = groupsPair.getLeft();
    final Pair<Effects, ForgeConfigSpec> effectsPair =
        new ForgeConfigSpec.Builder().configure(Effects::new);
    EFFECTS_SPEC = effectsPair.getRight();
    EFFECTS = effectsPair.getLeft();
  }

  public static void setup() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DietConfigReader.CLIENT_SPEC);
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, DietConfigReader.GENERAL_SPEC);
    createCustomServerConfig(DietConfigReader.GROUPS_SPEC, "groups");
    createCustomServerConfig(DietConfigReader.EFFECTS_SPEC, "effects");
  }

  public static class Client {

    private final ForgeConfigSpec.IntValue buttonX;
    private final ForgeConfigSpec.IntValue buttonY;

    public Client(ForgeConfigSpec.Builder builder) {
      builder.push("gui");

      buttonX =
          builder.comment("The x-position of the GUI button").translation(CONFIG_PREFIX + "buttonX")
              .defineInRange("buttonX", 126, -1000, 1000);

      buttonY =
          builder.comment("The y-position of the GUI button").translation(CONFIG_PREFIX + "buttonY")
              .defineInRange("buttonY", -22, -1000, 1000);

      builder.pop();
    }
  }

  public static class General {

    public final ForgeConfigSpec.DoubleValue deathPenaltyMin;
    public final ForgeConfigSpec.DoubleValue deathPenaltyLoss;

    public final ForgeConfigSpec.DoubleValue gainPenaltyPerGroup;
    public final ForgeConfigSpec.DoubleValue decayPenaltyPerGroup;

    public General(ForgeConfigSpec.Builder builder) {
      builder.push("death_penalty");

      deathPenaltyMin =
          builder.comment("The minimum percentage that diet groups can be reduced to upon death.")
              .translation(CONFIG_PREFIX + "deathPenaltyMin")
              .defineInRange("deathPenaltyMin", 0.0f, 0.0f, 1.0f);

      deathPenaltyLoss =
          builder.comment("The reduction in percentage applied to all diet groups upon death.")
              .translation(CONFIG_PREFIX + "deathPenaltyLoss")
              .defineInRange("deathPenaltyLoss", 1.0f, 0.0f, 1.0f);

      builder.pop();

      builder.push("calculation");

      gainPenaltyPerGroup = builder
          .comment("The percent reduction in total gain for each diet group consumed at once.")
          .translation(CONFIG_PREFIX + "gainPenaltyPerGroup")
          .defineInRange("gainPenaltyPerGroup", 0.15f, 0.0f, 1.0f);

      decayPenaltyPerGroup = builder
          .comment("The percent reduction in total decay for each diet group decayed at once.")
          .translation(CONFIG_PREFIX + "decayPenaltyPerGroup")
          .defineInRange("decayPenaltyPerGroup", 0.15f, 0.0f, 1.0f);

      builder.pop();
    }
  }

  public static class Groups {

    public GroupConfigList instance;

    public Groups(ForgeConfigSpec.Builder builder) {
      builder.comment("List of diet groups").define("groups", new ArrayList<>());
      builder.build();
    }
  }

  public static class Effects {

    public EffectConfigList instance;

    public Effects(ForgeConfigSpec.Builder builder) {
      builder.comment("List of diet effects").define("effects", new ArrayList<>());
      builder.build();
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  static void configLoad(final ModConfig.Loading evt) {
    load(evt.getConfig());
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  static void configReload(final ModConfig.Reloading evt) {
    load(evt.getConfig());
  }

  public static void readGroups(CommentedConfig configData) {
    DietConfigReader.GROUPS.instance = CONVERTER.toObject(configData, GroupConfigList::new);
    DietServerConfig.groups = DietConfigReader.GROUPS.instance.groups;
    DietGroups.build(DietServerConfig.groups);
  }

  public static void readEffects(CommentedConfig configData) {
    DietConfigReader.EFFECTS.instance = CONVERTER.toObject(configData, EffectConfigList::new);
    DietServerConfig.effects = DietConfigReader.EFFECTS.instance.effects;
    DietEffects.build(DietServerConfig.effects);
  }

  public static void readServer() {
    DietServerConfig.deathPenaltyMin = GENERAL.deathPenaltyMin.get().floatValue();
    DietServerConfig.deathPenaltyLoss = GENERAL.deathPenaltyLoss.get().floatValue();
    DietServerConfig.decayPenaltyPerGroup = GENERAL.decayPenaltyPerGroup.get().floatValue();
    DietServerConfig.gainPenaltyPerGroup = GENERAL.gainPenaltyPerGroup.get().floatValue();
  }

  public static void readClient() {
    DietClientConfig.buttonX = CLIENT.buttonX.get();
    DietClientConfig.buttonY = CLIENT.buttonY.get();
  }

  private static void createCustomServerConfig(ForgeConfigSpec spec, String name) {
    String fileName = DietMod.MOD_ID + "-" + name + ".toml";
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, spec, fileName);
    File defaults =
        new File(FMLPaths.GAMEDIR.get() + "/defaultconfigs/" + fileName);

    if (!defaults.exists()) {
      try {
        FileUtils.copyInputStreamToFile(
            Objects.requireNonNull(DietMod.class.getClassLoader().getResourceAsStream(fileName)),
            defaults);
      } catch (IOException e) {
        DietMod.LOGGER.error("Error creating default config for " + fileName);
      }
    }
  }

  private static void load(final ModConfig config) {

    if (config.getModId().equals(DietMod.MOD_ID)) {
      DietCalculator.invalidate();

      if (config.getType() == ModConfig.Type.SERVER) {
        ForgeConfigSpec spec = config.getSpec();
        CommentedConfig configData = config.getConfigData();

        if (spec == GROUPS_SPEC) {
          readGroups(configData);
        } else if (spec == EFFECTS_SPEC) {
          readEffects(configData);
        } else if (spec == GENERAL_SPEC) {
          readServer();
        }
      } else if (config.getType() == ModConfig.Type.CLIENT) {
        ForgeConfigSpec spec = config.getSpec();

        if (spec == CLIENT_SPEC) {
          readClient();
        }
      }
    }
  }
}
