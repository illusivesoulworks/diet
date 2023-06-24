package com.illusivesoulworks.diet.platform.services;

import com.illusivesoulworks.diet.api.type.IDietTracker;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public interface ICapabilityService {

  Optional<? extends IDietTracker> get(Player player);
}
