package top.theillusivec4.diet.data;

import javax.annotation.Nullable;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import top.theillusivec4.diet.DietMod;

public class DietBlockTagsProvider extends BlockTagsProvider {

  public DietBlockTagsProvider(DataGenerator generatorIn, @Nullable ExistingFileHelper existingFileHelper) {
    super(generatorIn, DietMod.MOD_ID, existingFileHelper);
  }
}
