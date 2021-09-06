package top.theillusivec4.diet.common.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.common.group.DietGroups;

public class DietFallback {

  private static final Map<Item, Set<IDietGroup>> VALUES = new HashMap<>();

  public static void reload(MinecraftServer server) {
    VALUES.clear();
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

    for (IRecipe<?> recipe : recipeManager.getRecipes()) {
      ItemStack output = recipe.getRecipeOutput();
      Item outputItem = output.getItem();

      if (foodWithoutTags.contains(outputItem) && !VALUES.containsKey(outputItem)) {
        Set<IDietGroup> calculatedGroups = new HashSet<>();

        for (Ingredient ingredient : recipe.getIngredients()) {

          for (ItemStack matchingStack : ingredient.getMatchingStacks()) {

            for (IDietGroup group : groups) {

              if (group.contains(matchingStack)) {
                calculatedGroups.add(group);
              }
            }
          }
        }

        VALUES.putIfAbsent(outputItem, calculatedGroups);
      }
    }
  }

  public static Optional<Set<IDietGroup>> get(Item item) {
    return Optional.ofNullable(VALUES.get(item));
  }
}
