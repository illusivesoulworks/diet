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
import com.illusivesoulworks.diet.client.screen.DynamicButton;
import com.illusivesoulworks.diet.common.integration.CuriosIntegration;
import com.illusivesoulworks.diet.common.integration.IntegrationManager;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DietConstants.MOD_ID, value = Dist.CLIENT)
public class DietClientEventsListener {

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void initGui(final ScreenEvent.Init.Post evt) {
    Screen screen = evt.getScreen();

    if (screen instanceof InventoryScreen ||
        (IntegrationManager.isCuriosLoaded() && CuriosIntegration.isCuriosScreen(screen))) {
      DynamicButton button = DietClientEvents.getButton(screen);

      if (button != null) {
        evt.addListener(button);
      }
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void tick(final TickEvent.ClientTickEvent evt) {

    if (evt.phase == TickEvent.Phase.END) {
      DietClientEvents.tick(Minecraft.getInstance());
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void tooltip(final ItemTooltipEvent evt) {
    Player player = evt.getEntity();
    List<Component> tooltips = evt.getToolTip();
    ItemStack stack = evt.getItemStack();
    DietClientEvents.renderItemTooltip(player, stack, tooltips);
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void renderTooltip(ScreenEvent.Render.Post evt) {
    DietClientEvents.renderEffectsTooltip(evt.getScreen(), evt.getGuiGraphics());
  }
}
