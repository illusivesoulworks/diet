package top.theillusivec4.diet.common.integration;

import net.minecraftforge.fml.ModList;

public class IntegrationManager {

  static boolean isCuriosLoaded;

  public static void setup() {
    isCuriosLoaded = ModList.get().isLoaded("curios");
  }

  public static boolean isCuriosLoaded() {
    return isCuriosLoaded;
  }
}
