package com.illusivesoulworks.diet.api.type;

import net.minecraft.world.effect.MobEffect;

public interface IDietStatusEffect {

  MobEffect getEffect();

  int getBasePower();

  int getIncrement();
}
