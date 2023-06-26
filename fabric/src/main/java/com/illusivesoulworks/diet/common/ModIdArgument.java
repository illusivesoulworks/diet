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

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.SharedSuggestionProvider;

public class ModIdArgument implements ArgumentType<String> {

  private static final List<String> EXAMPLES = Arrays.asList("fabric-api", "diet");

  public static ModIdArgument modIdArgument() {
    return new ModIdArgument();
  }

  @Override
  public String parse(final StringReader reader) {
    return reader.readUnquotedString();
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context,
                                                            final SuggestionsBuilder builder) {
    return SharedSuggestionProvider.suggest(FabricLoader.getInstance().getAllMods().stream()
        .map(modContainer -> modContainer.getMetadata().getId()), builder);
  }

  @Override
  public Collection<String> getExamples() {
    return EXAMPLES;
  }
}
