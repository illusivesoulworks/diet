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

package com.illusivesoulworks.diet.platform.services;

import com.illusivesoulworks.diet.DietCommonMod;
import com.illusivesoulworks.diet.common.ModIdArgument;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.block.BlockPickInteractionAware;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.tuple.Triple;

public class QuiltRegistryService implements IRegistryService {
  private static final TagKey<Item> INGREDIENTS =
      TagKey.create(Registries.ITEM, DietCommonMod.resource("ingredients"));
  private static final TagKey<Item> SPECIAL_FOOD =
      TagKey.create(Registries.ITEM, DietCommonMod.resource("special_food"));

  @Override
  public Optional<Item> getItem(ResourceLocation resourceLocation) {
    return Optional.of(BuiltInRegistries.ITEM.get(resourceLocation));
  }

  @Override
  public ResourceLocation getItemKey(Item item) {
    return BuiltInRegistries.ITEM.getKey(item);
  }

  @Override
  public Optional<Attribute> getAttribute(ResourceLocation resourceLocation) {
    return Optional.ofNullable(BuiltInRegistries.ATTRIBUTE.get(resourceLocation));
  }

  @Override
  public ResourceLocation getAttributeKey(Attribute attribute) {
    return BuiltInRegistries.ATTRIBUTE.getKey(attribute);
  }

  @Override
  public Optional<MobEffect> getStatusEffect(ResourceLocation resourceLocation) {
    return Optional.ofNullable(BuiltInRegistries.MOB_EFFECT.get(resourceLocation));
  }

  @Override
  public ResourceLocation getStatusEffectKey(MobEffect effect) {
    return BuiltInRegistries.MOB_EFFECT.getKey(effect);
  }

  @Override
  public FoodProperties getFoodProperties(ItemStack stack, Player player) {
    return stack.getItem().getFoodProperties();
  }

  @Override
  public BiFunction<Player, ItemStack, Triple<List<ItemStack>, Integer, Float>> getOverride(
      Item item) {
    return null;
  }

  @Override
  public boolean isIngredient(ItemStack stack) {
    return stack.is(INGREDIENTS);
  }

  @Override
  public boolean isSpecialFood(ItemStack stack) {
    return stack.is(SPECIAL_FOOD);
  }

  @Override
  public Collection<Item> getItems() {
    return BuiltInRegistries.ITEM.stream().toList();
  }

  @Override
  public Collection<Item> getTagItems(TagKey<Item> tagKey) {
    return BuiltInRegistries.ITEM.getTag(tagKey).stream().flatMap(HolderSet.ListBacked::stream)
        .map(Holder::value).collect(Collectors.toSet());
  }

  @Override
  public ItemStack getPickStack(BlockState state, BlockHitResult result, Level world, BlockPos pos,
                                ServerPlayer player) {

    if (state.getBlock() instanceof BlockPickInteractionAware) {
      return (((BlockPickInteractionAware) state.getBlock()).getPickedStack(state, world, pos,
          player, result));
    }
    return state.getBlock().getCloneItemStack(world, pos, state);
  }

  @Override
  public ArgumentType<String> getModIdArgument() {
    return ModIdArgument.modIdArgument();
  }
}
