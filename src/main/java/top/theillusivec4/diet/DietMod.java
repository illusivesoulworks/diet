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

import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.diet.api.DietApi;
import top.theillusivec4.diet.api.IDietTracker;
import top.theillusivec4.diet.client.DietKeys;
import top.theillusivec4.diet.common.command.DietCommand;
import top.theillusivec4.diet.common.command.DietGroupArgument;
import top.theillusivec4.diet.common.config.data.DietConfigReader;
import top.theillusivec4.diet.common.integration.IntegrationManager;
import top.theillusivec4.diet.common.network.DietNetwork;
import top.theillusivec4.diet.common.util.DietOverride;
import top.theillusivec4.diet.common.util.DietValueGenerator;
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
    eventBus.addListener(this::registerCaps);
    eventBus.addGenericListener(Attribute.class, this::registerAttributes);
    eventBus.addListener(this::modifyAttributes);
    DietConfigReader.setup();
  }

  private void setup(final FMLCommonSetupEvent evt) {
    DietNetwork.setup();
    DietValueGenerator.setup();
    DietCommand.setup();
    IntegrationManager.setup();
    evt.enqueueWork(() -> ArgumentTypes.register(id("group"), DietGroupArgument.class,
        new EmptyArgumentSerializer<>(DietGroupArgument::group)));
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
    }
  }

  private void registerCaps(final RegisterCapabilitiesEvent evt) {
    evt.register(IDietTracker.class);
  }

  private void registerAttributes(final RegistryEvent.Register<Attribute> evt) {
    evt.getRegistry().register(DietApi.getInstance().getNaturalRegeneration()
        .setRegistryName(new ResourceLocation(DietMod.MOD_ID, "natural_regeneration")));
  }

  private void modifyAttributes(final EntityAttributeModificationEvent evt) {
    evt.add(EntityType.PLAYER, DietApi.getInstance().getNaturalRegeneration());
  }
}
