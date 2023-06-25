package com.illusivesoulworks.diet.mixin;

import com.illusivesoulworks.diet.api.type.IDietTracker;
import com.illusivesoulworks.diet.common.component.DietComponents;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class MixinPlayerList {

  @Inject(at = @At("TAIL"), method = "placeNewPlayer(Lnet/minecraft/network/Connection;Lnet/minecraft/server/level/ServerPlayer;)V")
  private void diet$placeNewPlayer(Connection netManager, ServerPlayer player, CallbackInfo ci) {
    DietComponents.DIET_TRACKER.maybeGet(player).ifPresent(IDietTracker::sync);
  }
}
