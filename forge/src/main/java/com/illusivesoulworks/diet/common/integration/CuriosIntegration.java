package com.illusivesoulworks.diet.common.integration;

import net.minecraft.client.gui.screens.Screen;
import top.theillusivec4.curios.client.gui.CuriosScreen;

public class CuriosIntegration {

  public static boolean isCuriosScreen(Screen screen) {
    return screen instanceof CuriosScreen;
  }
}
