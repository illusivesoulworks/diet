package top.theillusivec4.diet.data;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
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
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.diet.DietMod;

public class DietTagsProvider extends ItemTagsProvider {

  public DietTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider,
                          String modId, @Nullable ExistingFileHelper existingFileHelper) {
    super(dataGenerator, blockTagProvider, modId, existingFileHelper);
  }

  @Override
  protected void registerTags() {
    TagsProvider.Builder<Item> fruits = this.getOrCreateBuilder(tag("fruits"));
    List<String> elements = createList(
        "minecraft:apple",
        "minecraft:chorus_fruit",
        "minecraft:enchanted_golden_apple",
        "minecraft:golden_apple",
        "minecraft:melon_slice",
        "minecraft:sweet_berries"
    );

    for (String element : elements) {
      add(fruits, element);
    }

    TagsProvider.Builder<Item> grains = this.getOrCreateBuilder(tag("grains"));
    elements = createList(
        "minecraft:bread",
        "minecraft:cookie",
        "minecraft:wheat"
    );

    for (String element : elements) {
      add(grains, element);
    }

    TagsProvider.Builder<Item> proteins = this.getOrCreateBuilder(tag("proteins"));
    addTag(proteins, ItemTags.FISHES);
    elements = createList(
        "minecraft:beef",
        "minecraft:chicken",
        "minecraft:cooked_mutton",
        "minecraft:cooked_porkchop",
        "minecraft:cooked_beef",
        "minecraft:cooked_chicken",
        "minecraft:cooked_rabbit",
        "minecraft:egg",
        "minecraft:milk_bucket",
        "minecraft:mutton",
        "minecraft:porkchop",
        "minecraft:rabbit",
        "minecraft:rabbit_stew",
        "minecraft:rotten_flesh",
        "minecraft:spider_eye"
    );

    for (String element : elements) {
      add(proteins, element);
    }

    TagsProvider.Builder<Item> sugars = this.getOrCreateBuilder(tag("sugars"));
    elements = createList(
        "minecraft:cake",
        "minecraft:cookie",
        "minecraft:honey_bottle",
        "minecraft:pumpkin_pie"
    );

    for (String element : elements) {
      add(sugars, element);
    }

    TagsProvider.Builder<Item> vegetables = this.getOrCreateBuilder(tag("vegetables"));
    elements = createList(
        "minecraft:baked_potato",
        "minecraft:beetroot",
        "minecraft:beetroot_soup",
        "minecraft:carrot",
        "minecraft:dried_kelp",
        "minecraft:golden_carrot",
        "minecraft:mushroom_stew",
        "minecraft:poisonous_potato",
        "minecraft:potato",
        "minecraft:pumpkin_pie",
        "minecraft:rabbit_stew",
        "minecraft:suspicious_stew"
    );

    for (String element : elements) {
      add(vegetables, element);
    }

    TagsProvider.Builder<Item> specialFood = this.getOrCreateBuilder(tag("special_food"));
    add(specialFood, "minecraft:cake");
  }

  private void add(TagsProvider.Builder<Item> builder, String name) {
    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));

    if (item == null) {
      throw new IllegalArgumentException("Couldn't find required item " + name);
    } else {
      builder.add(item);
    }
  }

  private void addTag(TagsProvider.Builder<Item> builder, ITag.INamedTag<Item> tag) {
    builder.addTag(tag);
  }

  private void addOptional(TagsProvider.Builder<Item> builder, String name) {
    builder.addOptional(new ResourceLocation(name));
  }

  private void addOptionalTag(TagsProvider.Builder<Item> builder, String name) {
    builder.addOptionalTag(new ResourceLocation(name));
  }

  private ITag.INamedTag<Item> tag(String name) {
    return ItemTags.makeWrapperTag(DietMod.id(name));
  }

  private List<String> createList(String... names) {
    List<String> result = Lists.newArrayList(names);
    Collections.sort(result);
    return result;
  }

  @Nonnull
  @Override
  public String getName() {
    return "Diet Item Tags";
  }
}
