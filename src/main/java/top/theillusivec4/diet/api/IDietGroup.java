package top.theillusivec4.diet.api;

import java.awt.Color;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;

public interface IDietGroup {

  String getName();

  Item getIcon();

  Color getColor();

  float getDefaultValue();

  int getOrder();

  double getGainMultiplier();

  double getDecayMultiplier();

  boolean isBeneficial();

  Tags.IOptionalNamedTag<Item> getTag();

  boolean contains(ItemStack stack);
}
