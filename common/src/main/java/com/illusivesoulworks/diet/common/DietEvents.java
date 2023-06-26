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
