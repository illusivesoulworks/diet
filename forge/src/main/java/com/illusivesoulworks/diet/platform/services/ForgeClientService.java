package com.illusivesoulworks.diet.platform.services;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class ForgeClientService implements IClientService {

  @Override
  public int getGuiLeft(AbstractContainerScreen<?> containerScreen) {
    return containerScreen.getGuiLeft();
  }

  @Override
  public int getGuiTop(AbstractContainerScreen<?> containerScreen) {
    return containerScreen.getGuiTop();
  }
}
