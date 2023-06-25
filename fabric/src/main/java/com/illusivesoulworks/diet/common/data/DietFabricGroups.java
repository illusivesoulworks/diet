package com.illusivesoulworks.diet.common.data;

import com.illusivesoulworks.diet.DietCommonMod;
import com.illusivesoulworks.diet.common.data.group.DietGroups;
import java.util.Collection;
import java.util.Collections;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.resources.ResourceLocation;

public class DietFabricGroups extends DietGroups implements IdentifiableResourceReloadListener {

  @Override
  public ResourceLocation getFabricId() {
    return DietCommonMod.resource("groups");
  }

  @Override
  public Collection<ResourceLocation> getFabricDependencies() {
    return Collections.singleton(ResourceReloadListenerKeys.TAGS);
  }
}
