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

package com.illusivesoulworks.diet.common.capability;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.illusivesoulworks.diet.api.DietApi;
import com.illusivesoulworks.diet.api.type.IDietAttribute;
import com.illusivesoulworks.diet.api.type.IDietCondition;
import com.illusivesoulworks.diet.api.type.IDietEffect;
import com.illusivesoulworks.diet.api.type.IDietGroup;
import com.illusivesoulworks.diet.api.type.IDietResult;
import com.illusivesoulworks.diet.api.type.IDietStatusEffect;
import com.illusivesoulworks.diet.api.type.IDietTracker;
import com.illusivesoulworks.diet.common.config.DietConfig;
import com.illusivesoulworks.diet.common.data.effect.DietEffect;
import com.illusivesoulworks.diet.common.data.effect.DietEffectsInfo;
import com.illusivesoulworks.diet.common.data.suite.DietSuites;
import com.illusivesoulworks.diet.common.util.DietResult;
import com.illusivesoulworks.diet.platform.Services;
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

public class PlayerDietTracker implements IDietTracker {

  private static final Map<MobEffect, Integer> EFFECT_DURATION = new HashMap<>();

  private final Player player;
  private final Map<String, Float> values = new HashMap<>();
  private final Map<Attribute, Set<UUID>> activeModifiers = new HashMap<>();
  private final Set<Item> eatenFood = new HashSet<>();

  private boolean active = true;
  private String suite = "builtin";

  private int prevFood = 0;
  private ItemStack captured = ItemStack.EMPTY;

  static {
    EFFECT_DURATION.put(MobEffects.NIGHT_VISION, 300);
    EFFECT_DURATION.put(MobEffects.CONFUSION, 300);
  }

  public PlayerDietTracker(Player player) {
    this.player = player;
    this.initSuite();
  }

  @Override
  public void initSuite() {
    Map<String, Float> oldValues = new HashMap<>(this.values);
    this.values.clear();
    DietSuites.getSuite(this.player.getLevel(), this.suite).ifPresent(suite -> {

      for (IDietGroup group : suite.getGroups()) {
        String name = group.getName();
        float amount;

        if (oldValues.containsKey(name)) {
          amount = oldValues.get(name);
        } else {
          amount = group.getDefaultValue();
        }
        this.values.put(name, Mth.clamp(amount, 0.0f, 1.0f));
      }
    });
  }

  @Override
  public void tick() {

    if (!this.player.getLevel().isClientSide()) {

      if (!this.player.isCreative() && this.active) {
        int currentFood = this.player.getFoodData().getFoodLevel();

        if (currentFood < this.prevFood && !Services.EVENT.fireApplyDecayEvent(this.player)) {
          this.decay(this.prevFood - currentFood);
        }
        this.prevFood = currentFood;
      }

      if (this.player.tickCount % 80 == 0) {
        for (Map.Entry<Attribute, Set<UUID>> entry : this.activeModifiers.entrySet()) {
          Set<UUID> uuids = entry.getValue();
          AttributeInstance att = this.player.getAttribute(entry.getKey());

          if (att != null) {

            for (UUID uuid : uuids) {
              att.removeModifier(uuid);
            }
          }
        }
        this.activeModifiers.clear();

        if (this.active) {
          this.applyEffects();
        }
      }
    }
  }

  @Override
  public void consume(ItemStack stack, int healing, float saturationModifier) {

    if (this.active && this.prevFood != this.player.getFoodData().getFoodLevel() &&
        !Services.EVENT.fireConsumeStackEvent(stack, this.player)) {
      IDietResult result =
          DietApi.getInstance().get(this.player, stack, healing, saturationModifier);

      if (result != DietResult.EMPTY) {
        this.addEaten(stack.getItem());
        this.apply(result);
      }
    }
  }

  @Override
  public void consume(List<ItemStack> stacks, int healing, float saturationModifier) {

    if (this.active && this.prevFood != this.player.getFoodData().getFoodLevel()) {
      IDietResult result =
          DietApi.getInstance().get(this.player, stacks, healing, saturationModifier);

      if (result != DietResult.EMPTY) {
        this.apply(result);
      }
    }
  }

  @Override
  public void consume(ItemStack stack) {

    if (this.active && this.prevFood != this.player.getFoodData().getFoodLevel() &&
        !Services.EVENT.fireConsumeStackEvent(stack, this.player)) {
      IDietResult result = DietApi.getInstance().get(this.player, stack);

      if (result != DietResult.EMPTY) {
        this.addEaten(stack.getItem());
        this.apply(result);
      }
    }
  }

