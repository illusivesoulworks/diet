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

package com.illusivesoulworks.diet;

import com.illusivesoulworks.diet.api.DietApi;
import com.illusivesoulworks.diet.common.DietEvents;
import com.illusivesoulworks.diet.common.ModIdArgument;
import com.illusivesoulworks.diet.common.command.DietCommand;
import com.illusivesoulworks.diet.common.command.DietGroupArgument;
import com.illusivesoulworks.diet.common.component.DietComponents;
import com.illusivesoulworks.diet.common.data.group.DietGroups;
import com.illusivesoulworks.diet.common.data.suite.DietSuites;
import com.illusivesoulworks.diet.common.util.DietValueGenerator;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.server.packs.PackType;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;
import org.quiltmc.qsl.command.api.ServerArgumentType;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourceLoaderEvents;
import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader;

public class DietQuiltMod implements ModInitializer {

  @Override
  public void onInitialize(ModContainer modContainer) {
    Registry.register(Registry.ATTRIBUTE, DietCommonMod.resource("natural_regeneration"),
        DietApi.getInstance().getNaturalRegeneration());
    DietComponents.setup();
    ResourceLoader resourceManagerHelper = ResourceLoader.get(PackType.SERVER_DATA);
    resourceManagerHelper.registerReloader((IdentifiableResourceReloader) DietGroups.SERVER);
    resourceManagerHelper.registerReloader((IdentifiableResourceReloader) DietSuites.SERVER);
    resourceManagerHelper.addReloaderOrdering(DietCommonMod.resource("groups"),
        DietCommonMod.resource("suites"));
    ServerArgumentType.register(DietCommonMod.resource("modid"), ModIdArgument.class,
        SingletonArgumentInfo.contextFree(ModIdArgument::new),
        originalArg -> StringArgumentType.word());
    ServerArgumentType.register(DietCommonMod.resource("groups"), DietGroupArgument.class,
        SingletonArgumentInfo.contextFree(DietGroupArgument::group),
        originalArg -> StringArgumentType.word());
    CommandRegistrationCallback.EVENT.register(
        (dispatcher, registryAccess, environment) -> DietCommand.register(dispatcher));
    org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents.READY.register(
        DietValueGenerator::reload);
    ResourceLoaderEvents.END_DATA_PACK_RELOAD.register(
        (server, resourceManager, success) -> {

          if (server != null) {
            DietEvents.syncDatapack(server);
          }
        });
    ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(
        (player, joined) -> DietEvents.syncDatapack(player));
  }
}
