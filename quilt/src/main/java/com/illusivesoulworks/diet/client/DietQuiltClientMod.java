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

import com.illusivesoulworks.diet.common.DietQuiltNetwork;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.screen.api.client.ScreenEvents;
import org.quiltmc.qsl.tooltip.api.client.ItemTooltipCallback;

public class DietQuiltClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient(ModContainer modContainer) {
    DietQuiltNetwork.setup();
    KeyBindingHelper.registerKeyBinding(DietKeys.get());
    ClientTickEvents.END.register(DietClientEvents::tick);
    ItemTooltipCallback.EVENT.register(
        (stack, player, context, lines) -> DietClientEvents.renderItemTooltip(
            Minecraft.getInstance().player, stack, lines));
    ScreenEvents.AFTER_RENDER.register(
        (screen, matrices, mouseX, mouseY, tickDelta) -> DietClientEvents.renderTooltip(
            screen.getClient()));
    ScreenEvents.AFTER_INIT.register((screen, client, scaledWidth, scaledHeight) -> {

      if (screen instanceof InventoryScreen inventoryScreen) {
        inventoryScreen.getButtons().add(DietClientEvents.getButton(inventoryScreen));
      }
    });
  }
}
