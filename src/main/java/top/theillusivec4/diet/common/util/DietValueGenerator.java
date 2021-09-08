package top.theillusivec4.diet.common.util;

import com.google.common.base.Stopwatch;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.common.group.DietGroups;

public class DietValueGenerator {

  private static final Map<Item, Set<IDietGroup>> GENERATED = new HashMap<>();
  private static final Stopwatch STOPWATCH = Stopwatch.createUnstarted();
  private static Tags.IOptionalNamedTag<Item> INGREDIENTS;

  public static void setup() {

    if (INGREDIENTS == null) {
      INGREDIENTS = ForgeTagHandler.createOptionalTag(ForgeRegistries.ITEMS,
          new ResourceLocation(DietMod.MOD_ID, "ingredients"), new HashSet<>());
    }
  }

  public static void reload(MinecraftServer server) {
    DietMod.LOGGER.info("Generating diet values...");
    STOPWATCH.reset();
    STOPWATCH.start();
    GENERATED.clear();
    RecipeManager recipeManager = server.getRecipeManager();
    Set<Item> foodWithoutTags = new HashSet<>();
    Set<IDietGroup> groups = DietGroups.get();

    for (Item item : ForgeRegistries.ITEMS) {
      Food food = item.getFood();

      if (food != null && food.getHealing() > 0) {
        boolean hasGroup = false;

        for (IDietGroup group : groups) {

          if (group.contains(new ItemStack(item))) {
            hasGroup = true;
            break;
          }
        }

        if (!hasGroup) {
          foodWithoutTags.add(item);
        }
      }
    }
    DietMod.LOGGER.info("Found {} ungrouped food items: Processing recipes...",
        foodWithoutTags.size());
    Map<Item, IRecipe<?>> recipes = new HashMap<>();
    Set<IRecipe<?>> sortedRecipes =
        recipeManager.getRecipes().stream().sorted(Comparator.comparing(IRecipe::getId)).collect(
            Collectors.toCollection(LinkedHashSet::new));
    Set<IRecipe<?>> processedRecipes = new HashSet<>();

    for (IRecipe<?> recipe : sortedRecipes) {
      ItemStack output = recipe.getRecipeOutput();
      Item item = output.getItem();

      if (foodWithoutTags.contains(item) && !processedRecipes.contains(recipe)) {
        recipes.putIfAbsent(item, recipe);
        traverseRecipes(processedRecipes, recipes, sortedRecipes, recipe);
      }
    }
    Set<Item> processedItems = new HashSet<>();

    for (Map.Entry<Item, IRecipe<?>> entry : recipes.entrySet()) {
      Item item = entry.getKey();

      if (!processedItems.contains(item)) {
        traverseIngredients(processedItems, recipes, groups, item);
      }
    }
    STOPWATCH.stop();
    DietMod.LOGGER.info("Processed {} recipes: {} took {}", processedRecipes.size(),
        DietMod.MOD_ID, STOPWATCH);
  }

  private static void traverseRecipes(Set<IRecipe<?>> processed, Map<Item, IRecipe<?>> recipes,
                                      Set<IRecipe<?>> allRecipes, IRecipe<?> recipe) {
    processed.add(recipe);

    for (Ingredient ingredient : recipe.getIngredients()) {

      for (ItemStack matchingStack : ingredient.getMatchingStacks()) {

        for (IRecipe<?> entry : allRecipes) {
          ItemStack output = entry.getRecipeOutput();
          Item item = output.getItem();

          if (item == matchingStack.getItem() && !processed.contains(entry)) {
            recipes.putIfAbsent(item, entry);
            traverseRecipes(processed, recipes, allRecipes, entry);
          }
        }
      }
    }
  }

  private static Set<IDietGroup> traverseIngredients(Set<Item> processed,
                                                     Map<Item, IRecipe<?>> recipes,
                                                     Set<IDietGroup> groups, Item item) {
    processed.add(item);
    Set<IDietGroup> result = new HashSet<>();
    IRecipe<?> recipe = recipes.get(item);

    if (recipe != null) {

      for (Ingredient ingredient : recipe.getIngredients()) {

        for (ItemStack matchingStack : ingredient.getMatchingStacks()) {
          Item matchingItem = matchingStack.getItem();
          Set<IDietGroup> fallback = GENERATED.get(matchingItem);

          if (fallback != null) {
            result.addAll(fallback);
          } else if (!processed.contains(matchingItem)) {

            if (TagCollectionManager.getManager().getItemTags().getDirectIdFromTag(INGREDIENTS) != null &&
                !INGREDIENTS.isDefaulted() && INGREDIENTS.contains(matchingItem)) {
              processed.add(matchingItem);
              GENERATED.putIfAbsent(matchingItem, new HashSet<>());
              continue;
            }
            Set<IDietGroup> found = new HashSet<>();

            for (IDietGroup group : groups) {

              if (group.contains(matchingStack)) {
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
      }
    }
    GENERATED.putIfAbsent(item, result);
    return result;
  }

  public static Optional<Set<IDietGroup>> get(Item item) {
    return Optional.ofNullable(GENERATED.get(item));
  }
}
