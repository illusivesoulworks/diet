package top.theillusivec4.diet.common.integration;

import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.power.PowerType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;

public class OriginsIntegration {

  public static Set<String> getOrigins(PlayerEntity player) {
    Set<String> result = new HashSet<>();

    for (Map.Entry<OriginLayer, Origin> entry : Origin.get(player).entrySet()) {
      result.add(entry.getValue().getIdentifier().toString());
    }
    return result;
  }

  public static Set<String> getOriginPowers(PlayerEntity player) {
    Set<String> result = new HashSet<>();

    for (Map.Entry<OriginLayer, Origin> entry : Origin.get(player).entrySet()) {

      for (PowerType<?> powerType : entry.getValue().getPowerTypes()) {
        result.add(powerType.getIdentifier().toString());
      }
    }
    return result;
  }
}
