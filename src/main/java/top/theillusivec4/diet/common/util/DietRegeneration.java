package top.theillusivec4.diet.common.util;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.diet.api.DietApi;

public class DietRegeneration {

  public static boolean hasRegen(Player player, boolean flag) {
    AttributeInstance attributeInstance =
        player.getAttribute(DietApi.getInstance().getNaturalRegeneration());
    return flag && (attributeInstance == null || attributeInstance.getValue() >= 1.0d);
  }
}
