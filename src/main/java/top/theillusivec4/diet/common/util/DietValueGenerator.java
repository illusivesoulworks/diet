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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
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

  private static void serverStarting(final ServerStartingEvent evt) {
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
      FoodProperties food = item.getFoodProperties();

      if ((food != null && food.getNutrition() > 0) || SPECIAL_FOOD.contains(item)) {

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
    Map<Item, Recipe<?>> recipes = new HashMap<>();
    List<Recipe<?>> sortedRecipes =
        recipeManager.getRecipes().stream().sorted(Comparator.comparing(Recipe::getId))
            .collect(Collectors.toList());
    Set<Recipe<?>> processedRecipes = new HashSet<>();

    for (Recipe<?> recipe : sortedRecipes) {
      ItemStack output = ItemStack.EMPTY;

      try {
        output = recipe.getResultItem();
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

    for (Map.Entry<Item, Recipe<?>> entry : recipes.entrySet()) {
      Item item = entry.getKey();

      if (!processedItems.contains(item)) {
        traverseIngredients(processedItems, recipes, groups, item);
      }
    }
    DietMod.LOGGER.info("Processed {} items", processedItems.size());
    STOPWATCH.stop();
    DietMod.LOGGER.info("Generating diet values took {}", STOPWATCH);

    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      DietNetwork.sendGeneratedValuesS2C(player, GENERATED);
    }
  }

  public static void sync(ServerPlayer player) {
    DietNetwork.sendGeneratedValuesS2C(player, GENERATED);
  }

  public static void sync(SPacketGeneratedValues packet) {
    GENERATED.clear();
    GENERATED.putAll(packet.generated);
  }

  private static void traverseRecipes(Set<Recipe<?>> processed, Map<Item, Recipe<?>> recipes,
                                      List<Recipe<?>> allRecipes, Recipe<?> recipe) {
    processed.add(recipe);

    for (Ingredient ingredient : recipe.getIngredients()) {
      Arrays.stream(ingredient.getItems())
          .min(Comparator.comparing(ItemStack::getDescriptionId)).ifPresent(stack -> {

            for (Recipe<?> entry : allRecipes) {
              ItemStack output = ItemStack.EMPTY;

              try {
                output = entry.getResultItem();
              } catch (Exception e) {
                DietMod.LOGGER.error("Diet was unable to process recipe: {}", entry.getId());
              }

              // This shouldn't be necessary but some mods are violating the non-null contract, so
              // we have to check for it anyways
              if (output == null) {
                DietMod.LOGGER.error("Diet was unable to process recipe due to null output: {}", entry.getId());
                return;
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
                                                     Map<Item, Recipe<?>> recipes,
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
      Recipe<?> recipe = recipes.get(item);

      if (recipe != null) {

        for (Ingredient ingredient : recipe.getIngredients()) {
          Arrays.stream(ingredient.getItems())
              .min(Comparator.comparing(ItemStack::getDescriptionId)).ifPresent(stack -> {
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
