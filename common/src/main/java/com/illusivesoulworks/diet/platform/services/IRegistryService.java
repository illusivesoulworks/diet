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

import com.mojang.brigadier.arguments.ArgumentType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
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

public interface IRegistryService {

  Optional<Item> getItem(ResourceLocation resourceLocation);

  ResourceLocation getItemKey(Item item);

  Optional<Attribute> getAttribute(ResourceLocation resourceLocation);

  ResourceLocation getAttributeKey(Attribute attribute);

  Optional<MobEffect> getStatusEffect(ResourceLocation resourceLocation);

  ResourceLocation getStatusEffectKey(MobEffect effect);

  FoodProperties getFoodProperties(ItemStack stack, Player player);

  BiFunction<Player, ItemStack, Triple<List<ItemStack>, Integer, Float>> getOverride(Item item);

  boolean isIngredient(ItemStack stack);

  boolean isSpecialFood(ItemStack stack);

  Collection<Item> getItems();

  Collection<Item> getTagItems(TagKey<Item> tagKey);

  ItemStack getPickStack(BlockState state, BlockHitResult result, Level world, BlockPos pos,
                         ServerPlayer player);

  ArgumentType<String> getModIdArgument();
}
