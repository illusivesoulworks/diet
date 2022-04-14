package top.theillusivec4.diet.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class DietApi {

  // The API instance to use
  public static DietApi getInstance() {
    throw new IllegalArgumentException("Missing API implementation for Diet!");
  }

  /**
   * Retrieves a set of diet groups from a given player and ItemStack.
   *
   * @param player The player involved
   * @param stack  The ItemStack involved
   * @return A set of diet groups
   */
  public Set<IDietGroup> getGroups(Player player, ItemStack stack) {
    return new HashSet<>();
  }

  /**
   * Retrieves a diet result from a given player and ItemStack.
   *
   * @param player The player involved
   * @param stack  The ItemStack involved
   * @return A diet result
   */
  public IDietResult get(Player player, ItemStack stack) {
    return HashMap::new;
  }

  /**
   * Retrieves a diet result from a given player, ItemStack, food, and saturation.
   *
   * @param player     The player involved
   * @param stack      The ItemStack involved
   * @param food       The amount of food gain
   * @param saturation The saturation modifier
   * @return A diet result
   */
  public IDietResult get(Player player, ItemStack stack, int food, float saturation) {
    return HashMap::new;
  }

  /**
   * Retrieves a diet result from a given player, ItemStack list, food, and saturation.
   *
   * @param player     The player involved
   * @param stacks     The ItemStacks involved
   * @param food       The amount of food gain
   * @param saturation The saturation modifier
   * @return A diet result
   */
  public IDietResult get(Player player, List<ItemStack> stacks, int food, float saturation) {
    return HashMap::new;
  }

  /**
   * Retrieves the attribute that controls player natural regeneration from high food and saturation
   * levels.
   * Values >= 1 allows natural regeneration to work as normal as per the gamerule
   * Values < 1 disables natural regeneration
   *
   * @return The registered attribute
   */
  public Attribute getNaturalRegeneration() {
    return null;
  }
}
