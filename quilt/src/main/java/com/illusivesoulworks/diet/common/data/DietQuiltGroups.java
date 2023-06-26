package com.illusivesoulworks.diet.common.data;

import com.illusivesoulworks.diet.DietCommonMod;
import com.illusivesoulworks.diet.common.data.group.DietGroups;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader;

public class DietQuiltGroups extends DietGroups implements IdentifiableResourceReloader {

  @Nonnull
  @Override
  public ResourceLocation getQuiltId() {
    return DietCommonMod.resource("groups");
  }
}
