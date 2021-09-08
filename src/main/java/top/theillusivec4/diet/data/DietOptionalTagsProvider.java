package top.theillusivec4.diet.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DietOptionalTagsProvider extends GenericDietTagsProvider {

  public DietOptionalTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider,
                                  @Nullable ExistingFileHelper existingFileHelper) {
    super(dataGenerator, blockTagProvider, existingFileHelper);
  }

  @Override
  protected void registerTags() {
    createList(
        ""
    );
  }

  @Nonnull
  @Override
  public String getName() {
    return "Diet Optional Item Tags";
  }
}