  @Override
  public float getValue(String group) {
    return this.values.getOrDefault(group, 0.0f);
  }

  @Override
  public void setValue(String group, float amount) {
    this.values.replace(group, Mth.clamp(amount, 0.0f, 1.0f));
  }

  @Override
  public Map<String, Float> getValues() {
    return ImmutableMap.copyOf(this.values);
  }

  @Override
  public void setValues(Map<String, Float> entries) {

    for (Map.Entry<String, Float> entry : entries.entrySet()) {
      this.values.replace(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public String getSuite() {
    return this.suite;
  }

  @Override
  public void setSuite(String name) {

    if (!this.suite.equals(name)) {
      this.suite = name;
      this.initSuite();
    }
  }

  @Override
  public Map<Attribute, Set<UUID>> getModifiers() {
    return ImmutableMap.copyOf(this.activeModifiers);
  }

  @Override
  public void setModifiers(Map<Attribute, Set<UUID>> modifiers) {
    this.activeModifiers.clear();
    this.activeModifiers.putAll(modifiers);
  }

  @Override
  public boolean isActive() {
    return this.active;
  }

  @Override
  public void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public Player getPlayer() {
    return this.player;
  }

  private void applyEffects() {

    if (Services.EVENT.fireApplyEffectEvent(this.player)) {
      return;
    }
    DietEffectsInfo info = new DietEffectsInfo();
    DietSuites.getSuite(this.player.getLevel(), this.suite).ifPresent(suite -> {

      for (IDietEffect effect : suite.getEffects()) {
        boolean match = true;
        int multiplier = 0;

        for (IDietCondition condition : effect.getConditions()) {
          int matches = condition.getMatches(this.player, this.values);

          if (matches == 0) {
            match = false;
            break;
          }

          if (condition.getMatchMethod() == DietEffect.MatchMethod.EVERY) {
            multiplier += matches;
          }
        }

        if (match) {
          multiplier = Math.max(1, multiplier);

          for (IDietAttribute attribute : effect.getAttributes()) {
            AttributeInstance att = player.getAttribute(attribute.getAttribute());
            AttributeModifier mod = new AttributeModifier(effect.getUuid(), "Diet group effect",
                attribute.getBaseAmount() + ((multiplier - 1) * attribute.getIncrement()),
                attribute.getOperation());

            if (att != null && !att.hasModifier(mod)) {
              att.addPermanentModifier(mod);
              activeModifiers.computeIfAbsent(attribute.getAttribute(), k -> new HashSet<>())
                  .add(effect.getUuid());
              info.addModifier(attribute.getAttribute(), mod);
            }
          }

          for (IDietStatusEffect statusEffect : effect.getStatusEffects()) {
            int duration = EFFECT_DURATION.getOrDefault(statusEffect.getEffect(), 100);
            MobEffectInstance instance = new MobEffectInstance(statusEffect.getEffect(), duration,
                statusEffect.getBasePower() + ((multiplier - 1) * statusEffect.getIncrement()),
                true, false);
            player.addEffect(instance);
            info.addEffect(instance);
          }
        }
      }
    });

    if (player instanceof ServerPlayer) {
      Services.NETWORK.sendEffectsInfoS2C((ServerPlayer) player, info);
    }
  }

  private void decay(int foodDiff) {
    Map<String, Float> updated = new HashMap<>();
    long size = this.values.values().stream().filter(val -> val > 0.0f).count();

    if (size <= 0) {
      return;
    }
    float scale = ((float) foodDiff) / size;
    scale *= Math.pow(1.0f - DietConfig.SERVER.decayPenaltyPerGroup.get() / 100f, size - 1);
    float finalScale = scale;
    DietSuites.getSuite(this.player.getLevel(), this.suite).ifPresent(suite -> {

      for (IDietGroup group : suite.getGroups()) {
        String name = group.getName();
        float value = getValue(name);
        float decay = (float) (Math.exp(value) * finalScale * group.getDecayMultiplier() / 100.0f);

        if (decay > 0.0f) {
          value = Mth.clamp(value - decay, 0.0f, 1.0f);
          this.values.replace(name, value);
          updated.put(name, value);
        }
      }
    });

    if (!updated.isEmpty()) {
      sync(this.suite, updated);
    }
  }

  private void apply(IDietResult result) {
    Map<IDietGroup, Float> entries = result.get();
    Map<String, Float> applied = new HashMap<>();

    for (Map.Entry<IDietGroup, Float> entry : entries.entrySet()) {
      String name = entry.getKey().getName();
      float value = Mth.clamp(entry.getValue() + this.values.get(name), 0.0f, 1.0f);
      this.values.replace(name, value);
      applied.put(name, value);
    }

    if (!applied.isEmpty()) {
      sync(this.suite, applied);
    }
  }

  @Override
  public void sync() {
    sync(this.suite, this.values);
    sync(this.active);
    sync(this.eatenFood);
  }

  private void sync(Set<Item> values) {

    if (player instanceof ServerPlayer serverPlayer) {
      Services.NETWORK.sendEatenS2C(serverPlayer, values);
    }
  }

  private void sync(String suite, Map<String, Float> values) {

    if (player instanceof ServerPlayer serverPlayer) {
      Services.NETWORK.sendDietS2C(serverPlayer, suite, values);
    }
  }

  private void sync(boolean flag) {

    if (player instanceof ServerPlayer serverPlayer) {
      Services.NETWORK.sendActivationS2C(serverPlayer, flag);
    }
  }

  @Override
  public void captureStack(ItemStack stack) {
    this.captured = stack;
  }

  @Override
  public ItemStack getCapturedStack() {
    return this.captured;
  }

  @Override
  public void addEaten(Item item) {
    this.eatenFood.add(item);
    this.sync(Sets.newHashSet(item));
  }

  @Override
  public Set<Item> getEaten() {
    return this.eatenFood;
  }

  @Override
  public void setEaten(Set<Item> foods) {
    this.eatenFood.clear();
    this.eatenFood.addAll(foods);
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
            Objects.requireNonNull(Services.REGISTRY.getAttributeKey(modifier.getKey()))
                .toString()));
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
        ResourceLocation rl = Services.REGISTRY.getItemKey(item);

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

    DietSuites.getSuite(this.player.getLevel(), this.suite).ifPresent(suite -> {

      for (IDietGroup group : suite.getGroups()) {
        String name = group.getName();
        float amount = tag.contains(name) ? tag.getFloat(name) : group.getDefaultValue();
        groups.put(name, Mth.clamp(amount, 0.0f, 1.0f));
      }
    });
    ListTag list = tag.getList("Modifiers", Tag.TAG_COMPOUND);
    Map<Attribute, Set<UUID>> modifiers = new HashMap<>();

    for (int i = 0; i < list.size(); i++) {
      CompoundTag attributeTag = list.getCompound(i);
      Attribute att = Services.REGISTRY.getAttribute(
          new ResourceLocation(attributeTag.getString("AttributeName"))).orElse(null);

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
      Services.REGISTRY.getItem(rl).ifPresent(eaten::add);
    }
    this.setEaten(eaten);
    this.setModifiers(modifiers);
    this.setValues(groups);
    this.setActive(!tag.contains("Active") || tag.getBoolean("Active"));
  }

  @Override
  public void copy(Player oldPlayer, boolean wasDeath) {
    Services.CAPABILITY.get(this.player)
        .ifPresent(diet -> Services.CAPABILITY.get(oldPlayer).ifPresent(originalDiet -> {
          Map<String, Float> originalValues = originalDiet.getValues();
          DietSuites.getSuite(this.player.getLevel(), this.suite).ifPresent(suite -> {

            for (IDietGroup group : suite.getGroups()) {
              String id = group.getName();
              float originalValue = originalValues.getOrDefault(id, group.getDefaultValue());
              float newValue = originalValue;

              if (wasDeath) {

                if (DietConfig.SERVER.deathPenaltyMethod.get() ==
                    DietConfig.DeathPenaltyMethod.RESET) {
                  newValue = group.getDefaultValue();
                } else {
                  float loss = (float) DietConfig.SERVER.deathPenaltyLoss.get() / 100F;

                  if (DietConfig.SERVER.deathPenaltyMethod.get() ==
                      DietConfig.DeathPenaltyMethod.AMOUNT) {
                    newValue -= loss;
                  } else {
                    newValue *= (1 - loss);
                  }
                  newValue = Math.min(originalValue,
                      Math.max(newValue, (float) DietConfig.SERVER.deathPenaltyMin.get() / 100F));
                }
              }
              diet.setValue(id, newValue);
            }
          });
          diet.setActive(originalDiet.isActive());

          if (!wasDeath) {
            diet.setModifiers(originalDiet.getModifiers());
          }
        }));
  }
}
