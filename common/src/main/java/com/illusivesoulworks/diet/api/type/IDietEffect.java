package com.illusivesoulworks.diet.api.type;

import java.util.List;
import java.util.UUID;

public interface IDietEffect {

  List<IDietCondition> getConditions();

  List<IDietAttribute> getAttributes();

  List<IDietStatusEffect> getStatusEffects();

  UUID getUuid();
}
