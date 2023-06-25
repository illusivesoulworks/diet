package com.illusivesoulworks.diet.common.data;

import com.illusivesoulworks.diet.DietCommonMod;
import com.illusivesoulworks.diet.common.data.suite.DietSuites;
import java.util.Collection;
import java.util.Collections;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

public class DietFabricSuites extends DietSuites implements IdentifiableResourceReloadListener {

  @Override
  public ResourceLocation getFabricId() {
    return DietCommonMod.resource("suites");
  }

  @Override
  public Collection<ResourceLocation> getFabricDependencies() {
    return Collections.singleton(DietCommonMod.resource("groups"));
  }
}
