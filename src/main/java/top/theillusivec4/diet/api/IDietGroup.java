package top.theillusivec4.diet.api;

import java.awt.Color;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IDietGroup {

  String getName();

  Item getIcon();

  Color getColor();

  float getDefaultValue();

  int getOrder();

  double getGainMultiplier();

  double getDecayMultiplier();

  boolean contains(ItemStack stack);
}
