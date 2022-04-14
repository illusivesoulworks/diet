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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.diet.api.DietApi;
import top.theillusivec4.diet.api.DietEvent;
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.api.IDietResult;
import top.theillusivec4.diet.api.IDietTracker;
import top.theillusivec4.diet.common.config.DietServerConfig;
import top.theillusivec4.diet.common.effect.DietEffect;
import top.theillusivec4.diet.common.effect.DietEffects;
import top.theillusivec4.diet.common.effect.DietEffectsInfo;
import top.theillusivec4.diet.common.group.DietGroups;
import top.theillusivec4.diet.common.network.DietNetwork;
import top.theillusivec4.diet.common.util.DietResult;

public class PlayerDietTracker implements IDietTracker {

  private static final Map<MobEffect, Integer> EFFECT_DURATION = new HashMap<>();

  private final Player player;
  private final Map<String, Float> values = new HashMap<>();
  private final Map<Attribute, Set<UUID>> activeModifiers = new HashMap<>();
  private final Set<Item> eatenFood = new HashSet<>();

  private boolean active = true;

  private int prevFood = 0;
  private ItemStack captured = ItemStack.EMPTY;

  static {
    EFFECT_DURATION.put(MobEffects.NIGHT_VISION, 300);
    EFFECT_DURATION.put(MobEffects.CONFUSION, 300);
  }

  public PlayerDietTracker(Player playerIn) {
    player = playerIn;

    for (IDietGroup group : DietGroups.get()) {
      String name = group.getName();
      float amount = group.getDefaultValue();
      values.put(name, Mth.clamp(amount, 0.0f, 1.0f));
    }
  }

  @Override
  public void tick() {

    if (player instanceof ServerPlayer) {

      if (!player.isCreative() && active) {
        int currentFood = player.getFoodData().getFoodLevel();

        if (currentFood < prevFood &&
            !MinecraftForge.EVENT_BUS.post(new DietEvent.ApplyDecay(player))) {
          decay(prevFood - currentFood);
        }
        prevFood = currentFood;
      }

      if (player.tickCount % 80 == 0) {
        for (Map.Entry<Attribute, Set<UUID>> entry : activeModifiers.entrySet()) {
          Set<UUID> uuids = entry.getValue();
          AttributeInstance att = player.getAttribute(entry.getKey());

          if (att != null) {

            for (UUID uuid : uuids) {
              att.removeModifier(uuid);
            }
          }
        }
        activeModifiers.clear();

        if (active) {
          applyEffects();
        }
      }
    }
  }

  @Override
  public void consume(ItemStack stack, int healing, float saturationModifier) {

    if (active && prevFood != player.getFoodData().getFoodLevel() &&
        !MinecraftForge.EVENT_BUS.post(new DietEvent.ConsumeItemStack(stack, player))) {
      IDietResult result = DietApi.getInstance().get(player, stack, healing, saturationModifier);

      if (result != DietResult.EMPTY) {
        addEaten(stack.getItem());
        apply(result);
      }
    }
  }

  @Override
  public void consume(List<ItemStack> stacks, int healing, float saturationModifier) {

    if (active && prevFood != player.getFoodData().getFoodLevel()) {
      IDietResult result = DietApi.getInstance().get(player, stacks, healing, saturationModifier);

      if (result != DietResult.EMPTY) {
        apply(result);
      }
    }
  }

  @Override
  public void consume(ItemStack stack) {

    if (active && prevFood != player.getFoodData().getFoodLevel() &&
        !MinecraftForge.EVENT_BUS.post(new DietEvent.ConsumeItemStack(stack, player))) {
      IDietResult result = DietApi.getInstance().get(player, stack);

      if (result != DietResult.EMPTY) {
        addEaten(stack.getItem());
        apply(result);
      }
    }
  }

  @Override
  public float getValue(String group) {
    return values.getOrDefault(group, 0.0f);
  }

  @Override
  public void setValue(String group, float amount) {
    values.put(group, Mth.clamp(amount, 0.0f, 1.0f));
  }

