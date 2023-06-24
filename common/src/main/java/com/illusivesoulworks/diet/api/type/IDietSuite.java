package com.illusivesoulworks.diet.api.type;

import java.util.List;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;

public interface IDietSuite {

  String getName();

  Set<IDietGroup> getGroups();

  List<IDietEffect> getEffects();

  CompoundTag save();
}
