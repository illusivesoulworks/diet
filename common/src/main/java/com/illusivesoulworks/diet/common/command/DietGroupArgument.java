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

package com.illusivesoulworks.diet.common.command;

import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.api.type.IDietGroup;
import com.illusivesoulworks.diet.common.data.group.DietGroups;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class DietGroupArgument implements ArgumentType<IDietGroup> {
  private static final Collection<String> EXAMPLES = Arrays.asList("fruits", "vegetables");

  public static final DynamicCommandExceptionType GROUP_UNKNOWN = new DynamicCommandExceptionType(
      (group) -> Component.translatable("commands." + DietConstants.MOD_ID + ".group.unknown",
          group));

  public static DietGroupArgument group() {
    return new DietGroupArgument();
  }

  public static IDietGroup getGroup(CommandContext<CommandSourceStack> context, String name) {
    return context.getArgument(name, IDietGroup.class);
  }

  public IDietGroup parse(StringReader input) throws CommandSyntaxException {
    String name = input.readString();

    for (IDietGroup group : DietGroups.SERVER.getGroups()) {

      if (name.equals(group.getName())) {
        return group;
      }
    }
    throw GROUP_UNKNOWN.create(name);
  }

  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> ctx,
                                                            SuggestionsBuilder builder) {
    return SharedSuggestionProvider.suggest(
        DietGroups.SERVER.getGroups().stream().map(IDietGroup::getName), builder);
  }

  public Collection<String> getExamples() {
    return EXAMPLES;
  }
}
