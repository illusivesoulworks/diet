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

import javax.annotation.Nonnull;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.diet.client.DietKeys;
import top.theillusivec4.diet.common.capability.DietTrackerCapability;
import top.theillusivec4.diet.common.command.DietCommand;
import top.theillusivec4.diet.common.config.data.DietConfigReader;
import top.theillusivec4.diet.common.network.DietNetwork;
import top.theillusivec4.diet.common.util.DietCalculator;
import top.theillusivec4.diet.common.util.DietMessage;

@Mod(DietMod.MOD_ID)
public class DietMod {

  public static final String MOD_ID = "diet";
  public static final Logger LOGGER = LogManager.getLogger();

  public DietMod() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
    eventBus.addListener(this::enqueue);
    eventBus.addListener(this::process);
    MinecraftForge.EVENT_BUS.addListener(this::setupCommands);
    MinecraftForge.EVENT_BUS.addListener(this::reload);
    DietConfigReader.setup();
  }

  private void setup(final FMLCommonSetupEvent evt) {
    DietTrackerCapability.setup();
    DietNetwork.setup();
  }

  private void clientSetup(final FMLClientSetupEvent evt) {
    DietKeys.setup();
  }

  private void enqueue(final InterModEnqueueEvent evt) {
    DietMessage.enqueue();
  }

  private void process(final InterModProcessEvent evt) {
    DietMessage.process(evt.getIMCStream());
  }

  private void setupCommands(final RegisterCommandsEvent evt) {
    DietCommand.register(evt.getDispatcher());
  }

  private void reload(final AddReloadListenerEvent evt) {
    evt.addListener(new ReloadListener<Void>() {

      @Nonnull
      @Override
      protected Void prepare(@Nonnull IResourceManager resourceManagerIn,
                             @Nonnull IProfiler profilerIn) {
        return null;
      }

      @Override
      protected void apply(@Nonnull Void objectIn, @Nonnull IResourceManager resourceManagerIn,
                           @Nonnull IProfiler profilerIn) {
        DietCalculator.invalidate();
      }
    });
  }
}
