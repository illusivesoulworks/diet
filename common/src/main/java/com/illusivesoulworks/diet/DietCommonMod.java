package com.illusivesoulworks.diet;

import net.minecraft.resources.ResourceLocation;

public class DietCommonMod {

  public static ResourceLocation resource(String path) {
    return new ResourceLocation(DietConstants.MOD_ID, path);
  }
}