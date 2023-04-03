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

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.DietCapability;
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.common.config.DietClientConfig;
import top.theillusivec4.diet.common.effect.DietEffectsInfo;
import top.theillusivec4.diet.common.group.DietGroups;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

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
        super(MutableComponent.create(new TranslatableContents("gui." + DietMod.MOD_ID + ".title")));
        this.xSize = 248;
        this.ySize = DietGroups.get().size() * 20 + 60;
        this.fromInventory = fromInventory;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(
            new Button(this.width / 2 - 50, (this.height + this.ySize) / 2 - 30,
                100, 20, MutableComponent.create(new TranslatableContents("gui.diet.close")), (p_213002_1_) -> {
                if (this.minecraft != null && this.minecraft.player != null) {

                    if (fromInventory) {
                        this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
                    } else {
                        this.onClose();
                    }
                }
            }));
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        this.renderForeground(matrixStack, mouseX, mouseY);
        this.renderTitle(matrixStack, mouseX, mouseY);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void renderTitle(PoseStack matrixStack, int mouseX, int mouseY) {
        int titleWidth = this.font.width(this.title.getString());
        this.font
            .draw(matrixStack, this.title, (float) this.width / 2 - (float) titleWidth / 2,
                (float) this.height / 2 - (float) this.ySize / 2 + 10, DietClientConfig.textColor);
        List<DietEffectsInfo.AttributeModifier> modifiers = DietScreen.tooltip.getModifiers();
        List<DietEffectsInfo.StatusEffect> effects = DietScreen.tooltip.getEffects();

        if (this.minecraft != null && (!modifiers.isEmpty() || !effects.isEmpty())) {
            RenderSystem.setShaderTexture(0, ICONS);
            int lowerX = this.width / 2 + titleWidth / 2 + 5;
            int lowerY = this.height / 2 - this.ySize / 2 + 7;
            int upperX = lowerX + 16;
            int upperY = lowerY + 16;
            GuiComponent
                .blit(matrixStack, lowerX, lowerY,
                    16, 16, 0, 37, 16, 16, 256, 256);

            if (mouseX >= lowerX && mouseX <= upperX && mouseY >= lowerY && mouseY <= upperY) {
                List<Component> tooltips = DietTooltip.getEffects();
                this.renderComponentTooltip(matrixStack, tooltips, mouseX, mouseY);
            }
        }
    }

    public void renderForeground(PoseStack matrixStack, int mouseX, int mouseY) {

        if (this.minecraft != null) {
            LocalPlayer player = this.minecraft.player;

            if (player != null) {
                DietCapability.get(player).ifPresent(diet -> {
                    int y = this.height / 2 - this.ySize / 2 + 25;
                    int x = this.width / 2 - this.xSize / 2 + 10;
                    Component tooltip = null;

                    for (IDietGroup group : DietGroups.get()) {
                        this.itemRenderer.renderGuiItem(new ItemStack(group.getIcon()), x, y - 5);
                        MutableComponent text = MutableComponent.create(new TranslatableContents(
                            "groups." + DietMod.MOD_ID + "." + group.getName() + ".name"));
                        this.font.draw(matrixStack, text, x + 20, y, DietClientConfig.textColor);
                        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
                        RenderSystem.setShaderTexture(0, ICONS);
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
                        this.font.draw(matrixStack, percentText, (float) (xPos + 1), (float) yPos, 0);
                        this.font.draw(matrixStack, percentText, (float) (xPos - 1), (float) yPos, 0);
                        this.font.draw(matrixStack, percentText, (float) xPos, (float) (yPos + 1), 0);
                        this.font.draw(matrixStack, percentText, (float) xPos, (float) (yPos - 1), 0);
                        this.font
                            .draw(matrixStack, percentText, (float) xPos, (float) yPos, color.getRGB());
                        int lowerY = y - 5;
                        int upperX = x + 16;
                        int upperY = lowerY + 16;

                        if (mouseX >= x && mouseX <= upperX && mouseY >= lowerY && mouseY <= upperY) {
                            String key = "groups." + DietMod.MOD_ID + "." + group.getName() + ".tooltip";

                            if (Language.getInstance().has(key)) {
                                tooltip = MutableComponent.create(new TranslatableContents(key));
                            }
                        }
                        y += 20;
                    }

                    if (tooltip != null) {
                        List<Component> tooltips = Lists.newArrayList(tooltip);
                        this.renderComponentTooltip(matrixStack, tooltips, mouseX, mouseY);
                    }
                });
            }
        }
    }

    @Override
    public void renderBackground(@Nonnull PoseStack matrixStack) {
        super.renderBackground(matrixStack);

        if (this.minecraft != null) {
            RenderSystem.setShaderTexture(0, BACKGROUND);
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            GuiComponent.blit(matrixStack, i, j, this.xSize, 4, 0, 0, 248, 4, 256, 256);
            GuiComponent
                .blit(matrixStack, i, j + 4, this.xSize, this.ySize - 8, 0, 4, 248, 24, 256, 256);
            GuiComponent
                .blit(matrixStack, i, j + this.ySize - 4, this.xSize, 4, 0, 162, 248, 4, 256, 256);
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