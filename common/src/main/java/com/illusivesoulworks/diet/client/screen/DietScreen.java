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

import com.google.common.collect.Lists;
import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.api.type.IDietGroup;
import com.illusivesoulworks.diet.api.type.IDietSuite;
import com.illusivesoulworks.diet.api.util.DietColor;
import com.illusivesoulworks.diet.client.DietKeys;
import com.illusivesoulworks.diet.common.config.DietConfig;
import com.illusivesoulworks.diet.common.data.effect.DietEffectsInfo;
import com.illusivesoulworks.diet.common.data.suite.DietSuites;
import com.illusivesoulworks.diet.platform.Services;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class DietScreen extends Screen {

  private static final ResourceLocation BACKGROUND =
      new ResourceLocation("minecraft", "textures/gui/demo_background.png");
  private static final ResourceLocation ICONS =
      new ResourceLocation(DietConstants.MOD_ID, "textures/gui/icons.png");

  public static DietEffectsInfo tooltip = new DietEffectsInfo();
  private final Set<IDietGroup> groups = new HashSet<>();

  private final int xSize;
  private int ySize;
  private final boolean fromInventory;

  public DietScreen(boolean fromInventory) {
    super(Component.translatable("gui." + DietConstants.MOD_ID + ".title"));
    this.xSize = 248;
    this.fromInventory = fromInventory;
  }

  @Override
  protected void init() {
    super.init();

    if (this.minecraft != null && this.minecraft.player != null && this.minecraft.level != null) {
      groups.addAll(Services.CAPABILITY.get(this.minecraft.player).map(
          tracker -> DietSuites.getSuite(this.minecraft.level, tracker.getSuite())
              .map(IDietSuite::getGroups).orElse(Set.of())).orElse(Set.of()));
    }
    this.ySize = this.groups.size() * 20 + 60;
    this.addRenderableWidget(
        Button.builder(Component.translatable("gui.diet.close"), (button) -> {
          if (this.minecraft != null && this.minecraft.player != null) {

            if (fromInventory) {
              this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
            } else {
              this.onClose();
            }
          }
        }).size(100, 20).pos(this.width / 2 - 50, (this.height + this.ySize) / 2 - 30).build());
  }

  @Override
  public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(guiGraphics);
    this.renderForeground(guiGraphics, mouseX, mouseY);
    this.renderTitle(guiGraphics, mouseX, mouseY);
    super.render(guiGraphics, mouseX, mouseY, partialTicks);
  }

  public void renderTitle(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    int titleWidth = this.font.width(this.title.getString());
    guiGraphics.drawString(this.font, this.title, this.width / 2 - titleWidth / 2,
        this.height / 2 - this.ySize / 2 + 10, getTextColor(), false);
    List<DietEffectsInfo.AttributeModifier> modifiers = DietScreen.tooltip.getModifiers();
    List<DietEffectsInfo.StatusEffect> effects = DietScreen.tooltip.getEffects();

    if (this.minecraft != null && (!modifiers.isEmpty() || !effects.isEmpty())) {
      int lowerX = this.width / 2 + titleWidth / 2 + 5;
      int lowerY = this.height / 2 - this.ySize / 2 + 7;
      int upperX = lowerX + 16;
      int upperY = lowerY + 16;
      guiGraphics.blit(ICONS, lowerX, lowerY, 16, 16, 0, 37, 16, 16, 256, 256);

      if (mouseX >= lowerX && mouseX <= upperX && mouseY >= lowerY && mouseY <= upperY) {
        List<Component> tooltips = DietTooltip.getEffects();
        guiGraphics.renderComponentTooltip(this.font, tooltips, mouseX, mouseY);
      }
    }
  }

  private int getTextColor() {
    String config = DietConfig.CLIENT.textColor.get();

    if (config.startsWith("#")) {
      return Integer.parseInt(config.substring(1), 16);
    }
    return Integer.parseInt(config);
  }

  public void renderForeground(GuiGraphics guiGraphics, int mouseX, int mouseY) {

    if (this.minecraft != null && this.minecraft.level != null) {
      LocalPlayer player = this.minecraft.player;

      if (player != null) {
        Services.CAPABILITY.get(player).ifPresent(
            diet -> DietSuites.getSuite(this.minecraft.level, diet.getSuite()).ifPresent(suite -> {
              int y = this.height / 2 - this.ySize / 2 + 25;
              int x = this.width / 2 - this.xSize / 2 + 10;
              Component tooltip = null;

              for (IDietGroup group : suite.getGroups()) {
                guiGraphics.renderItem(new ItemStack(group.getIcon()), x, y - 5);
                MutableComponent text = Component.translatable(
                    "groups." + DietConstants.MOD_ID + "." + group.getName() + ".name");
                guiGraphics.drawString(this.font, text, x + 20, y, getTextColor(), false);
                RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
                RenderSystem.setShaderTexture(0, ICONS);
                DietColor color = diet.isActive() ? group.getColor() : DietColor.GRAY;
                int red = color.red();
                int green = color.green();
                int blue = color.blue();
                int percent = (int) Math.floor(diet.getValue(group.getName()) * 100.0f);
                String percentText = percent + "%";
                coloredBlit(guiGraphics.pose(), x + 90, y + 2, 102, 5, 20, 0, 102, 5, 256, 256, red,
                    green, blue, 255);

                if (percent > 0) {
                  int texWidth = percent + 1;
                  coloredBlit(guiGraphics.pose(), x + 90, y + 2, texWidth, 5, 20, 5, texWidth, 5,
                      256, 256, red, green, blue, 255);
                }
                int xPos = x + 200;
                int yPos = y + 1;
                guiGraphics.drawString(this.font, percentText, (xPos + 1), yPos, 0, false);
                guiGraphics.drawString(this.font, percentText, (xPos - 1), yPos, 0, false);
                guiGraphics.drawString(this.font, percentText, xPos, (yPos + 1), 0, false);
                guiGraphics.drawString(this.font, percentText, xPos, (yPos - 1), 0, false);
                guiGraphics.drawString(this.font, percentText, xPos, yPos, color.getRGB(), false);
                int lowerY = y - 5;
                int upperX = x + 16;
                int upperY = lowerY + 16;

                if (mouseX >= x && mouseX <= upperX && mouseY >= lowerY && mouseY <= upperY) {
                  String key =
                      "groups." + DietConstants.MOD_ID + "." + group.getName() + ".tooltip";

                  if (Language.getInstance().has(key)) {
                    tooltip = Component.translatable(key);
                  }
                }
                y += 20;
              }

              if (tooltip != null) {
                List<Component> tooltips = Lists.newArrayList(tooltip);
                guiGraphics.renderComponentTooltip(this.font, tooltips, mouseX, mouseY);
              }
            }));
      }
    }
  }

  @Override
  public void renderBackground(@Nonnull GuiGraphics guiGraphics) {
    super.renderBackground(guiGraphics);

    if (this.minecraft != null) {
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      guiGraphics.blit(BACKGROUND, i, j, this.xSize, 4, 0, 0, 248, 4, 256, 256);
      guiGraphics.blit(BACKGROUND, i, j + 4, this.xSize, this.ySize - 8, 0, 4, 248, 24, 256, 256);
      guiGraphics.blit(BACKGROUND, i, j + this.ySize - 4, this.xSize, 4, 0, 162, 248, 4, 256, 256);
    }
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

    if (this.minecraft != null && this.minecraft.player != null) {

      if (this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
        this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
        return true;
      } else if (DietKeys.OPEN_GUI.matches(keyCode, scanCode)) {

        if (fromInventory) {
          this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
        } else {
          this.onClose();
        }
        return true;
      }
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }

  private static void coloredBlit(PoseStack matrixStack, int x, int y, int width, int height,
                                  float uOffset, float vOffset, int uWidth, int vHeight,
                                  int textureWidth, int textureHeight, int red, int green, int blue,
                                  int alpha) {
    int x2 = x + width;
    int y2 = y + height;
    float minU = (uOffset + 0.0F) / (float) textureWidth;
    float maxU = (uOffset + (float) uWidth) / (float) textureWidth;
    float minV = (vOffset + 0.0F) / (float) textureHeight;
    float maxV = (vOffset + (float) vHeight) / (float) textureHeight;
    Matrix4f matrix = matrixStack.last().pose();
    BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
    bufferbuilder.vertex(matrix, (float) x, (float) y2, 0).color(red, green, blue, alpha)
        .uv(minU, maxV).endVertex();
    bufferbuilder.vertex(matrix, (float) x2, (float) y2, 0).color(red, green, blue, alpha)
        .uv(maxU, maxV).endVertex();
    bufferbuilder.vertex(matrix, (float) x2, (float) y, 0).color(red, green, blue, alpha)
        .uv(maxU, minV).endVertex();
    bufferbuilder.vertex(matrix, (float) x, (float) y, 0).color(red, green, blue, alpha)
        .uv(minU, minV).endVertex();
    BufferUploader.drawWithShader(bufferbuilder.end());
  }
}
