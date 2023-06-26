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
