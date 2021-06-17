/*
 * Copyright (C) 2021 C4
 *
 * This file is part of Diet, a mod made for Minecraft.
 *
 * Diet is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Diet is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Diet.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package top.theillusivec4.diet.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.DietCapability;
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.common.config.DietClientConfig;
import top.theillusivec4.diet.common.effect.DietEffectsInfo;
import top.theillusivec4.diet.common.group.DietGroups;

public class DietScreen extends Screen {

  private static final ResourceLocation BACKGROUND =
      new ResourceLocation("minecraft", "textures/gui/demo_background.png");
  private static final ResourceLocation ICONS =
      new ResourceLocation(DietMod.MOD_ID, "textures/gui/icons.png");

  public static DietEffectsInfo tooltip = new DietEffectsInfo();

  private final int xSize;
  private final int ySize;
  private final boolean fromInventory;

  public DietScreen(boolean fromInventory) {
    super(new TranslationTextComponent("gui." + DietMod.MOD_ID + ".title"));
    this.xSize = 248;
    this.ySize = 166;
    this.fromInventory = fromInventory;
  }

  @Override
  protected void init() {
    super.init();
    int numGroups = DietGroups.get().size();
    this.addButton(
        new Button(this.width / 2 - 50, (this.height - this.ySize) / 2 + (1 + numGroups) * 18 + 14,
            100, 20, new TranslationTextComponent("gui.diet.close"), (p_213002_1_) -> {
          if (this.minecraft != null && this.minecraft.player != null) {

            if (fromInventory) {
              this.minecraft.displayGuiScreen(new InventoryScreen(this.minecraft.player));
            } else {
              this.closeScreen();
            }
          }
        }));
  }

  @Override
  public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(matrixStack);
    this.renderForeground(matrixStack);
    this.renderTitle(matrixStack, mouseX, mouseY);
    super.render(matrixStack, mouseX, mouseY, partialTicks);
  }

  public void renderTitle(MatrixStack matrixStack, int mouseX, int mouseY) {
    int titleWidth = this.font.getStringWidth(this.title.getString());
    this.font
        .drawText(matrixStack, this.title, (float) this.width / 2 - (float) titleWidth / 2,
            (float) this.height / 2 - (float) this.ySize / 2 + 10, DietClientConfig.textColor);
    List<DietEffectsInfo.AttributeModifier> modifiers = DietScreen.tooltip.getModifiers();
    List<DietEffectsInfo.StatusEffect> effects = DietScreen.tooltip.getEffects();

    if (this.minecraft != null && (!modifiers.isEmpty() || !effects.isEmpty())) {
      this.minecraft.getTextureManager().bindTexture(ICONS);
      int lowerX = this.width / 2 + titleWidth / 2 + 5;
      int lowerY = this.height / 2 - this.ySize / 2 + 7;
      int upperX = lowerX + 16;
      int upperY = lowerY + 16;
      AbstractGui
          .blit(matrixStack, lowerX, lowerY,
              16, 16, 0, 37, 16, 16, 256, 256);

      if (mouseX >= lowerX && mouseX <= upperX && mouseY >= lowerY && mouseY <= upperY) {
        List<ITextComponent> tooltips = DietTooltip.getEffects();
        net.minecraftforge.fml.client.gui.GuiUtils
            .drawHoveringText(matrixStack, tooltips, mouseX, mouseY, width, height, -1, font);
      }
    }
  }

  public void renderForeground(MatrixStack matrixStack) {

    if (this.minecraft != null) {
      ClientPlayerEntity player = this.minecraft.player;

      if (player != null) {
        DietCapability.get(player).ifPresent(diet -> {
          int y = this.height / 2 - this.ySize / 2 + 25;
          int x = this.width / 2 - this.xSize / 2 + 10;

          for (IDietGroup group : DietGroups.get()) {
            this.itemRenderer.renderItemIntoGUI(new ItemStack(group.getIcon()), x, y - 5);
            TranslationTextComponent text = new TranslationTextComponent(
                "groups." + DietMod.MOD_ID + "." + group.getName() + ".name");
            this.font.drawText(matrixStack, text, x + 20, y, DietClientConfig.textColor);
            this.minecraft.getTextureManager().bindTexture(ICONS);
            Color color = diet.isActive() ? group.getColor() : Color.gray;
            int red = color.getRed();
            int green = color.getGreen();
            int blue = color.getBlue();
            int alpha = color.getAlpha();
            int percent = (int) Math.floor(diet.getValue(group.getName()) * 100.0f);
            String percentText = "" + percent + "%";
            coloredBlit(matrixStack, x + 90, y + 2, 102, 5, 20, 0, 102, 5, 256, 256, red, green,
                blue, alpha);

            if (percent > 0) {
              int texWidth = percent + 1;
              coloredBlit(matrixStack, x + 90, y + 2, texWidth, 5, 20, 5, texWidth, 5, 256, 256,
                  red, green, blue, alpha);
            }
            int xPos = x + 200;
            int yPos = y + 1;
            this.font.drawString(matrixStack, percentText, (float) (xPos + 1), (float) yPos, 0);
            this.font.drawString(matrixStack, percentText, (float) (xPos - 1), (float) yPos, 0);
            this.font.drawString(matrixStack, percentText, (float) xPos, (float) (yPos + 1), 0);
            this.font.drawString(matrixStack, percentText, (float) xPos, (float) (yPos - 1), 0);
            this.font
                .drawString(matrixStack, percentText, (float) xPos, (float) yPos, color.getRGB());
            y += 20;
          }
        });
      }
    }
  }

  @Override
  public void renderBackground(@Nonnull MatrixStack matrixStack) {
    super.renderBackground(matrixStack);

    if (this.minecraft != null) {
      int numGroups = DietGroups.get().size();
      this.minecraft.getTextureManager().bindTexture(BACKGROUND);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      AbstractGui.blit(matrixStack, i, j, 248, 4, 0, 0, 248, 4, 256, 256);
      AbstractGui
          .blit(matrixStack, i, j + 4, 248, 40 + (1 + numGroups) * 18, 0, 4, 248, 24, 256, 256);
      AbstractGui
          .blit(matrixStack, i, j + (1 + numGroups) * 18 + 44, 248, 4, 0, 162, 248, 4, 256, 256);
    }
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

    if (this.minecraft != null && this.minecraft.player != null) {

      if (this.minecraft.gameSettings.keyBindInventory.matchesKey(keyCode, scanCode)) {
        this.minecraft.displayGuiScreen(new InventoryScreen(this.minecraft.player));
        return true;
      } else if (DietKeys.OPEN_GUI.matchesKey(keyCode, scanCode)) {

        if (fromInventory) {
          this.minecraft.displayGuiScreen(new InventoryScreen(this.minecraft.player));
        } else {
          this.closeScreen();
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

  @SuppressWarnings("deprecation")
  private static void coloredBlit(MatrixStack matrixStack, int x, int y, int width, int height,
                                  float uOffset, float vOffset, int uWidth, int vHeight,
                                  int textureWidth, int textureHeight, int red, int green, int blue,
                                  int alpha) {
    int x2 = x + width;
    int y2 = y + height;
    float minU = (uOffset + 0.0F) / (float) textureWidth;
    float maxU = (uOffset + (float) uWidth) / (float) textureWidth;
    float minV = (vOffset + 0.0F) / (float) textureHeight;
    float maxV = (vOffset + (float) vHeight) / (float) textureHeight;
    Matrix4f matrix = matrixStack.getLast().getMatrix();
    BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
    bufferbuilder.pos(matrix, (float) x, (float) y2, 0).color(red, green, blue, alpha)
        .tex(minU, maxV).endVertex();
    bufferbuilder.pos(matrix, (float) x2, (float) y2, 0).color(red, green, blue, alpha)
        .tex(maxU, maxV).endVertex();
    bufferbuilder.pos(matrix, (float) x2, (float) y, 0).color(red, green, blue, alpha)
        .tex(maxU, minV).endVertex();
    bufferbuilder.pos(matrix, (float) x, (float) y, 0).color(red, green, blue, alpha)
        .tex(minU, minV).endVertex();
    bufferbuilder.finishDrawing();
    RenderSystem.enableAlphaTest();
    WorldVertexBufferUploader.draw(bufferbuilder);
  }
}
