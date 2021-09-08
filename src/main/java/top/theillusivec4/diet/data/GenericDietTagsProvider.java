package top.theillusivec4.diet.data;

import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import top.theillusivec4.diet.DietMod;

public abstract class GenericDietTagsProvider extends ItemTagsProvider {

  protected TagsProvider.Builder<Item> fruits;
  protected TagsProvider.Builder<Item> grains;
  protected TagsProvider.Builder<Item> proteins;
  protected TagsProvider.Builder<Item> sugars;
  protected TagsProvider.Builder<Item> vegetables;
  protected TagsProvider.Builder<Item> specialFood;
  protected TagsProvider.Builder<Item> ingredients;

  public GenericDietTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider,
                                 @Nullable ExistingFileHelper existingFileHelper) {
    super(dataGenerator, blockTagProvider, DietMod.MOD_ID, existingFileHelper);
  }

  protected void init() {
    this.fruits = this.getOrCreateBuilder(tag("fruits"));
    this.grains = this.getOrCreateBuilder(tag("grains"));
    this.proteins = this.getOrCreateBuilder(tag("proteins"));
    this.sugars = this.getOrCreateBuilder(tag("sugars"));
    this.vegetables = this.getOrCreateBuilder(tag("vegetables"));
    this.specialFood = this.getOrCreateBuilder(tag("special_food"));
    this.ingredients = this.getOrCreateBuilder(tag("ingredients"));
  }

  protected void add(TagsProvider.Builder<Item> builder, Item item) {
    builder.addItemEntry(item);
  }

  protected void addTag(TagsProvider.Builder<Item> builder, ITag.INamedTag<Item> tag) {
    builder.addTag(tag);
  }

  protected void addOptional(TagsProvider.Builder<Item> builder, String name) {
    builder.addOptional(new ResourceLocation(name));
  }

  protected void addOptionalTag(TagsProvider.Builder<Item> builder, String name) {
    builder.addOptionalTag(new ResourceLocation(name));
  }

  protected ITag.INamedTag<Item> tag(String name) {
    return ItemTags.makeWrapperTag(DietMod.id(name));
  }

  @SafeVarargs
  protected static <T> List<T> createList(Comparator<T> comparator, T... names) {
    List<T> result = Lists.newArrayList(names);
    result.sort(comparator);
    return result;
  }

  protected static List<String> createList(String... names) {
    return createList(Comparator.comparing(String::toString), names);
  }

  protected static List<Item> createList(Item... items) {
    return createList(Comparator.comparing(e -> Objects.requireNonNull(e.getRegistryName())),
        items);
  }
}
