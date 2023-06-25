package com.illusivesoulworks.diet.common;

import com.illusivesoulworks.diet.common.data.group.DietGroups;
import com.illusivesoulworks.diet.common.data.suite.DietSuites;
import com.illusivesoulworks.diet.common.util.DietValueGenerator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class DietEvents {

  public static void syncDatapack(ServerPlayer player) {
    DietGroups.SERVER.sync(player);
    DietSuites.SERVER.sync(player);
  }

  public static void syncDatapack(MinecraftServer server) {
    DietValueGenerator.reload(server);

    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      DietGroups.SERVER.sync(player);
      DietSuites.SERVER.sync(player);
    }
  }
}
