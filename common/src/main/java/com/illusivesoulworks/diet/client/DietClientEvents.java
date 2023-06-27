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

package com.illusivesoulworks.diet.client;

import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.api.DietApi;
import com.illusivesoulworks.diet.api.type.IDietGroup;
import com.illusivesoulworks.diet.api.type.IDietResult;
import com.illusivesoulworks.diet.client.screen.DietScreen;
import com.illusivesoulworks.diet.client.screen.DynamicButton;
import com.illusivesoulworks.diet.common.config.DietConfig;
import com.illusivesoulworks.diet.common.util.DietResult;
import com.illusivesoulworks.diet.platform.ClientServices;
import com.illusivesoulworks.diet.platform.Services;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DietClientEvents {

  private static final TagKey<Item> SPECIAL_FOOD = TagKey.create(Registries.ITEM,
      new ResourceLocation(DietConstants.MOD_ID, "special_food"));
  private static final DecimalFormat DECIMALFORMAT = Util.make(new DecimalFormat("#.#"),
      (num) -> num.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT)));
  private static final ResourceLocation ICONS =
      new ResourceLocation(DietConstants.MOD_ID, "textures/gui/icons.png");

  public static List<Component> tooltip = null;
  public static int tooltipX = 0;
  public static int tooltipY = 0;

  public static void tick(Minecraft mc) {
    LocalPlayer player = mc.player;

    if (player != null && mc.isWindowActive() && !(mc.screen instanceof DietScreen) &&
        DietKeys.OPEN_GUI.consumeClick()) {
      mc.setScreen(new DietScreen(mc.screen instanceof InventoryScreen));
    }
  }

  public static DynamicButton getButton(Screen screen) {

    if (DietConfig.CLIENT.addButton.get()) {
      AbstractContainerScreen<?> containerScreen = (AbstractContainerScreen<?>) screen;
      return new DynamicButton(containerScreen,
          ClientServices.INSTANCE.getGuiLeft(containerScreen) + DietConfig.CLIENT.buttonX.get(),
          containerScreen.height / 2 + DietConfig.CLIENT.buttonY.get(), 20, 18, 0, 0, 19, ICONS,
          (button) -> Minecraft.getInstance().setScreen(new DietScreen(true)));
    }
    return null;
  }

  public static void renderEffectsTooltip(Screen screen, GuiGraphics guiGraphics) {

    if (tooltip != null && screen != null) {
      guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltip, Optional.empty(), tooltipX,
          tooltipY);
      tooltip = null;
    }
  }

  public static void renderItemTooltip(Player player, ItemStack stack, List<Component> tooltips) {

    if (player != null) {

      if (DietConfig.SERVER.hideTooltipsUntilEaten.get() &&
          Services.CAPABILITY.get(player)
              .map(tracker -> !tracker.getEaten().contains(stack.getItem())).orElse(false)) {
        return;
      }
      IDietResult result = DietApi.getInstance().get(player, stack);

      if (result != DietResult.EMPTY) {
        Map<IDietGroup, Float> groups = result.get();
        boolean specialFood = stack.is(SPECIAL_FOOD);

        if (!groups.isEmpty()) {
          List<Component> groupsTooltips = new ArrayList<>();
          List<Component> beneficial = new ArrayList<>();
          List<Component> harmful = new ArrayList<>();

          for (Map.Entry<IDietGroup, Float> entry : groups.entrySet()) {
            float value = entry.getValue();
            Component groupName = Component.translatable(
                "groups." + DietConstants.MOD_ID + "." + entry.getKey().getName() + ".name");
            MutableComponent tooltip = null;

            if (specialFood) {
              tooltip =
                  Component.translatable("tooltip." + DietConstants.MOD_ID + ".group_", groupName);
            } else if (value > 0.0f) {
              tooltip = Component.translatable("tooltip." + DietConstants.MOD_ID + ".group",
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
            tooltips.add(Component.empty());
            tooltips.add(Component.translatable("tooltip." + DietConstants.MOD_ID + ".eaten")
                .withStyle(ChatFormatting.GRAY));
            tooltips.addAll(groupsTooltips);
          }
        }
      }
    }
  }
}
