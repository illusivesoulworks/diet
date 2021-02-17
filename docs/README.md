# Diet
[![](http://cf.way2muchnoise.eu/versions/diet.svg)](https://www.curseforge.com/minecraft/mc-mods/diet)
[![](http://cf.way2muchnoise.eu/short_diet_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/diet/files)
[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg?&style=flat-square)](https://www.gnu.org/licenses/lgpl-3.0)
[![ko-fi](https://img.shields.io/badge/Support%20Me-Ko--fi-%23FF5E5B?style=flat-square)](https://ko-fi.com/C0C1NL4O)

Diet is a mod that facilitates the creation and management of dietary food groups in Minecraft. Diet
comes with a default configuration that creates five classical food groups (fruits, grains, vegetables,
proteins, and sugars). The mod is highly configurable; users and modpack developers can define their
own food groups, classifications, and diet effects.

[![BisectHosting](https://i.postimg.cc/prDcRzJ8/logo-final.png)](https://bisecthosting.com/illusive)

## Features

### Food Groups

![](https://i.ibb.co/BLYDcbT/diet-screen.png)

Food groups are custom dietary groups that represent the types of food that you have eaten. Each
group has a value ranging between 0% and 100% depending on how much of that particular category that
a player has eaten. These values increase depending on what types of food a player eats and every
group gradually decays when the player uses up their hunger bar.

By default, Diet comes with five classical food groups: Fruits, Grains, Proteins, Vegetables, and
Sugars.

By editing the `diet-groups.toml` configuration file in the world save's `serverconfig` folder,
users and modpack developers can create their own custom food groups. Configurable options include:
- Name
- Item Icon
- Hexcode Color
- Ordering
- Default Value
- Gain Multiplier
- Decay Multiplier

Please refer to the [wiki](https://github.com/TheIllusiveC4/Diet/wiki) for more detailed
information.

### Dietary Effects

![](https://i.ibb.co/7vmZfpD/diet-effects.png)

Dietary effects are custom rewards or penalties applied to players based on certain, configurable
food group values. These effects can be configured through the `diet-effects.toml` file in the world
save's `serverconfig` folder.

Possible effects can include any registered potion effect, vanilla and modded, as
well as modifying attributes directly (i.e. increasing maximum health by an arbitrary value).
The conditions for these effects are highly configurable, including checking specific values,
checking only subsets of groups, applying effects cumulatively for each matching test, and much
more.

Please refer to the [wiki](https://github.com/TheIllusiveC4/Diet/wiki) for more detailed
information.

### Commands

Diet registers a few commands to help aid debugging and server management.

- `/diet`
    - `get <player> <group>`
    - `set <player> <group> <value>`
    - `add <player> <group> <value>`
    - `subtract <player> <group> <value>`
    - `reset <player>`
    - `pause <player>`
    - `resume <player>`

## Downloads
- [Diet on CurseForge](https://www.curseforge.com/minecraft/mc-mods/diet/files)

## Support

Please report all bugs, issues, and feature requests to the [issue tracker](https://github.com/TheIllusiveC4/Diet/issues).

For non-technical support and questions, join the developer's [Discord](https://discord.gg/JWgrdwt).

## License

All source code and assets are licensed under LGPL 3.0.

## Donations

Donations to the developer can be sent through [Ko-fi](https://ko-fi.com/C0C1NL4O).

## Five Food Groups - Supported Mods
- [Animalium](https://www.curseforge.com/minecraft/mc-mods/animalium)
- [Aquaculture 2](https://www.curseforge.com/minecraft/mc-mods/aquaculture)
- [akkamaddi's Ashenwheat](https://www.curseforge.com/minecraft/mc-mods/akkamaddis-ashenwheat)
- [Atmospheric](https://www.curseforge.com/minecraft/mc-mods/atmospheric)
- [Autumnity](https://www.curseforge.com/minecraft/mc-mods/autumnity)
- [Better Animals Plus](https://www.curseforge.com/minecraft/mc-mods/betteranimalsplus)
- [Buzzier Bees](https://www.curseforge.com/minecraft/mc-mods/buzzier-bees)
- [Combustive Fishing](https://www.curseforge.com/minecraft/mc-mods/combustive-fishing)
- [Croptopia](https://www.curseforge.com/minecraft/mc-mods/croptopia-fabric)
- [Edible Bugs](https://www.curseforge.com/minecraft/mc-mods/edible-bugs)
- [Exotic Birds](https://www.curseforge.com/minecraft/mc-mods/exotic-birds)
- [Farmer's Delight](https://www.curseforge.com/minecraft/mc-mods/farmers-delight)
- [Fruit Trees](https://www.curseforge.com/minecraft/mc-mods/fruit-trees)
- [Ice and Fire: Dragons](https://www.curseforge.com/minecraft/mc-mods/ice-and-fire-dragons)
- [Inspirations](https://www.curseforge.com/minecraft/mc-mods/inspirations)
- [Mystical World](https://www.curseforge.com/minecraft/mc-mods/mystical-world)
- [Neapolitan](https://www.curseforge.com/minecraft/mc-mods/neapolitan)
- [Pam's HarvestCraft 2: Crops](https://www.curseforge.com/minecraft/mc-mods/pams-harvestcraft-2-crops)
- [Pam's HarvestCraft 2: Food Core](https://www.curseforge.com/minecraft/mc-mods/pams-harvestcraft-2-food-core)
- [Pam's HarvestCraft 2: Food Extended](https://www.curseforge.com/minecraft/mc-mods/pams-harvestcraft-2-food-extended)
- [Pam's HarvestCraft 2: Trees](https://www.curseforge.com/minecraft/mc-mods/pams-harvestcraft-2-trees)
- [Quark](https://www.curseforge.com/minecraft/mc-mods/quark)
- [Rats](https://www.curseforge.com/minecraft/mc-mods/rats)
- [Simple Farming](https://www.curseforge.com/minecraft/mc-mods/simple-farming)
- [Simply Tea](https://www.curseforge.com/minecraft/mc-mods/simply-tea)
- [VanillaFoodPantry Mod](https://www.curseforge.com/minecraft/mc-mods/vanillafoodpantry-mod)

Please request support by opening an issue on the [issue tracker](https://github.com/TheIllusiveC4/Diet/issues)
or opening a pull request to contribute directly.
