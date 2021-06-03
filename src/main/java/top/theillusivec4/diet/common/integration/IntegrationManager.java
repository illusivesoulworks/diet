package top.theillusivec4.diet.common.integration;

import net.minecraftforge.fml.ModList;

public class IntegrationManager {

  private static boolean isCuriosLoaded;
  private static boolean isOriginsLoaded;

  public static void setup() {
    isCuriosLoaded = ModList.get().isLoaded("curios");
    isOriginsLoaded = ModList.get().isLoaded("origins");
  }

  public static boolean isCuriosLoaded() {
    return isCuriosLoaded;
  }

  public static boolean isOriginsLoaded() {
    return isOriginsLoaded;
  }
}
