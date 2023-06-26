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

package com.illusivesoulworks.diet.platform.services;

import java.nio.file.Path;
import org.quiltmc.loader.api.QuiltLoader;

public class QuiltPlatformService implements IPlatformService {

  @Override
  public String getPlatformName() {
    return "Quilt";
  }

  @Override
  public boolean isModLoaded(String modId) {
    return QuiltLoader.isModLoaded(modId);
  }

  @Override
  public boolean isDevelopmentEnvironment() {
    return QuiltLoader.isDevelopmentEnvironment();
  }

  @Override
  public Path getGameDir() {
    return QuiltLoader.getGameDir();
  }
}
