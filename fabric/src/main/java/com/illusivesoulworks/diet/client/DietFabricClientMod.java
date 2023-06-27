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

import com.illusivesoulworks.diet.common.DietFabricNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class DietFabricClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    DietFabricNetwork.setup();
    KeyBindingHelper.registerKeyBinding(DietKeys.get());
    ClientTickEvents.END_CLIENT_TICK.register(DietClientEvents::tick);
    ItemTooltipCallback.EVENT.register(
        (stack, context, lines) -> DietClientEvents.renderItemTooltip(
            Minecraft.getInstance().player, stack, lines));
    ScreenEvents.BEFORE_INIT.register(
        (client, screen, scaledWidth, scaledHeight) -> ScreenEvents.afterRender(screen).register(
            (screen1, matrices, mouseX, mouseY, tickDelta) -> DietClientEvents.renderEffectsTooltip(screen1, matrices)));
    ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {

      if (screen instanceof InventoryScreen inventoryScreen) {
        Screens.getButtons(inventoryScreen).add(DietClientEvents.getButton(inventoryScreen));
      }
    });
  }
}
