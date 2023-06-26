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

package com.illusivesoulworks.diet.common.data.suite;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.api.type.IDietAttribute;
import com.illusivesoulworks.diet.api.type.IDietCondition;
import com.illusivesoulworks.diet.api.type.IDietStatusEffect;
import com.illusivesoulworks.diet.api.type.IDietSuite;
import com.illusivesoulworks.diet.common.data.effect.DietEffect;
import com.illusivesoulworks.diet.common.data.group.DietGroups;
import com.illusivesoulworks.diet.platform.Services;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;

public class DietSuites extends SimpleJsonResourceReloadListener {

  private static final Gson GSON =
      (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
  private static final String UUID_PREFIX = "ea4130c8-9065-48a6-9207-ddc020fb9fc8";

  public static final DietSuites SERVER = Services.CAPABILITY.getSuitesListener();
  public static final DietSuites CLIENT = Services.CAPABILITY.getSuitesListener();

  private Map<String, IDietSuite> suites = new HashMap<>();

  private int uuidSuffix = 0;

  public DietSuites() {
    super(GSON, "diet/suites");
  }

  public static Optional<IDietSuite> getSuite(Level level, String name) {
    DietSuites instance = level.isClientSide() ? CLIENT : SERVER;
    return Optional.ofNullable(instance.suites.get(name));
  }

  public CompoundTag save() {
    CompoundTag tag = new CompoundTag();

    for (Map.Entry<String, IDietSuite> entry : this.suites.entrySet()) {
      tag.put(entry.getKey(), entry.getValue().save());
    }
    return tag;
  }

  public void load(CompoundTag tag) {
    Map<String, IDietSuite> loaded = new HashMap<>();

    for (String key : tag.getAllKeys()) {
      loaded.put(key, DietSuite.load((CompoundTag) Objects.requireNonNull(tag.get(key))));
    }
    this.suites = loaded;
  }

  public void sync(ServerPlayer player) {
    Services.NETWORK.sendDietSuitesS2C(player, this.save());
  }

  @Override
  protected void apply(@Nonnull Map<ResourceLocation, JsonElement> object,
                       @Nonnull ResourceManager resourceManager,
                       @Nonnull ProfilerFiller profilerFiller) {
    Map<String, DietSuite.Builder> map = new HashMap<>();
    uuidSuffix = 0;

    for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
      ResourceLocation resourcelocation = entry.getKey();

      if (resourcelocation.getNamespace().equals(DietConstants.MOD_ID)) {

        try {
          buildSuite(map.computeIfAbsent(resourcelocation.getPath(), DietSuite.Builder::new),
              GsonHelper.convertToJsonObject(entry.getValue(), "top element"));
        } catch (IllegalArgumentException | JsonParseException e) {
          DietConstants.LOG.error("Parsing error loading diet suite {}", resourcelocation, e);
        }
      }
    }

    for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
      ResourceLocation resourcelocation = entry.getKey();

      if (resourcelocation.getPath().startsWith("_") ||
          resourcelocation.getNamespace().equals(DietConstants.MOD_ID)) {
        continue;
      }

      try {
        buildSuite(map.computeIfAbsent(resourcelocation.getPath(), DietSuite.Builder::new),
            GsonHelper.convertToJsonObject(entry.getValue(), "top element"));
      } catch (IllegalArgumentException | JsonParseException e) {
        DietConstants.LOG.error("Parsing error loading diet suite {}", resourcelocation, e);
      }
    }
    suites = map.entrySet().stream()
        .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> entry.getValue().build()));
    DietConstants.LOG.info("Loaded {} diet suites", map.size());
  }

  private void buildSuite(DietSuite.Builder builder, JsonObject topElement) {
    boolean replace = GsonHelper.getAsBoolean(topElement, "replace", false);
    JsonArray empty = new JsonArray();
    JsonArray groups = GsonHelper.getAsJsonArray(topElement, "groups", empty);

    if (replace) {
      builder.clear();
    }

    for (JsonElement group : groups) {
      DietGroups.SERVER.getGroup(group.getAsString()).ifPresent(builder::group);
    }
    JsonArray effects = GsonHelper.getAsJsonArray(topElement, "effects", empty);

    for (JsonElement effect : effects) {
      JsonObject effectObject = effect.getAsJsonObject();
      JsonArray conditions = GsonHelper.getAsJsonArray(effectObject, "conditions", empty);
      JsonArray attributes = GsonHelper.getAsJsonArray(effectObject, "attributes", empty);
      JsonArray statusEffects = GsonHelper.getAsJsonArray(effectObject, "status_effects", empty);

      if (conditions.size() == 0) {
        throw new IllegalArgumentException("Conditions cannot be empty!");
      }

      if (attributes.size() == 0 && statusEffects.size() == 0) {
        throw new IllegalArgumentException("Both attributes and status_effects cannot be empty!");
      }
      List<IDietCondition> finalConditions = new ArrayList<>();

      for (JsonElement condition : conditions) {
        JsonObject conditionObject = condition.getAsJsonObject();
        JsonArray conditionGroups = GsonHelper.getAsJsonArray(conditionObject, "groups", empty);
        Set<String> set = new HashSet<>();

        for (JsonElement conditionGroup : conditionGroups) {
          set.add(conditionGroup.getAsString());
        }
        String match = GsonHelper.getAsString(conditionObject, "match", "any");
        float above = GsonHelper.getAsFloat(conditionObject, "above", 0.0f);
        float below = GsonHelper.getAsFloat(conditionObject, "below", 1.0f);
        finalConditions.add(new DietEffect.DietCondition(set,
            DietEffect.MatchMethod.findOrDefault(match, DietEffect.MatchMethod.ANY), above, below));
      }
      List<IDietAttribute> finalAttributes = new ArrayList<>();

      for (JsonElement attribute : attributes) {
        JsonObject attributeObject = attribute.getAsJsonObject();

        if (!attributeObject.has("name")) {
          throw new IllegalArgumentException("Attribute requires a name!");
        }
        String name = GsonHelper.getAsString(attributeObject, "name");
        Services.REGISTRY.getAttribute(new ResourceLocation(name)).ifPresentOrElse(att -> {
          float amount = GsonHelper.getAsFloat(attributeObject, "amount", 1.0f);
          AttributeModifier.Operation op =
              getOperation(GsonHelper.getAsString(attributeObject, "operation", ""));
          float increment = GsonHelper.getAsFloat(attributeObject, "increment", amount);
          finalAttributes.add(new DietEffect.DietAttribute(att, op, amount, increment));
        }, () -> {
          throw new IllegalArgumentException("Attribute " + name + " does not exist!");
        });
      }
      List<IDietStatusEffect> finalStatusEffects = new ArrayList<>();

      for (JsonElement statusEffect : statusEffects) {
        JsonObject statusEffectObject = statusEffect.getAsJsonObject();

        if (!statusEffectObject.has("name")) {
          throw new IllegalArgumentException("Status effect requires a name!");
        }
        String name = GsonHelper.getAsString(statusEffectObject, "name");
        Services.REGISTRY.getStatusEffect(new ResourceLocation(name)).ifPresentOrElse(eff -> {
          int power = GsonHelper.getAsInt(statusEffectObject, "power", 0);
          int increment = GsonHelper.getAsInt(statusEffectObject, "increment", 1);
          finalStatusEffects.add(new DietEffect.DietStatusEffect(eff, power, increment));
        }, () -> {
          throw new IllegalArgumentException("Attribute " + name + " does not exist!");
        });
      }
      UUID uuid = UUID.nameUUIDFromBytes((UUID_PREFIX + uuidSuffix).getBytes());
      uuidSuffix++;
      builder.effect(new DietEffect(uuid, finalAttributes, finalStatusEffects, finalConditions));
    }
  }

  private static AttributeModifier.Operation getOperation(String name) {
    if (name.equals("multiply_total")) {
      return AttributeModifier.Operation.MULTIPLY_TOTAL;
    } else if (name.equals("multiply_base")) {
      return AttributeModifier.Operation.MULTIPLY_BASE;
    }
    return AttributeModifier.Operation.ADDITION;
  }
}
