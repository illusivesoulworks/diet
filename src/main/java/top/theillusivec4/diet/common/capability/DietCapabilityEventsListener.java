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

package top.theillusivec4.diet.common.capability;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.DietCapability;
import top.theillusivec4.diet.api.IDietTracker;
import top.theillusivec4.diet.common.config.DietServerConfig;

@Mod.EventBusSubscriber(modid = DietMod.MOD_ID)
public class DietCapabilityEventsListener {

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {

    if (evt.getObject() instanceof PlayerEntity) {
      final LazyOptional<IDietTracker> capability =
          LazyOptional.of(() -> new PlayerDietTracker((PlayerEntity) evt.getObject()));
      evt.addCapability(DietCapability.DIET_TRACKER_ID, new Provider(capability));
      evt.addListener(capability::invalidate);
    }
  }

  @SubscribeEvent
  public static void playerClone(final PlayerEvent.Clone evt) {

    if (evt.getPlayer() instanceof ServerPlayerEntity) {
      ServerPlayerEntity player = (ServerPlayerEntity) evt.getPlayer();
      DietCapability.get(player)
          .ifPresent(diet -> DietCapability.get(evt.getOriginal()).ifPresent(originalDiet -> {
            for (Map.Entry<String, Float> entry : originalDiet.getValues().entrySet()) {
              float value = entry.getValue();

              if (evt.isWasDeath()) {
                value = Math.max(DietServerConfig.deathPenaltyMin,
                    value - DietServerConfig.deathPenaltyLoss);
              }
              diet.setValue(entry.getKey(), value);
            }
            diet.setActive(originalDiet.isActive());

            if (!evt.isWasDeath()) {
              diet.setModifiers(originalDiet.getModifiers());
            }
            diet.sync();
          }));
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void playerLoggedIn(final PlayerEvent.PlayerLoggedInEvent evt) {

    if (evt.getPlayer() instanceof ServerPlayerEntity) {
      ServerPlayerEntity player = (ServerPlayerEntity) evt.getPlayer();
      DietCapability.get(player).ifPresent(IDietTracker::sync);
    }
  }

  @SubscribeEvent
  public static void playerDimensionTravel(final PlayerEvent.PlayerChangedDimensionEvent evt) {

    if (evt.getPlayer() instanceof ServerPlayerEntity) {
      ServerPlayerEntity player = (ServerPlayerEntity) evt.getPlayer();
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
    LivingEntity livingEntity = evt.getEntityLiving();

    if (stack.getItem().isFood() && !livingEntity.world.isRemote &&
        livingEntity instanceof PlayerEntity) {
      DietCapability.get((PlayerEntity) livingEntity).ifPresent(diet -> diet.consume(stack));
    }
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  @SuppressWarnings("unused")
  public static void useBlock(final PlayerInteractEvent.RightClickBlock evt) {
    PlayerEntity player = evt.getPlayer();

    if (!player.world.isRemote) {
      DietCapability.get(player)
          .ifPresent(diet -> diet.consume(evt.getPos(), evt.getHand(), evt.getFace()));
    }
  }

  private static class Provider implements ICapabilitySerializable<INBT> {

    private static final IDietTracker EMPTY_TRACKER = new DietTrackerCapability.EmptyDietTracker();

    final LazyOptional<IDietTracker> capability;

    public Provider(LazyOptional<IDietTracker> capability) {
      this.capability = capability;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return DietCapability.DIET_TRACKER.orEmpty(cap, this.capability);
    }

    @Override
    public INBT serializeNBT() {
      return DietCapability.DIET_TRACKER.writeNBT(capability.orElse(EMPTY_TRACKER), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
      DietCapability.DIET_TRACKER.readNBT(capability.orElse(EMPTY_TRACKER), null, nbt);
    }
  }
}
