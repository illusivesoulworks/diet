package com.illusivesoulworks.diet.platform.services;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;

public interface IClientService {

  int getGuiLeft(AbstractContainerScreen<?> containerScreen);

  int getGuiTop(AbstractContainerScreen<?> containerScreen);
}
