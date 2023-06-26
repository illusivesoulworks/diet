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

package com.illusivesoulworks.diet.common.component;

import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.api.type.IDietTracker;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.qsl.entity_events.api.EntityWorldChangeEvents;
import org.quiltmc.qsl.entity_events.api.ServerPlayerEntityCopyCallback;

public class DietComponents implements EntityComponentInitializer {

  public static final ComponentKey<DietComponent> DIET_TRACKER =
      ComponentRegistry.getOrCreate(new ResourceLocation(DietConstants.MOD_ID, "diet_tracker"),
          DietComponent.class);

  public static void setup() {
    ServerPlayerEntityCopyCallback.EVENT.register(
        (oldPlayer, newPlayer, alive) -> DietComponents.DIET_TRACKER.maybeGet(newPlayer)
            .ifPresent(tracker -> {
              tracker.copy(oldPlayer, !alive);
              tracker.sync();
            }));
    EntityWorldChangeEvents.AFTER_PLAYER_WORLD_CHANGE.register(
        (player, origin, destination) -> DietComponents.DIET_TRACKER.maybeGet(player)
            .ifPresent(IDietTracker::sync));
  }

  @Override
  public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    registry.registerForPlayers(DIET_TRACKER, DietComponent::new, RespawnCopyStrategy.NEVER_COPY);
  }
}
