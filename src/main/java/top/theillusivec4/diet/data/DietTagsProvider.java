package top.theillusivec4.diet.data;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DietTagsProvider extends GenericDietTagsProvider {

  public DietTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider,
                          @Nullable ExistingFileHelper existingFileHelper) {
    super(dataGenerator, blockTagProvider, existingFileHelper);
  }

  @Override
  protected void registerTags() {
    this.init();
    List<Item> elements = createList(
        Items.APPLE,
        Items.CHORUS_FRUIT,
        Items.ENCHANTED_GOLDEN_APPLE,
        Items.GOLDEN_APPLE,
        Items.MELON,
        Items.MELON_SLICE,
        Items.SWEET_BERRIES
    );

    for (Item element : elements) {
      add(this.fruits, element);
    }

    elements = createList(
        Items.WHEAT
    );

    for (Item element : elements) {
      add(this.grains, element);
    }

    addTag(this.proteins, ItemTags.FISHES);
    elements = createList(
        Items.BEEF,
        Items.CHICKEN,
        Items.EGG,
        Items.MILK_BUCKET,
        Items.MUTTON,
        Items.PORKCHOP,
        Items.RABBIT,
        Items.ROTTEN_FLESH,
        Items.SPIDER_EYE
    );

    for (Item element : elements) {
      add(this.proteins, element);
    }

    elements = createList(
        Items.COCOA_BEANS,
        Items.HONEY_BLOCK,
        Items.SUGAR
    );

    for (Item element : elements) {
      add(this.sugars, element);
    }

    elements = createList(
        Items.BEETROOT,
        Items.BROWN_MUSHROOM,
        Items.CARROT,
        Items.DRIED_KELP,
        Items.GOLDEN_CARROT,
        Items.POISONOUS_POTATO,
        Items.POTATO,
        Items.PUMPKIN,
        Items.RED_MUSHROOM
    );

    for (Item element : elements) {
      add(this.vegetables, element);
    }

    add(this.specialFood, Items.CAKE);
    addTag(this.ingredients, Tags.Items.DYES);
  }

  @Nonnull
  @Override
  public String getName() {
    return "Diet Item Tags";
  }
}
