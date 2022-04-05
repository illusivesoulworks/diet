package top.theillusivec4.diet.common.util;

import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import top.theillusivec4.diet.api.DietApi;

public class DietRegeneration {

  public static boolean hasRegen(PlayerEntity player, boolean flag) {
    ModifiableAttributeInstance attributeInstance =
        player.getAttribute(DietApi.getInstance().getNaturalRegeneration());
    return flag && (attributeInstance == null || attributeInstance.getValue() >= 1.0d);
  }
}
