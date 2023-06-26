package com.illusivesoulworks.diet.common.component;

import com.illusivesoulworks.diet.common.capability.PlayerDietTracker;
import dev.onyxstudios.cca.api.v3.component.Component;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class DietComponent extends PlayerDietTracker implements Component {

  public DietComponent(Player player) {
    super(player);
  }

  @Override
  public void readFromNbt(@Nonnull CompoundTag tag) {
    this.load(tag);
  }

  @Override
  public void writeToNbt(@Nonnull CompoundTag tag) {
    this.save(tag);
  }
}
