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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.DietApi;
import top.theillusivec4.diet.api.DietCapability;
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.api.IDietResult;
import top.theillusivec4.diet.common.config.DietClientConfig;
import top.theillusivec4.diet.common.config.DietServerConfig;
import top.theillusivec4.diet.common.integration.CuriosIntegration;
import top.theillusivec4.diet.common.integration.IntegrationManager;
import top.theillusivec4.diet.common.util.DietResult;

@Mod.EventBusSubscriber(modid = DietMod.MOD_ID, value = Dist.CLIENT)
public class DietClientEventsListener {

  private static final Tags.IOptionalNamedTag<Item> SPECIAL_FOOD =
      ForgeTagHandler.createOptionalTag(ForgeRegistries.ITEMS,
          new ResourceLocation(DietMod.MOD_ID, "special_food"), new HashSet<>());
  private static final DecimalFormat DECIMALFORMAT = Util.make(new DecimalFormat("#.#"),
      (num) -> num.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT)));

  private static final ResourceLocation ICONS =
      new ResourceLocation(DietMod.MOD_ID, "textures/gui/icons.png");

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void initGui(final GuiScreenEvent.InitGuiEvent.Post evt) {
    Screen screen = evt.getGui();

    if (screen instanceof InventoryScreen ||
        (IntegrationManager.isCuriosLoaded() && CuriosIntegration.isCuriosScreen(screen))) {
      ContainerScreen<?> containerScreen = (ContainerScreen<?>) screen;
      evt.addWidget(new DynamicButton(containerScreen,
          containerScreen.getGuiLeft() + DietClientConfig.buttonX,
          containerScreen.height / 2 + DietClientConfig.buttonY, 20, 18, 0, 0, 19, ICONS,
          (button) -> Minecraft.getInstance().displayGuiScreen(new DietScreen(true))));
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void tick(final TickEvent.ClientTickEvent evt) {
    Minecraft mc = Minecraft.getInstance();
    ClientPlayerEntity player = mc.player;

    if (player != null && evt.phase == TickEvent.Phase.END && mc.isGameFocused() &&
        !(mc.currentScreen instanceof DietScreen) && DietKeys.OPEN_GUI.isPressed()) {
      mc.displayGuiScreen(new DietScreen(mc.currentScreen instanceof InventoryScreen));
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void tooltip(final ItemTooltipEvent evt) {
    PlayerEntity player = evt.getPlayer();
    List<ITextComponent> tooltips = evt.getToolTip();
    ItemStack stack = evt.getItemStack();

    if (player != null && player.world != null) {

      if (DietServerConfig.hideTooltipsUntilEaten &&
          DietCapability.get(player).map(tracker -> !tracker.getEaten().contains(stack.getItem()))
              .orElse(false)) {
        return;
      }
      IDietResult result = DietApi.getInstance().get(player, stack);

      if (result != DietResult.EMPTY) {
        Map<IDietGroup, Float> groups = result.get();
        boolean specialFood = SPECIAL_FOOD.contains(stack.getItem());

        if (!groups.isEmpty()) {
          List<ITextComponent> groupsTooltips = new ArrayList<>();

          for (Map.Entry<IDietGroup, Float> entry : groups.entrySet()) {
            float value = entry.getValue();
            TranslationTextComponent groupName = new TranslationTextComponent(
                "groups." + DietMod.MOD_ID + "." + entry.getKey().getName() + ".name");

            if (specialFood) {
              groupsTooltips.add(
                  (new TranslationTextComponent("tooltip." + DietMod.MOD_ID + ".group_", groupName))
                      .mergeStyle(TextFormatting.GREEN));
            } else if (value > 0.0f) {
              groupsTooltips.add(
                  (new TranslationTextComponent("tooltip." + DietMod.MOD_ID + ".group",
                      DECIMALFORMAT.format(entry.getValue() * 100), groupName))
                      .mergeStyle(TextFormatting.GREEN));
            }
          }

          if (!groupsTooltips.isEmpty()) {
            tooltips.add(StringTextComponent.EMPTY);
            tooltips.add(new TranslationTextComponent("tooltip." + DietMod.MOD_ID + ".eaten")
                .mergeStyle(TextFormatting.GRAY));
            tooltips.addAll(groupsTooltips);
          }
        }
      }
    }
  }

  public static class DynamicButton extends ImageButton {

    private final ContainerScreen<?> containerScreen;

    public DynamicButton(ContainerScreen<?> screenIn, int xIn, int yIn, int widthIn, int heightIn,
                         int xTexStartIn, int yTexStartIn, int yDiffTextIn,
                         ResourceLocation resourceLocationIn, IPressable onPressIn) {
      super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn,
          onPressIn);
      containerScreen = screenIn;
    }

    @Override
    public void renderWidget(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY,
                             float partialTicks) {
      x = containerScreen.getGuiLeft() + DietClientConfig.buttonX;
      y = containerScreen.getGuiTop() + DietClientConfig.buttonY + 83;
      super.renderWidget(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderToolTip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
      List<ITextComponent> tooltips = DietTooltip.getEffects();

      if (!tooltips.isEmpty()) {
        containerScreen.renderWrappedToolTip(matrixStack, tooltips, mouseX, mouseY,
            Minecraft.getInstance().fontRenderer);
      }
    }
  }
}
