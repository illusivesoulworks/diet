package top.theillusivec4.diet.common.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.common.group.DietGroups;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class DietGroupArgument implements ArgumentType<IDietGroup> {
    private static final Collection<String> EXAMPLES = Arrays.asList("fruits", "vegetables");

    public static final DynamicCommandExceptionType
        GROUP_UNKNOWN = new DynamicCommandExceptionType(
        (group) -> MutableComponent.create(new TranslatableContents("commands." + DietMod.MOD_ID + ".group.unknown", group)));

    public static DietGroupArgument group() {
        return new DietGroupArgument();
    }

    public static IDietGroup getGroup(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, IDietGroup.class);
    }

    public IDietGroup parse(StringReader input) throws CommandSyntaxException {
        String name = input.readString();

        for (IDietGroup group : DietGroups.get()) {

            if (name.equals(group.getName())) {
                return group;
            }
        }
        throw GROUP_UNKNOWN.create(name);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> ctx,
                                                              SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(DietGroups.get().stream().map(IDietGroup::getName), builder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}