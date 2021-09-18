package top.theillusivec4.diet.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public abstract class DietApi {

  private static final DietApi DEFAULT = new DietApi() {
  };
  private static DietApi instance = null;

  // The API instance to use
  public static DietApi getInstance() {
    return instance == null ? DEFAULT : instance;
  }

  public static void setInstance(DietApi api) {

    if (instance == null) {
      instance = api;
    }
  }

  /**
   * Retrieves a set of diet groups from a given player and ItemStack.
   *
   * @param player The player involved
   * @param stack  The ItemStack involved
   * @return A set of diet groups
   */
  public Set<IDietGroup> getGroups(PlayerEntity player, ItemStack stack) {
    return new HashSet<>();
  }

  /**
   * Retrieves a diet result from a given player and ItemStack.
   *
   * @param player The player involved
   * @param stack  The ItemStack involved
   * @return A diet result
   */
  public IDietResult get(PlayerEntity player, ItemStack stack) {
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
  public IDietResult get(PlayerEntity player, ItemStack stack, int food, float saturation) {
    return HashMap::new;
  }
}
