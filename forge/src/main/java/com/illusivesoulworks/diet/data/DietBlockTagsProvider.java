package com.illusivesoulworks.diet.data;

import com.illusivesoulworks.diet.DietConstants;
import javax.annotation.Nullable;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DietBlockTagsProvider extends BlockTagsProvider {

  public DietBlockTagsProvider(DataGenerator generatorIn,
                               @Nullable ExistingFileHelper existingFileHelper) {
    super(generatorIn, DietConstants.MOD_ID, existingFileHelper);
  }
}
