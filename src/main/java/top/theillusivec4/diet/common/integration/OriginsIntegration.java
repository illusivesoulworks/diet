package top.theillusivec4.diet.common.integration;

import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class OriginsIntegration {

  public static Set<String> getOrigins(Player player) {
    Set<String> result = new HashSet<>();
    IOriginContainer.get(player).ifPresent(originContainer -> {
      for (Map.Entry<OriginLayer, Origin> entry : originContainer.getOrigins().entrySet()) {
        ResourceLocation rl = entry.getValue().getRegistryName();

        if (rl != null) {
          result.add(rl.toString());
        }
      }
    });
    return result;
  }

  public static Set<String> getOriginPowers(Player player) {
    Set<String> result = new HashSet<>();
    IOriginContainer.get(player).ifPresent(originContainer -> {
      for (Map.Entry<OriginLayer, Origin> entry : originContainer.getOrigins().entrySet()) {

        for (ResourceLocation powerType : entry.getValue().getPowers()) {
          result.add(powerType.toString());
        }
      }
    });
    return result;
  }
}
