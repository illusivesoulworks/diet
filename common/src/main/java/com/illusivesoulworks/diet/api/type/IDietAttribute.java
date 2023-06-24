package com.illusivesoulworks.diet.api.type;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public interface IDietAttribute {

  Attribute getAttribute();

  AttributeModifier.Operation getOperation();

  double getBaseAmount();

  double getIncrement();
}
