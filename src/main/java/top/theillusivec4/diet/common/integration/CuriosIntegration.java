package top.theillusivec4.diet.common.integration;

import net.minecraft.client.gui.screen.Screen;
import top.theillusivec4.curios.client.gui.CuriosScreen;
import top.theillusivec4.diet.client.DietClientEventsListener;

public class CuriosIntegration {

  public static boolean isCuriosScreen(Screen screen) {
    return screen instanceof CuriosScreen;
  }
}
