/*
 * Copyright (C) 2021-2023 Illusive Soulworks
 *
 * Diet is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Diet is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Diet.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.diet.client.screen;

import com.illusivesoulworks.diet.client.DietClientEvents;
import com.illusivesoulworks.diet.common.config.DietConfig;
import com.illusivesoulworks.diet.platform.ClientServices;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class DynamicButton extends ImageButton {

  private final AbstractContainerScreen<?> containerScreen;

  public DynamicButton(AbstractContainerScreen<?> screenIn, int xIn, int yIn, int widthIn,
                       int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn,
                       ResourceLocation resourceLocationIn, OnPress onPressIn) {
    super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn,
        onPressIn);
    this.containerScreen = screenIn;
  }

  @Override
  public void renderButton(@Nonnull PoseStack matrixStack, int mouseX, int mouseY,
                           float partialTicks) {
    this.x =
        ClientServices.INSTANCE.getGuiLeft(this.containerScreen) + DietConfig.CLIENT.buttonX.get();
    this.y =
        ClientServices.INSTANCE.getGuiTop(this.containerScreen) + DietConfig.CLIENT.buttonY.get() +
            83;
    super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
  }

  @Override
  public void renderToolTip(@Nonnull PoseStack matrixStack, int mouseX, int mouseY) {
    List<Component> tooltips = DietTooltip.getEffects();

    if (!tooltips.isEmpty()) {
      DietClientEvents.tooltip = tooltips;
      DietClientEvents.tooltipX = mouseX;
      DietClientEvents.tooltipY = mouseY;
    }
  }
}
