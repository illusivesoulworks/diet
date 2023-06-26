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

package com.illusivesoulworks.diet.common.data;

import com.illusivesoulworks.diet.DietCommonMod;
import com.illusivesoulworks.diet.common.data.suite.DietSuites;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader;

public class DietQuiltSuites extends DietSuites implements IdentifiableResourceReloader {

  @Nonnull
  @Override
  public ResourceLocation getQuiltId() {
    return DietCommonMod.resource("suites");
  }
}
