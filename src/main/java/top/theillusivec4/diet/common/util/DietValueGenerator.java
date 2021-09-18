package top.theillusivec4.diet.common.util;

import com.google.common.base.Stopwatch;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.common.group.DietGroups;
import top.theillusivec4.diet.common.network.DietNetwork;
import top.theillusivec4.diet.common.network.server.SPacketGeneratedValues;

public class DietValueGenerator {

  private static final Map<Item, Set<IDietGroup>> GENERATED = new HashMap<>();
  private static final Stopwatch STOPWATCH = Stopwatch.createUnstarted();
  private static final Tags.IOptionalNamedTag<Item> INGREDIENTS =
      ItemTags.createOptional(new ResourceLocation(DietMod.id("ingredients")));
  private static final Tags.IOptionalNamedTag<Item> SPECIAL_FOOD =
      ItemTags.createOptional(new ResourceLocation(DietMod.id("special_food")));

  public static void setup() {
    MinecraftForge.EVENT_BUS.addListener(DietValueGenerator::onDatapackSync);
    MinecraftForge.EVENT_BUS.addListener(DietValueGenerator::serverStarting);
  }

  private static void onDatapackSync(final OnDatapackSyncEvent evt) {

    if (evt.getPlayer() == null) {
      DietValueGenerator.reload(evt.getPlayerList().getServer());
    } else {
      DietValueGenerator.sync(evt.getPlayer());
    }
  }

  private static void serverStarting(final FMLServerStartingEvent evt) {
    DietValueGenerator.reload(evt.getServer());
  }

  public static void reload(MinecraftServer server) {
    DietMod.LOGGER.info("Generating diet values...");
    STOPWATCH.reset();
    STOPWATCH.start();
    DietMod.LOGGER.info("Finding ungrouped food items...");
    GENERATED.clear();
    RecipeManager recipeManager = server.getRecipeManager();
    Set<Item> ungroupedFood = new HashSet<>();
    Set<IDietGroup> groups = DietGroups.get();
    items:
    for (Item item : ForgeRegistries.ITEMS) {
      Food food = item.getFood();

      if ((food != null && food.getHealing() > 0) || SPECIAL_FOOD.contains(item)) {

        for (IDietGroup group : groups) {

          if (group.contains(new ItemStack(item))) {
            continue items;
          }
        }
        ungroupedFood.add(item);
      }
    }
    DietMod.LOGGER.info("Found {} ungrouped food items", ungroupedFood.size());
    DietMod.LOGGER.info("Finding recipes...");
    Map<Item, IRecipe<?>> recipes = new HashMap<>();
    List<IRecipe<?>> sortedRecipes =
        recipeManager.getRecipes().stream().sorted(Comparator.comparing(IRecipe::getId))
            .collect(Collectors.toList());
    Set<IRecipe<?>> processedRecipes = new HashSet<>();

    for (IRecipe<?> recipe : sortedRecipes) {
      ItemStack output = ItemStack.EMPTY;

      try {
        output = recipe.getRecipeOutput();
      } catch (Exception e) {
        DietMod.LOGGER.error("Diet was unable to process recipe: {}", recipe.getId());
      }
      Item item = output.getItem();

      if (ungroupedFood.contains(item) && !processedRecipes.contains(recipe)) {
        recipes.putIfAbsent(item, recipe);
        traverseRecipes(processedRecipes, recipes, sortedRecipes, recipe);
      }
    }
    DietMod.LOGGER.info("Found {} recipes to process", recipes.size());
    DietMod.LOGGER.info("Processing items...");
    Set<Item> processedItems = new HashSet<>();

    for (Map.Entry<Item, IRecipe<?>> entry : recipes.entrySet()) {
      Item item = entry.getKey();

      if (!processedItems.contains(item)) {
        traverseIngredients(processedItems, recipes, groups, item);
      }
    }
    DietMod.LOGGER.info("Processed {} items", processedItems.size());
    STOPWATCH.stop();
    DietMod.LOGGER.info("Generating diet values took {}", STOPWATCH);

    for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
      DietNetwork.sendGeneratedValuesS2C(player, GENERATED);
    }
  }

  public static void sync(ServerPlayerEntity player) {
    DietNetwork.sendGeneratedValuesS2C(player, GENERATED);
  }

  public static void sync(SPacketGeneratedValues packet) {
    GENERATED.clear();
    GENERATED.putAll(packet.generated);
  }

  private static void traverseRecipes(Set<IRecipe<?>> processed, Map<Item, IRecipe<?>> recipes,
                                      List<IRecipe<?>> allRecipes, IRecipe<?> recipe) {
    processed.add(recipe);

    for (Ingredient ingredient : recipe.getIngredients()) {
      Arrays.stream(ingredient.getMatchingStacks())
          .min(Comparator.comparing(ItemStack::getTranslationKey)).ifPresent(stack -> {

            for (IRecipe<?> entry : allRecipes) {
              ItemStack output = ItemStack.EMPTY;

              try {
                output = entry.getRecipeOutput();
              } catch (Exception e) {
                DietMod.LOGGER.error("Diet was unable to process recipe: {}", entry.getId());
              }
              Item item = output.getItem();

              if (item == stack.getItem() && !processed.contains(entry)) {
                recipes.putIfAbsent(item, entry);
                traverseRecipes(processed, recipes, allRecipes, entry);
              }
            }
          });
    }
  }

  private static Set<IDietGroup> traverseIngredients(Set<Item> processed,
                                                     Map<Item, IRecipe<?>> recipes,
                                                     Set<IDietGroup> groups, Item item) {
    processed.add(item);
    Set<IDietGroup> result = new HashSet<>();
    ItemStack fillerStack = new ItemStack(item);

    for (IDietGroup group : groups) {

      if (group.contains(fillerStack)) {
        result.add(group);
      }
    }

    if (result.isEmpty()) {
      IRecipe<?> recipe = recipes.get(item);

      if (recipe != null) {

        for (Ingredient ingredient : recipe.getIngredients()) {
          Arrays.stream(ingredient.getMatchingStacks())
              .min(Comparator.comparing(ItemStack::getTranslationKey)).ifPresent(stack -> {
                Item matchingItem = stack.getItem();

                if (!INGREDIENTS.contains(matchingItem)) {
                  Set<IDietGroup> fallback = GENERATED.get(matchingItem);

                  if (fallback != null) {
                    result.addAll(fallback);
                  } else if (!processed.contains(matchingItem)) {
                    Set<IDietGroup> found = new HashSet<>();

                    for (IDietGroup group : groups) {

                      if (group.contains(stack)) {
                        found.add(group);
                      }
                    }

                    if (found.isEmpty()) {
                      found.addAll(traverseIngredients(processed, recipes, groups, matchingItem));
                    }
                    GENERATED.putIfAbsent(matchingItem, found);
                    result.addAll(found);
                  }
                }
              });
        }
      }
    }
    GENERATED.putIfAbsent(item, result);
    return result;
  }

  public static Optional<Set<IDietGroup>> get(Item item) {
    return Optional.ofNullable(GENERATED.get(item));
  }
}