  @Override
  public Map<String, Float> getValues() {
    return ImmutableMap.copyOf(values);
  }

  @Override
  public void setValues(Map<String, Float> entries) {
    values.clear();
    values.putAll(entries);
  }

  @Override
  public Map<Attribute, Set<UUID>> getModifiers() {
    return ImmutableMap.copyOf(activeModifiers);
  }

  @Override
  public void setModifiers(Map<Attribute, Set<UUID>> modifiers) {
    activeModifiers.clear();
    activeModifiers.putAll(modifiers);
  }

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public Player getPlayer() {
    return player;
  }

  private void applyEffects() {

    if (MinecraftForge.EVENT_BUS.post(new DietEvent.ApplyEffect(getPlayer()))) {
      return;
    }

    List<DietEffect> effects = DietEffects.get();
    DietEffectsInfo info = new DietEffectsInfo();

    for (DietEffect effect : effects) {
      boolean match = true;
      int multiplier = 0;

      for (DietEffect.Condition condition : effect.conditions) {
        int matches = condition.getMatches(player, values);

        if (matches == 0) {
          match = false;
          break;
        }

        if (condition.match == DietEffect.MatchMethod.EVERY) {
          multiplier += matches;
        }
      }

      if (match) {
        multiplier = Math.max(1, multiplier);

        for (DietEffect.DietAttribute attribute : effect.attributes) {
          AttributeInstance att = player.getAttribute(attribute.attribute);
          AttributeModifier mod =
              new AttributeModifier(effect.uuid, "Diet group effect", attribute.amount * multiplier,
                  attribute.operation);

          if (att != null && !att.hasModifier(mod)) {
            att.addPermanentModifier(mod);
            activeModifiers.computeIfAbsent(attribute.attribute, k -> new HashSet<>())
                .add(effect.uuid);
            info.addModifier(attribute.attribute, mod);
          }
        }

        for (DietEffect.DietStatusEffect statusEffect : effect.statusEffects) {
          int duration = EFFECT_DURATION.getOrDefault(statusEffect.effect, 100);
          MobEffectInstance instance =
              new MobEffectInstance(statusEffect.effect, duration, statusEffect.power * multiplier,
                  true, false);
          player.addEffect(instance);
          info.addEffect(instance);
        }
      }
    }

    if (player instanceof ServerPlayer) {
      DietNetwork.sendEffectsInfoS2C((ServerPlayer) player, info);
    }
  }

  private void decay(int foodDiff) {
    Map<String, Float> updated = new HashMap<>();
    long size = values.values().stream().filter(val -> val > 0.0f).count();

    if (size <= 0) {
      return;
    }
    float scale = ((float) foodDiff) / size;
    scale *= Math.pow(1.0f - DietServerConfig.decayPenaltyPerGroup, size - 1);

    for (IDietGroup group : DietGroups.get()) {
      String name = group.getName();
      float value = getValue(name);
      float decay = (float) (Math.exp(value) * scale * group.getDecayMultiplier() / 100.0f);

      if (decay > 0.0f) {
        value = Mth.clamp(value - decay, 0.0f, 1.0f);
        values.replace(name, value);
        updated.put(name, value);
      }
    }

    if (!updated.isEmpty()) {
      sync(updated);
    }
  }

  private void apply(IDietResult result) {
    Map<IDietGroup, Float> entries = result.get();
    Map<String, Float> applied = new HashMap<>();

    for (Map.Entry<IDietGroup, Float> entry : entries.entrySet()) {
      String name = entry.getKey().getName();
      float value = Mth.clamp(entry.getValue() + values.get(name), 0.0f, 1.0f);
      values.replace(name, value);
      applied.put(name, value);
    }

    if (!applied.isEmpty()) {
      sync(applied);
    }
  }

  @Override
  public void sync() {
    sync(values);
    sync(active);
    sync(eatenFood);
  }

  private void sync(Set<Item> values) {

    if (player instanceof ServerPlayer) {
      DietNetwork.sendEatenS2C((ServerPlayer) player, values);
    }
  }

