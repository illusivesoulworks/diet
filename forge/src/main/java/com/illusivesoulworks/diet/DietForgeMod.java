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

package com.illusivesoulworks.diet;

import com.illusivesoulworks.diet.api.DietApi;
import com.illusivesoulworks.diet.api.type.IDietTracker;
import com.illusivesoulworks.diet.client.DietKeys;
import com.illusivesoulworks.diet.common.DietEvents;
import com.illusivesoulworks.diet.common.command.DietCommand;
import com.illusivesoulworks.diet.common.command.DietGroupArgument;
import com.illusivesoulworks.diet.common.config.DietConfig;
import com.illusivesoulworks.diet.common.impl.group.DietGroups;
import com.illusivesoulworks.diet.common.impl.suite.DietSuites;
import com.illusivesoulworks.diet.common.integration.IntegrationManager;
import com.illusivesoulworks.diet.common.network.DietForgeNetwork;
import com.illusivesoulworks.diet.common.util.DietOverride;
import com.illusivesoulworks.diet.common.util.DietValueGenerator;
import com.illusivesoulworks.diet.data.DietBlockTagsProvider;
import com.illusivesoulworks.diet.data.DietTagsProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(DietConstants.MOD_ID)
public class DietForgeMod {

  private static final DeferredRegister<Attribute> ATTRIBUTES =
      DeferredRegister.create(ForgeRegistries.ATTRIBUTES, DietConstants.MOD_ID);

  public static final RegistryObject<Attribute> NATURAL_REGEN =
      ATTRIBUTES.register("natural_regeneration",
          () -> DietApi.getInstance().getNaturalRegeneration());

  public DietForgeMod() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::process);
    eventBus.addListener(this::gatherData);
    eventBus.addListener(this::registerCaps);
    eventBus.addListener(this::modifyAttributes);
    DietConfig.setup();
    ATTRIBUTES.register(eventBus);
    MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
    MinecraftForge.EVENT_BUS.addListener(this::onDatapackSync);
    MinecraftForge.EVENT_BUS.addListener(this::setupCommands);
    MinecraftForge.EVENT_BUS.addListener(this::addReloaders);
  }

  private void setup(final FMLCommonSetupEvent evt) {
    DietForgeNetwork.setup();
    IntegrationManager.setup();
    evt.enqueueWork(() -> ArgumentTypeInfos.registerByClass(DietGroupArgument.class,
        SingletonArgumentInfo.contextFree(DietGroupArgument::group)));
  }

  private void addReloaders(final AddReloadListenerEvent evt) {
    evt.addListener(DietGroups.SERVER);
    evt.addListener(DietSuites.SERVER);
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
      generator.addProvider(true,
          new DietTagsProvider(generator, blockTagsProvider, existingFileHelper));
    }
  }

  private void registerCaps(final RegisterCapabilitiesEvent evt) {
    evt.register(IDietTracker.class);
  }

  private void modifyAttributes(final EntityAttributeModificationEvent evt) {
    evt.add(EntityType.PLAYER, DietApi.getInstance().getNaturalRegeneration());
  }

  private void serverStarting(final ServerStartingEvent evt) {
    DietValueGenerator.reload(evt.getServer());
  }

  private void onDatapackSync(final OnDatapackSyncEvent evt) {

    if (evt.getPlayer() == null) {
      DietEvents.syncDatapack(evt.getPlayerList().getServer());
    } else {
      DietEvents.syncDatapack(evt.getPlayer());
    }
  }

  private void setupCommands(final RegisterCommandsEvent evt) {
    DietCommand.register(evt.getDispatcher());
  }

  @Mod.EventBusSubscriber(modid = DietConstants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class ClientModEvents {

    @SubscribeEvent
    public static void registerKeys(final RegisterKeyMappingsEvent evt) {
      evt.register(DietKeys.get());
    }
  }
}
