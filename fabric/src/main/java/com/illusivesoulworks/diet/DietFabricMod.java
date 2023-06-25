package com.illusivesoulworks.diet;

import com.illusivesoulworks.diet.api.DietApi;
import com.illusivesoulworks.diet.common.DietEvents;
import com.illusivesoulworks.diet.common.command.DietCommand;
import com.illusivesoulworks.diet.common.command.DietGroupArgument;
import com.illusivesoulworks.diet.common.component.DietComponents;
import com.illusivesoulworks.diet.common.data.group.DietGroups;
import com.illusivesoulworks.diet.common.data.suite.DietSuites;
import com.illusivesoulworks.diet.common.util.DietValueGenerator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.server.packs.PackType;

public class DietFabricMod implements ModInitializer {

  @Override
  public void onInitialize() {
    Registry.register(Registry.ATTRIBUTE, DietCommonMod.resource("natural_regeneration"),
        DietApi.getInstance().getNaturalRegeneration());
    DietComponents.setup();
    ResourceManagerHelper resourceManagerHelper = ResourceManagerHelper.get(PackType.SERVER_DATA);
    resourceManagerHelper.registerReloadListener(
        (IdentifiableResourceReloadListener) DietGroups.SERVER);
    resourceManagerHelper.registerReloadListener(
        (IdentifiableResourceReloadListener) DietSuites.SERVER);
    ArgumentTypeRegistry.registerArgumentType(DietCommonMod.resource("groups"),
        DietGroupArgument.class, SingletonArgumentInfo.contextFree(DietGroupArgument::group));
    CommandRegistrationCallback.EVENT.register(
        (dispatcher, registryAccess, environment) -> DietCommand.register(dispatcher));
    ServerLifecycleEvents.SERVER_STARTED.register(DietValueGenerator::reload);
    ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(
        (server, resourceManager, success) -> DietEvents.syncDatapack(server));
    ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(
        (player, joined) -> DietEvents.syncDatapack(player));
  }
}
