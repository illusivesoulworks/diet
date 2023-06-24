package com.illusivesoulworks.diet.api.util;

public record DietColor(int red, int blue, int green) {

  public static final DietColor GRAY = new DietColor(128, 128, 128);

  public int getRGB() {
    return ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF));
  }
}
