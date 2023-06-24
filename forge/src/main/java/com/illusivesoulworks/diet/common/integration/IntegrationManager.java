package com.illusivesoulworks.diet.common.integration;

import net.minecraftforge.fml.ModList;

public class IntegrationManager {

  private static boolean isCuriosLoaded;

  public static void setup() {
    isCuriosLoaded = ModList.get().isLoaded("curios");
  }

  public static boolean isCuriosLoaded() {
    return isCuriosLoaded;
  }
}
