package com.illusivesoulworks.diet.platform.services;

import com.illusivesoulworks.diet.mixin.AccessorContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class QuiltClientService implements IClientService {

  @Override
  public int getGuiLeft(AbstractContainerScreen<?> containerScreen) {
    return ((AccessorContainerScreen) containerScreen).getLeftPos();
  }

  @Override
  public int getGuiTop(AbstractContainerScreen<?> containerScreen) {
    return ((AccessorContainerScreen) containerScreen).getTopPos();
  }
}
