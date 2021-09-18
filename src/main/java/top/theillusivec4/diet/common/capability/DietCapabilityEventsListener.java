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
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
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
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.api.IDietTracker;
import top.theillusivec4.diet.common.config.DietServerConfig;
import top.theillusivec4.diet.common.group.DietGroups;

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
  @SuppressWarnings("unused")
  public static void playerClone(final PlayerEvent.Clone evt) {

    if (evt.getPlayer() instanceof ServerPlayerEntity) {
      ServerPlayerEntity player = (ServerPlayerEntity) evt.getPlayer();
      DietCapability.get(player)
          .ifPresent(diet -> DietCapability.get(evt.getOriginal()).ifPresent(originalDiet -> {
            Map<String, Float> originalValues = originalDiet.getValues();

            for (IDietGroup group : DietGroups.get()) {
              String id = group.getName();
              float originalValue = originalValues.getOrDefault(id, group.getDefaultValue());
              float newValue = originalValue;

              if (evt.isWasDeath()) {

                if (DietServerConfig.deathPenaltyMethod ==
                    DietServerConfig.DeathPenaltyMethod.RESET) {
                  newValue = group.getDefaultValue();
                } else {

                  if (DietServerConfig.deathPenaltyMethod ==
                      DietServerConfig.DeathPenaltyMethod.AMOUNT) {
                    newValue -= DietServerConfig.deathPenaltyLoss;
                  } else {
                    newValue *= (1 - DietServerConfig.deathPenaltyLoss);
                  }
                  newValue =
                      Math.min(originalValue, Math.max(newValue, DietServerConfig.deathPenaltyMin));
                }
              }
              diet.setValue(id, newValue);
            }
            diet.setActive(originalDiet.isActive());

            if (!evt.isWasDeath()) {
              diet.setModifiers(originalDiet.getModifiers());
            }
          }));
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void playerRespawned(final PlayerEvent.PlayerRespawnEvent evt) {

    if (evt.getPlayer() instanceof ServerPlayerEntity) {
      ServerPlayerEntity player = (ServerPlayerEntity) evt.getPlayer();
      DietCapability.get(player).ifPresent(IDietTracker::sync);
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
  @SuppressWarnings("unused")
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

    if (!livingEntity.world.isRemote && livingEntity instanceof PlayerEntity) {
      Food food = stack.getItem().getFood();

      if (food != null) {
        DietCapability.get((PlayerEntity) livingEntity).ifPresent(diet -> diet.consume(stack));
      }
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void rightClickBlock(final PlayerInteractEvent.RightClickBlock evt) {
    PlayerEntity player = evt.getPlayer();
    World world = player.getEntityWorld();

    if (!world.isRemote() && !player.isSpectator()) {
      DietCapability.get(player).ifPresent(tracker -> {
        Hand hand = evt.getHand();
        ItemStack stack = player.getHeldItem(hand);
        BlockRayTraceResult rayTraceResult = evt.getHitVec();
        ItemUseContext itemusecontext = new ItemUseContext(player, hand, rayTraceResult);

        if (evt.getUseItem() != net.minecraftforge.eventbus.api.Event.Result.DENY) {
          ActionResultType result = stack.onItemUseFirst(itemusecontext);

          if (result != ActionResultType.PASS) {
            return;
          }
        }
        BlockPos pos = rayTraceResult.getPos();
        boolean flag =
            !player.getHeldItemMainhand().isEmpty() || !player.getHeldItemOffhand().isEmpty();
        boolean flag1 = (player.isSecondaryUseActive() && flag) &&
            !(player.getHeldItemMainhand().doesSneakBypassUse(world, pos, player) &&
                player.getHeldItemOffhand().doesSneakBypassUse(world, pos, player));

        if (evt.getUseBlock() == net.minecraftforge.eventbus.api.Event.Result.ALLOW ||
            (evt.getUseBlock() != net.minecraftforge.eventbus.api.Event.Result.DENY && !flag1)) {
          tracker.captureStack(
              player.world.getBlockState(pos).getPickBlock(rayTraceResult, world, pos, player));
        }
      });
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

      if (DietCapability.DIET_TRACKER != null) {
        return DietCapability.DIET_TRACKER.orEmpty(cap, this.capability);
      } else {
        DietMod.LOGGER.error("Missing Diet capability!");
        return LazyOptional.empty();
      }
    }

    @Override
    public INBT serializeNBT() {

      if (DietCapability.DIET_TRACKER != null) {
        return DietCapability.DIET_TRACKER.writeNBT(capability.orElse(EMPTY_TRACKER), null);
      } else {
        DietMod.LOGGER.error("Missing Diet capability!");
        return new CompoundNBT();
      }
    }

    @Override
    public void deserializeNBT(INBT nbt) {

      if (DietCapability.DIET_TRACKER != null) {
        DietCapability.DIET_TRACKER.readNBT(capability.orElse(EMPTY_TRACKER), null, nbt);
      } else {
        DietMod.LOGGER.error("Missing Diet capability!");
      }
    }
  }
}
