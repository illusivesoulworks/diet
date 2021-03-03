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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.diet.api.IDietTracker;
import top.theillusivec4.diet.common.group.DietGroup;
import top.theillusivec4.diet.common.group.DietGroups;

public class DietTrackerCapability {

  public static void setup() {
    CapabilityManager.INSTANCE.register(IDietTracker.class,
        new Capability.IStorage<IDietTracker>() {

          @Override
          public INBT writeNBT(Capability<IDietTracker> capability, IDietTracker instance,
                               Direction side) {
            CompoundNBT tag = new CompoundNBT();

            for (Map.Entry<String, Float> group : instance.getValues().entrySet()) {
              tag.putFloat(group.getKey(), group.getValue());
            }
            ListNBT list = new ListNBT();

            for (Map.Entry<Attribute, Set<UUID>> modifier : instance.getModifiers().entrySet()) {
              CompoundNBT attributeTag = new CompoundNBT();
              attributeTag.put("AttributeName", StringNBT.valueOf(
                  Objects.requireNonNull(modifier.getKey().getRegistryName()).toString()));
              ListNBT uuids = new ListNBT();

              for (UUID uuid : modifier.getValue()) {
                uuids.add(StringNBT.valueOf(uuid.toString()));
              }
              attributeTag.put("UUIDs", uuids);
              list.add(attributeTag);
            }
            tag.put("Modifiers", list);
            tag.putBoolean("Active", instance.isActive());
            return tag;
          }

          @Override
          public void readNBT(Capability<IDietTracker> capability, IDietTracker instance,
                              Direction side, INBT nbt) {
            Map<String, Float> groups = new HashMap<>();
            CompoundNBT tag = (CompoundNBT) nbt;

            for (DietGroup group : DietGroups.get()) {
              String name = group.getName();
              float amount = tag.contains(name) ? tag.getFloat(name) : group.getDefaultValue();
              groups.put(name, MathHelper.clamp(amount, 0.0f, 1.0f));
            }
            ListNBT list = tag.getList("Modifiers", Constants.NBT.TAG_COMPOUND);
            Map<Attribute, Set<UUID>> modifiers = new HashMap<>();

            for (int i = 0; i < list.size(); i++) {
              CompoundNBT attributeTag = list.getCompound(i);
              Attribute att = ForgeRegistries.ATTRIBUTES
                  .getValue(new ResourceLocation(attributeTag.getString("AttributeName")));

              if (att != null) {
                Set<UUID> uuids = new HashSet<>();
                ListNBT uuidList = attributeTag.getList("UUIDs", Constants.NBT.TAG_STRING);

                for (int j = 0; j < uuidList.size(); j++) {
                  uuids.add(UUID.fromString(uuidList.getString(j)));
                }
                modifiers.put(att, uuids);
              }
            }
            instance.setModifiers(modifiers);
            instance.setValues(groups);
            instance.setActive(tag.getBoolean("Active"));
          }
        }, EmptyDietTracker::new);
  }

  public static class EmptyDietTracker implements IDietTracker {

    @Override
    public void tick() {
      // NO-OP
    }

    @Override
    public void consume(ItemStack stack) {
      // NO-OP
    }

    @Override
    public void consume(ItemStack stack, int healing, float saturationModifier) {

    }

    @Override
    public float getValue(String group) {
      return 0;
    }

    @Override
    public void setValue(String group, float amount) {
      // NO-OP
    }

    @Override
    public Map<String, Float> getValues() {
      return null;
    }

    @Override
    public void setValues(Map<String, Float> groups) {
      // NO-OP
    }

    @Override
    public Map<Attribute, Set<UUID>> getModifiers() {
      return null;
    }

    @Override
    public void setModifiers(Map<Attribute, Set<UUID>> modifiers) {
      // NO-OP
    }

    @Override
    public boolean isActive() {
      return false;
    }

    @Override
    public void setActive(boolean active) {
      // NO-OP
    }

    @Override
    public PlayerEntity getPlayer() {
      return null;
    }

    @Override
    public void sync() {
      // NO-OP
    }

    @Override
    public void captureStack(ItemStack stack) {

    }

    @Override
    public ItemStack getCapturedStack() {
      return null;
    }
  }
}
