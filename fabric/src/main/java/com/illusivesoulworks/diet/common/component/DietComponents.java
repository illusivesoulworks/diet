package com.illusivesoulworks.diet.common.component;

import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.api.type.IDietTracker;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.resources.ResourceLocation;

public class DietComponents implements EntityComponentInitializer {

  public static final ComponentKey<DietComponent> DIET_TRACKER =
      ComponentRegistry.getOrCreate(new ResourceLocation(DietConstants.MOD_ID, "diet_tracker"),
          DietComponent.class);

  public static void setup() {
//    ServerPlayConnectionEvents.JOIN.register(
//        (handler, sender, server) -> {
//          DietComponents.DIET_TRACKER.maybeGet(handler.player)
//              .ifPresent(IDietTracker::sync);
//        });
    ServerPlayerEvents.COPY_FROM.register(
        (oldPlayer, newPlayer, alive) -> DietComponents.DIET_TRACKER.maybeGet(newPlayer)
            .ifPresent(tracker -> tracker.copy(oldPlayer, !alive)));
    ServerPlayerEvents.AFTER_RESPAWN.register(
        (oldPlayer, newPlayer, alive) -> DietComponents.DIET_TRACKER.maybeGet(newPlayer)
            .ifPresent(IDietTracker::sync));
    ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(
        (player, origin, destination) -> DietComponents.DIET_TRACKER.maybeGet(player)
            .ifPresent(IDietTracker::sync));
  }

  @Override
  public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    registry.registerForPlayers(DIET_TRACKER, DietComponent::new, RespawnCopyStrategy.NEVER_COPY);
  }
}
