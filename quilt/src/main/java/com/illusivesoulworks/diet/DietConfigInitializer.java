/*
 * Copyright (C) 2021-2023 Illusive Soulworks
 *
 * Diet is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Diet is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Diet.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.diet;

import com.illusivesoulworks.diet.common.config.DietConfigLoader;
import com.illusivesoulworks.spectrelib.config.SpectreConfigInitializer;
import org.quiltmc.loader.api.ModContainer;

public class DietConfigInitializer implements SpectreConfigInitializer {

  @Override
  public void onInitializeConfig(ModContainer modContainer) {
    DietConfigLoader.setup();
  }

  // Hotfix to avoid crashing when loaded with the Fabric-version of SpectreLib
  public void onInitializeConfig() {
    onInitializeConfig(null);
  }
}
