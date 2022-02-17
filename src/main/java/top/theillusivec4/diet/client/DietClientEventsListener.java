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

import com.mojang.blaze3d.vertex.PoseStack;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
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

  private static List<Component> tooltip = null;
  private static int tooltipX = 0;
  private static int tooltipY = 0;

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void initGui(final ScreenEvent.InitScreenEvent.Post evt) {
    Screen screen = evt.getScreen();

    if (DietClientConfig.addButton) {

      if (screen instanceof InventoryScreen ||
          (IntegrationManager.isCuriosLoaded() && CuriosIntegration.isCuriosScreen(screen))) {
        AbstractContainerScreen<?> containerScreen = (AbstractContainerScreen<?>) screen;
        evt.addListener(new DynamicButton(containerScreen,
            containerScreen.getGuiLeft() + DietClientConfig.buttonX,
            containerScreen.height / 2 + DietClientConfig.buttonY, 20, 18, 0, 0, 19, ICONS,
            (button) -> Minecraft.getInstance().setScreen(new DietScreen(true))));
      }
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void tick(final TickEvent.ClientTickEvent evt) {
    Minecraft mc = Minecraft.getInstance();
    LocalPlayer player = mc.player;

    if (player != null && evt.phase == TickEvent.Phase.END && mc.isWindowActive() &&
        !(mc.screen instanceof DietScreen) && DietKeys.OPEN_GUI.consumeClick()) {
      mc.setScreen(new DietScreen(mc.screen instanceof InventoryScreen));
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void tooltip(final ItemTooltipEvent evt) {
    Player player = evt.getPlayer();
    List<Component> tooltips = evt.getToolTip();
    ItemStack stack = evt.getItemStack();

    if (player != null && player.level != null) {

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
          List<Component> groupsTooltips = new ArrayList<>();
          List<Component> beneficial = new ArrayList<>();
          List<Component> harmful = new ArrayList<>();

          for (Map.Entry<IDietGroup, Float> entry : groups.entrySet()) {
            float value = entry.getValue();
            TranslatableComponent groupName = new TranslatableComponent(
                "groups." + DietMod.MOD_ID + "." + entry.getKey().getName() + ".name");
            TranslatableComponent tooltip = null;

            if (specialFood) {
              tooltip =
                  new TranslatableComponent("tooltip." + DietMod.MOD_ID + ".group_", groupName);
            } else if (value > 0.0f) {
              tooltip = new TranslatableComponent("tooltip." + DietMod.MOD_ID + ".group",
                  DECIMALFORMAT.format(entry.getValue() * 100), groupName);
            }

            if (tooltip != null) {

              if (entry.getKey().isBeneficial()) {
                tooltip.withStyle(ChatFormatting.GREEN);
                beneficial.add(tooltip);
              } else {
                tooltip.withStyle(ChatFormatting.RED);
                harmful.add(tooltip);
              }
            }
          }
          groupsTooltips.addAll(beneficial);
          groupsTooltips.addAll(harmful);

          if (!groupsTooltips.isEmpty()) {
            tooltips.add(TextComponent.EMPTY);
            tooltips.add(new TranslatableComponent("tooltip." + DietMod.MOD_ID + ".eaten")
                .withStyle(ChatFormatting.GRAY));
            tooltips.addAll(groupsTooltips);
          }
        }
      }
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void renderTooltip(TickEvent.RenderTickEvent event) {

    if(event.phase == TickEvent.Phase.END && tooltip != null) {
      Minecraft mc = Minecraft.getInstance();
      Screen screen = mc.screen;

      if (screen != null) {
        screen.renderTooltip(new PoseStack(), tooltip, Optional.empty(), tooltipX, tooltipY);
      }
      tooltip = null;
    }
  }

  public static class DynamicButton extends ImageButton {

    private final AbstractContainerScreen<?> containerScreen;

    public DynamicButton(AbstractContainerScreen<?> screenIn, int xIn, int yIn, int widthIn,
                         int heightIn,
                         int xTexStartIn, int yTexStartIn, int yDiffTextIn,
                         ResourceLocation resourceLocationIn, OnPress onPressIn) {
      super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn,
          onPressIn);
      containerScreen = screenIn;
    }

    @Override
    public void renderButton(@Nonnull PoseStack matrixStack, int mouseX, int mouseY,
                             float partialTicks) {
      x = containerScreen.getGuiLeft() + DietClientConfig.buttonX;
      y = containerScreen.getGuiTop() + DietClientConfig.buttonY + 83;
      super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderToolTip(@Nonnull PoseStack matrixStack, int mouseX, int mouseY) {
      List<Component> tooltips = DietTooltip.getEffects();

      if (!tooltips.isEmpty()) {
        DietClientEventsListener.tooltip = tooltips;
        DietClientEventsListener.tooltipX = mouseX;
        DietClientEventsListener.tooltipY = mouseY;
      }
    }
  }
}
