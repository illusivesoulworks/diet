# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to the format [MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH](https://mcforge.readthedocs.io/en/1.15.x/conventions/versioning/).

## [1.18.2-1.3.0.1] - 2022.04.17
### Fixed
- Fixed Bread sometimes dropping out of the default Grains group [#126](https://github.com/TheIllusiveC4/Diet/issues/126)
- Fixed possible NoClassDefFoundError crashes at the cost of potentially (marginally) longer load times [#127](https://github.com/TheIllusiveC4/Diet/issues/127)

## [1.18.2-1.3.0.0] - 2022.04.14
### Added
- Added 5-food-group support for:
  - Goat Food [#125](https://github.com/TheIllusiveC4/Diet/issues/125)
- [API] Added `IDietTracker#consume(List, int, float)` for directly adding values to a player's diet through a list of
representative or component stacks and food values
### Changed
- Updated to and requires Forge 40.0.47+

## [1.18.2-1.2.1.0] - 2022.04.04
### Added
- Added `diet:natural_regeneration` attribute for controlling natural regeneration through Diet effects [#122](https://github.com/TheIllusiveC4/Diet/issues/122)
- Added `/diet export trails` command to export data about which items are being used to generate food groups for all
  items
### Changed
- Revamped Diet value generation algorithm to be more performant and address more edge-case issues. Be aware of changes
  to food groups for existing items and report discrepancies to the issue tracker
  [#120](https://github.com/TheIllusiveC4/Diet/issues/120)
  [#83](https://github.com/TheIllusiveC4/Diet/issues/83)

## [1.18.2-1.2.0.5] - 2022.03.02
### Changed
- Updated to Minecraft 1.18.2

## [1.18.1-1.2.0.4] - 2022.02.16
### Added
- Added config option `addButton` to `diet-client.toml` for enabling/disabling the Diet button in the inventory screen

## [1.18.1-1.2.0.3] - 2022.02.10
### Added
- Added 5-food-group support for:
  - Thermal Series [#115](https://github.com/TheIllusiveC4/Diet/issues/115)
  - Minecolonies [#106](https://github.com/TheIllusiveC4/Diet/issues/106)
### Fixed
- Fixed Diet capability resetting when dying or moving dimension [#112](https://github.com/TheIllusiveC4/Diet/issues/112) [#113](https://github.com/TheIllusiveC4/Diet/issues/113)

## [1.18.1-1.2.0.2] - 2022.01.31
### Fixed
- Fixed NPE crash [#111](https://github.com/TheIllusiveC4/Diet/issues/111)
- Fixed Diet preventing certain blocks from being placed/activated [#110](https://github.com/TheIllusiveC4/Diet/issues/110)

## [1.18.1-1.2.0.1] - 2022.01.28
### Fixed
- Fixed NPE crash [#107](https://github.com/TheIllusiveC4/Diet/issues/107)
- Fixed tooltip overlays [#108](https://github.com/TheIllusiveC4/Diet/issues/108)
- Fixed fractional add amounts in attribute modifiers not being shown in the tooltip correctly

## [1.18.1-1.2.0.0] - 2022.01.27
### Changed
- Updated to Minecraft 1.18.1
- Updated to Forge 39.0+

## [1.17.1-1.1.0.0] - 2022.01.26
### Added
- Added Glow Berries to the `diet:fruits` tag.
### Changed
- Updated to Minecraft 1.17.1
- Updated to Forge 37.0+
