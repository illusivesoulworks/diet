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

package com.illusivesoulworks.diet.common.capability;

import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.api.type.IDietTracker;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DietConstants.MOD_ID)
public class DietCapabilityEventsListener {

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {

    if (evt.getObject() instanceof Player player) {
      final IDietTracker tracker = new PlayerDietTracker(player);
      final LazyOptional<IDietTracker> capability = LazyOptional.of(() -> tracker);
      evt.addCapability(DietCapability.DIET_TRACKER_ID, new Provider(tracker, capability));
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void playerClone(final PlayerEvent.Clone evt) {

    if (evt.getEntity() instanceof ServerPlayer player) {
      Player original = evt.getOriginal();
      original.reviveCaps();
      DietCapability.get(player)
          .ifPresent(tracker -> tracker.copy(original, evt.isWasDeath()));
      original.invalidateCaps();
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void playerRespawned(final PlayerEvent.PlayerRespawnEvent evt) {

    if (evt.getEntity() instanceof ServerPlayer player) {
      DietCapability.get(player).ifPresent(IDietTracker::sync);
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void playerLoggedIn(final PlayerEvent.PlayerLoggedInEvent evt) {

    if (evt.getEntity() instanceof ServerPlayer player) {
      DietCapability.get(player).ifPresent(IDietTracker::sync);
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void playerDimensionTravel(final PlayerEvent.PlayerChangedDimensionEvent evt) {

    if (evt.getEntity() instanceof ServerPlayer player) {
      DietCapability.get(player).ifPresent(IDietTracker::sync);
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void playerTick(final TickEvent.PlayerTickEvent evt) {

    if (evt.side == LogicalSide.SERVER && evt.phase == TickEvent.Phase.END) {
      DietCapability.get(evt.player).ifPresent(IDietTracker::tick);
    }
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  @SuppressWarnings("unused")
  public static void finishItemUse(final LivingEntityUseItemEvent.Finish evt) {
    ItemStack stack = evt.getItem();
    LivingEntity livingEntity = evt.getEntity();

    if (!livingEntity.level.isClientSide && livingEntity instanceof Player) {
      FoodProperties food = stack.getFoodProperties(livingEntity);

      if (food != null) {
        DietCapability.get((Player) livingEntity).ifPresent(diet -> diet.consume(stack));
      }
    }
  }

  private record Provider(IDietTracker instance, LazyOptional<IDietTracker> capability)
      implements ICapabilitySerializable<Tag> {

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {

      if (DietCapability.DIET_TRACKER != null) {
        return DietCapability.DIET_TRACKER.orEmpty(cap, this.capability);
      } else {
        DietConstants.LOG.error("Missing Diet capability!");
        return LazyOptional.empty();
      }
    }

    @Override
    public Tag serializeNBT() {

      if (this.instance != null) {
        CompoundTag tag = new CompoundTag();
        this.instance.save(tag);
        return tag;
      } else {
        DietConstants.LOG.error("Missing Diet capability!");
        return new CompoundTag();
      }
    }

    @Override
    public void deserializeNBT(Tag nbt) {

      if (this.instance != null && nbt instanceof CompoundTag tag) {
        this.instance.load(tag);
      } else {
        DietConstants.LOG.error("Missing Diet capability!");
      }
    }
  }
}
