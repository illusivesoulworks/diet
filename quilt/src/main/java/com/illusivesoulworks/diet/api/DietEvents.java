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

package com.illusivesoulworks.diet.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class DietEvents {

  public static final Event<ConsumeItemStack> CONSUME_STACK =
      EventFactory.createArrayBacked(ConsumeItemStack.class, (listeners) -> (stack, player) -> {

        for (ConsumeItemStack listener : listeners) {

          if (!listener.consumeItemStack(stack, player)) {
            return true;
          }
        }
        return false;
      });

  public static final Event<ApplyDecay> APPLY_DECAY =
      EventFactory.createArrayBacked(ApplyDecay.class, (listeners) -> (player) -> {

        for (ApplyDecay listener : listeners) {

          if (!listener.applyDecay(player)) {
            return true;
          }
        }
        return false;
      });

  public static final Event<ApplyEffect> APPLY_EFFECT =
      EventFactory.createArrayBacked(ApplyEffect.class, (listeners) -> (player) -> {

        for (ApplyEffect listener : listeners) {

          if (!listener.applyEffect(player)) {
            return true;
          }
        }
        return false;
      });

  @FunctionalInterface
  public interface ConsumeItemStack {

    boolean consumeItemStack(ItemStack stack, Player player);
  }

  @FunctionalInterface
  public interface ApplyDecay {

    boolean applyDecay(Player player);
  }

  @FunctionalInterface
  public interface ApplyEffect {

    boolean applyEffect(Player player);
  }
}
