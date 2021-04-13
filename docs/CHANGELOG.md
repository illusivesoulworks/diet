# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to the format [MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH](https://mcforge.readthedocs.io/en/1.15.x/conventions/versioning/).

## [1.16.5-0.24] - 2021.04.12
### Added
- Added 5-food-group support for:
  - Delicate and Dainty
- Added `/diet export` for logging diet information for foods

## [1.16.5-0.23] - 2021.04.05
### Added
- Added 5-food-group support for:
  - Gilded Ingot
  - Supplementaries
  - Tinkers' Construct
  - Turkish Meals
  - Twilight Forest
### Changed
- Updated 5-food-group support for:
  - Create

## [1.16.5-0.22] - 2021.04.02
### Added
- Added Russian localization (thanks LEDshade!) [#32](https://github.com/TheIllusiveC4/Diet/pull/32)

## [1.16.5-0.21] - 2021.04.01
### Added
- Added five-food-group support for:
  - Endless Oceans: Adventure
  - EvilCraft
  - Gaia Dimension
  - Glow Bats
  - Greek Fantasy
  - Kray's Magic Candles
  - Mowzie's Mobs
  - Nether Soups Mob
  - Turtlemancy
### Changed
- Updated five-food-group support for:
  - Fins and Tails
  - Terrain Incognita

## [1.16.5-0.20] - 2021.03.31
### Added
- Added five-food-group support for:
  - ForageCraft
### Changed
- Updated five-food-group support for:
  - Abnormal's Delight
  - Alex's Mobs
  - Farmer's Delight

## [1.16.5-0.19] - 2021.03.30
### Added
- Added five-food-group support for:
  - Conjurer's Cookbook
  - Silent's Gems (Experimental)
- More API methods
### Fixed
- Fixed Chinese localization not appearing correctly

## [1.16.5-0.18] - 2021.03.23
### Fixed
- Fixed players not gaining diet values when eating food to maximum fullness [#27](https://github.com/TheIllusiveC4/Diet/issues/27)

## [1.16.5-0.17] - 2021.03.16
### Added
- Added five-food-group support for:
  - Food Enhancements
- Added "RESET" to config option for death penalties to reset values to defaults
### Changed
- Updated five-food-group support for:
  - Autumnity
  - Bloom and Gloom
- Players no longer gain diet values when full

## [1.16.5-0.16] - 2021.03.13
### Added
- Partial Taiwanese translation (thanks Pancakes0228!) [#22](https://github.com/TheIllusiveC4/Diet/pull/22)
- Added five-food-group support for:
  - Forbidden and Arcanus
  - Good Night's Sleep
  - Silent's Mechanisms
  - Untitled Duck Mod
### Fixed
- Fixed potential NPE crashes [#23](https://github.com/TheIllusiveC4/Diet/issues/23)

## [1.16.5-0.15] - 2021.03.10
### Added
- Added five-food-group support for:
  - Pandoras Creatures
### Fixed
- Fixed potential NPE crashes [#21](https://github.com/TheIllusiveC4/Diet/issues/21)

## [1.16.5-0.14] - 2021.03.08
### Added
- Added five-food-group support for:
  - Abundance
  - Bayou Blues
  - Blueberry
  - Drop the Meat
  - Golden Beetroot Mod
- Added percentage-based death penalty config option [#17](https://github.com/TheIllusiveC4/Diet/issues/17)
- Added food quality overrides list config option
### Changed
- Increased overall gain rate of all foods for every food group by 20%

## [1.16.5-0.13] - 2021.03.03
### Fixed
- Fixed GUI cross-mod compatibility issues [#16](https://github.com/TheIllusiveC4/Diet/issues/16)
- Fixed food blocks not giving diet values [#6](https://github.com/TheIllusiveC4/Diet/issues/6)

## [1.16.5-0.12] - 2021.03.02
### Changed
- Polished up dietary effects tooltip
### Fixed
- [API] Fixed potential infinite loop in IMC calls [#15](https://github.com/TheIllusiveC4/Diet/issues/15)

## [1.16.5-0.11] - 2021.03.01
### Added
- Added five-food-group support for:
  - Abnormals Delight
  - Better Default Biomes 
  - Creatures and Beasts
  - Environmental
  - Neapolitan (new foods)
  - Netherite Spuds
  - Pumpkin Spice Everything
  - Survival Plus
  - Trail Mix
  - VanillaTweaks
  - XercaMod
### Changed
- Doubled overall gain rate of all foods for every food group
### Fixed
- Fixed Pam's HarvestCraft 2 - Trees bug where nutmegs were showing a diet tooltip erroneously

## [1.16.5-0.10] - 2021.02.27
### Added
- Added five-food-group support for:
  - Bettas
  - Fins and Tails 
  - Frozen Up
  - Omni

## [1.16.5-0.9] - 2021.02.27
### Fixed
- Categorized missing item from Resourceful Bees

## [1.16.5-0.8] - 2021.02.27
### Added
- Added five-food-group support for:
  - Ars Nouveau
  - Better End (Forge)
  - Integrated Dynamics
  - Meet Your Fight
  - PneumaticCraft: Repressurized
  - Resourceful Bees
  - Oh The Biomes You'll Go
  - Undergarden
### Fixed
- Fixed diet values not syncing when changing dimensions [#9](https://github.com/TheIllusiveC4/Diet/issues/9)

## [1.16.5-0.7] - 2021.02.21
### Changed
- [API] Streamlined items/blocks API
- Removed Enchanted Golden Apple from most default food groups
- Updated tooltips for non-food items
### Fixed
- Fixed diet values updating on cake blocks even when players did not actually eat it

## [1.16.5-0.6] - 2021.02.20
### Added
- Added five-food-group support for:
  - Caves and Cliffs Backport
  - Caves and Cliffs Mod
  - Create
  - Cyclic  
  - Fantasy Mounts 
  - Pickle Tweaks  
  - Silent Gear
  - Terra Incognita  
  - The Bumblezone
  - Wyrmroost

## [1.16.5-0.5] - 2021.02.20
### Fixed
- Fixed missing items in Crock Pot for the default five food groups
- Fixed crash with cake item/block

## [1.16.5-0.4] - 2021.02.20
### Added
- [API] Added cancelable events: DietEvent.ConsumeItem, DietEvent.ConsumeBlock, DietEvent.ApplyDecay
### Changed
- Updated some integration classifications for the default five food groups

## [1.16.5-0.3] - 2021.02.19
### Fixed
- Attempted fix for config race condition [#7](https://github.com/TheIllusiveC4/Diet/issues/7)

## [1.16.5-0.2] - 2021.02.18
### Added
- Added five-food-group support for:
  - Artifacts
  - Alex's Mobs
  - Berry Good  
  - Bloom and Gloom
  - Cookielicious  
  - Crockpot
  - Druidcraft  
  - Eidolon  
  - Enhanced Mushrooms
  - Extra Foods  
  - Jellyfishing
  - The Endergetic Expansion
  - The Outer End
  - Peculiars  
  - Seasonals  
  - Upgrade Aquatic
  - Vanilla Cookbook  

## [1.16.5-0.1] - 2021.02.18
Initial beta release
