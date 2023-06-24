package com.illusivesoulworks.diet.platform.services;

import com.illusivesoulworks.diet.DietCommonMod;
import com.illusivesoulworks.diet.common.util.DietOverride;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;
import org.apache.commons.lang3.tuple.Triple;

public class ForgeRegistryService implements IRegistryService {

  private static final ITagManager<Item> ITEM_TAGS = ForgeRegistries.ITEMS.tags();
  private static final TagKey<Item> INGREDIENTS = ITEM_TAGS != null ?
      ITEM_TAGS.createTagKey(DietCommonMod.resource("ingredients")) : null;
  private static final TagKey<Item> SPECIAL_FOOD = ITEM_TAGS != null ?
      ITEM_TAGS.createTagKey(DietCommonMod.resource("special_food")) : null;

  @Override
  public Optional<Item> getItem(ResourceLocation resourceLocation) {
    return Optional.ofNullable(ForgeRegistries.ITEMS.getValue(resourceLocation));
  }

  @Override
  public ResourceLocation getItemKey(Item item) {
    return ForgeRegistries.ITEMS.getKey(item);
  }

  @Override
  public Optional<Attribute> getAttribute(ResourceLocation resourceLocation) {
    return Optional.ofNullable(ForgeRegistries.ATTRIBUTES.getValue(resourceLocation));
  }

  @Override
  public ResourceLocation getAttributeKey(Attribute attribute) {
    return ForgeRegistries.ATTRIBUTES.getKey(attribute);
  }

  @Override
  public Optional<MobEffect> getStatusEffect(ResourceLocation resourceLocation) {
    return Optional.ofNullable(ForgeRegistries.MOB_EFFECTS.getValue(resourceLocation));
  }

  @Override
  public ResourceLocation getStatusEffectKey(MobEffect effect) {
    return ForgeRegistries.MOB_EFFECTS.getKey(effect);
  }

  @Override
  public FoodProperties getFoodProperties(ItemStack stack, Player player) {
    return stack.getFoodProperties(player);
  }

  @Override
  public BiFunction<Player, ItemStack, Triple<List<ItemStack>, Integer, Float>> getOverride(
      Item item) {
    return DietOverride.get(item);
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
    return ForgeRegistries.ITEMS.getValues();
  }

  @Override
  public Collection<Item> getTagItems(TagKey<Item> tagKey) {
    return Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(tagKey).stream().toList();
  }

  @Override
  public ItemStack getPickStack(BlockState state, BlockHitResult result, Level world, BlockPos pos,
                                ServerPlayer player) {
    return state.getCloneItemStack(result, world, pos, player);
  }
}
