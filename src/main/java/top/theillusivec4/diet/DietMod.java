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

package top.theillusivec4.diet;

import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.diet.client.DietKeys;
import top.theillusivec4.diet.common.capability.DietTrackerCapability;
import top.theillusivec4.diet.common.command.DietCommand;
import top.theillusivec4.diet.common.command.DietGroupArgument;
import top.theillusivec4.diet.common.config.data.DietConfigReader;
import top.theillusivec4.diet.common.integration.IntegrationManager;
import top.theillusivec4.diet.common.network.DietNetwork;
import top.theillusivec4.diet.common.util.DietValueGenerator;
import top.theillusivec4.diet.common.util.DietOverride;
import top.theillusivec4.diet.data.DietBlockTagsProvider;
import top.theillusivec4.diet.data.DietTagsProvider;

@Mod(DietMod.MOD_ID)
public class DietMod {

  public static final String MOD_ID = "diet";
  public static final Logger LOGGER = LogManager.getLogger();

  public static String id(String name) {
    return DietMod.MOD_ID + ":" + name;
  }

  public DietMod() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
    eventBus.addListener(this::process);
    eventBus.addListener(this::gatherData);
    MinecraftForge.EVENT_BUS.addListener(this::setupCommands);
    MinecraftForge.EVENT_BUS.addListener(this::onDatapackSync);
    DietConfigReader.setup();
  }

  private void setup(final FMLCommonSetupEvent evt) {
    DietTrackerCapability.setup();
    DietNetwork.setup();
    IntegrationManager.setup();
    evt.enqueueWork(() -> ArgumentTypes.register(id("group"), DietGroupArgument.class,
        new ArgumentSerializer<>(DietGroupArgument::group)));
  }

  private void clientSetup(final FMLClientSetupEvent evt) {
    DietKeys.setup();
  }

  private void process(final InterModProcessEvent evt) {
    DietOverride.process(evt.getIMCStream());
  }

  private void gatherData(final GatherDataEvent evt) {
    DataGenerator generator = evt.getGenerator();

    if (evt.includeServer()) {
      ExistingFileHelper existingFileHelper = evt.getExistingFileHelper();
      DietBlockTagsProvider blockTagsProvider =
          new DietBlockTagsProvider(generator, existingFileHelper);
      generator.addProvider(
          new DietTagsProvider(generator, blockTagsProvider, existingFileHelper));
//      generator.addProvider(
//          new DietOptionalTagsProvider(generator, blockTagsProvider, existingFileHelper));
    }
  }

  private void setupCommands(final RegisterCommandsEvent evt) {
    DietCommand.register(evt.getDispatcher());
  }

  private void onDatapackSync(final OnDatapackSyncEvent evt) {
    DietValueGenerator.reload(evt.getPlayerList().getServer());
  }
}
