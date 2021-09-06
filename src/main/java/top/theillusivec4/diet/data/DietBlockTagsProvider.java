package top.theillusivec4.diet.data;

import javax.annotation.Nullable;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DietBlockTagsProvider extends BlockTagsProvider {

  public DietBlockTagsProvider(DataGenerator generatorIn, String modId,
                               @Nullable ExistingFileHelper existingFileHelper) {
    super(generatorIn, modId, existingFileHelper);
  }
}