  private void sync(Map<String, Float> values) {

    if (player instanceof ServerPlayer) {
      DietNetwork.sendDietS2C((ServerPlayer) player, values);
    }
  }

  private void sync(boolean flag) {

    if (player instanceof ServerPlayer) {
      DietNetwork.sendActivationS2C((ServerPlayer) player, flag);
    }
  }

  @Override
  public void captureStack(ItemStack stack) {
    captured = stack;
  }

  @Override
  public ItemStack getCapturedStack() {
    return captured;
  }

  @Override
  public void addEaten(Item item) {
    eatenFood.add(item);
    sync(Sets.newHashSet(item));
  }

  @Override
  public Set<Item> getEaten() {
    return eatenFood;
  }

  @Override
  public void setEaten(Set<Item> foods) {
    eatenFood.clear();
    eatenFood.addAll(foods);
  }

  @Override
  public void save(CompoundTag tag) {
    Map<String, Float> values = this.getValues();

    if (values != null) {

      for (Map.Entry<String, Float> group : values.entrySet()) {
        tag.putFloat(group.getKey(), group.getValue());
      }
    }
    ListTag list = new ListTag();
    Map<Attribute, Set<UUID>> modifiers = this.getModifiers();

    if (modifiers != null) {

      for (Map.Entry<Attribute, Set<UUID>> modifier : modifiers.entrySet()) {
        CompoundTag attributeTag = new CompoundTag();
        attributeTag.put("AttributeName", StringTag.valueOf(
            Objects.requireNonNull(modifier.getKey().getRegistryName()).toString()));
        ListTag uuids = new ListTag();

        for (UUID uuid : modifier.getValue()) {
          uuids.add(StringTag.valueOf(uuid.toString()));
        }
        attributeTag.put("UUIDs", uuids);
        list.add(attributeTag);
      }
    }
    tag.put("Modifiers", list);
    list = new ListTag();
    Set<Item> eaten = this.getEaten();

    if (eaten != null) {

      for (Item item : eaten) {
        ResourceLocation rl = item.getRegistryName();

        if (rl != null) {
          list.add(StringTag.valueOf(rl.toString()));
        }
      }
    }
    tag.put("Eaten", list);
    tag.putBoolean("Active", this.isActive());
  }

  @Override
  public void load(CompoundTag tag) {
    Map<String, Float> groups = new HashMap<>();

    for (IDietGroup group : DietGroups.get()) {
      String name = group.getName();
      float amount = tag.contains(name) ? tag.getFloat(name) : group.getDefaultValue();
      groups.put(name, Mth.clamp(amount, 0.0f, 1.0f));
    }
    ListTag list = tag.getList("Modifiers", Tag.TAG_COMPOUND);
    Map<Attribute, Set<UUID>> modifiers = new HashMap<>();

    for (int i = 0; i < list.size(); i++) {
      CompoundTag attributeTag = list.getCompound(i);
      Attribute att = ForgeRegistries.ATTRIBUTES
          .getValue(new ResourceLocation(attributeTag.getString("AttributeName")));

      if (att != null) {
        Set<UUID> uuids = new HashSet<>();
        ListTag uuidList = attributeTag.getList("UUIDs", Tag.TAG_STRING);

        for (int j = 0; j < uuidList.size(); j++) {
          uuids.add(UUID.fromString(uuidList.getString(j)));
        }
        modifiers.put(att, uuids);
      }
    }
    list = tag.getList("Eaten", Tag.TAG_STRING);
    Set<Item> eaten = new HashSet<>();

    for (int i = 0; i < list.size(); i++) {
      String s = list.getString(i);
      ResourceLocation rl = new ResourceLocation(s);
      Item item = ForgeRegistries.ITEMS.getValue(rl);

      if (item != null) {
        eaten.add(item);
      }
    }
    this.setEaten(eaten);
    this.setModifiers(modifiers);
    this.setValues(groups);
    this.setActive(!tag.contains("Active") || tag.getBoolean("Active"));
  }
}
