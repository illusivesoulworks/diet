package top.theillusivec4.diet.api;

import java.awt.Color;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.Tags;

public interface IDietGroup {

  String getName();

  Item getIcon();

  Color getColor();

  float getDefaultValue();

  int getOrder();

  double getGainMultiplier();

  double getDecayMultiplier();

  Tags.IOptionalNamedTag<Item> getTag();

  boolean contains(ItemStack stack);
}
