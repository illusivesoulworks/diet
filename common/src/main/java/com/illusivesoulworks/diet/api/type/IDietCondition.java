package com.illusivesoulworks.diet.api.type;

import com.illusivesoulworks.diet.common.impl.effect.DietEffect;
import java.util.Map;
import net.minecraft.world.entity.player.Player;

public interface IDietCondition {

  DietEffect.MatchMethod getMatchMethod();

  int getMatches(Player player, Map<String, Float> values);
}
