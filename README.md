# CliNet
CliNet is a free, Kami-based Forge mod focused on 1.12.2 Minecraft Anarchy servers.


[Discord link for discussion and easy jar download](https://discord.gg/8sTug9x)

Original Kami: https://github.com/zeroeightysix/KAMI


## Disclaimer

Mostly a project for me to get better, so my  apologies for any funky code. 

Mod also has a couple of Heph modules, added with permission and didn't add myself. However, I will work on rewriting those eventually. 

Kami for now, while i'm working on custom base.

## Usage instructions (modified from Kami)

## Installing

KAMI is a forge mod. Start by downloading the latest version of [1.12.2 forge](https://files.minecraftforge.net/).
1. Install forge
2. Navigate to your `.minecraft` directory.
   * **Linux**: `~/.minecraft`
   * **Windows**: `%appdata%/.minecraft`
3. Navigate to the `mods` directory. If it doesn't exist, create it.
4. Obtain the CliNet `.jar` file.
   * By **downloading** it: see Discord
   * By **building** it: see [building](#building).
5. Place the `.jar` file in your mods directory.

## How do I

##### Open the GUI
Press Y.

##### Use commands
The default prefix is `.`. Commands are used through chat, use `.commands` for a list of commands.

##### Bind modules
Run `.bind <module> <key>`.

You can also use `.bind modifiers on` to allow modules to be bound to keybinds with modifiers, e.g `ctrl + shift + w` or `ctrl + c`.

##### Change command prefix
By using the command `prefix <prefix>` or after having ran KAMI (make sure it's closed), editing your configuration file (find it using `config path` in-game) and changing the value of `commandPrefix` to change the prefix.


## Contributing

You are free to clone, modify CliNet and make pull requests as you wish. To set up your development environment, make use of the following commands:

```
git clone https://github.com/Hamburger2K/CliNet/
```

On GNU/Linux, run `chmod +x gradlew` and for the following commands use `./gradlew` instead of `gradlew.bat`

Of-course you can also use a Gradle installation if you for some reason want another version of gradle

```
Open cmd in the directory
gradlew.bat setupDecompWorkspace
```
Import CliNEt into your IDE of choice. 

If you use IntelliJ, import from the `build.gradle` file and run `gradlew.bat genIntellijRuns`

If you use Eclipse run `gradlew.bat eclipse`

If you do not wish to run from an IDE, use `gradlew.bat runClient` to run KAMI.

### Building
#### Linux
You can build by running these commands (without the <>) in a terminal.
```
git clone https://github.com/Hamburger2K/CliNet/
cd KAMI

chmod +x gradlew
./gradlew <args>
```
Possible arguments:
```
build
mkdir
rmOld
copy
```
If you use more than one then it must be in that order. 

Build is required, `mkdir` makes the `mods/1.12.2` directory, `rmOld` removes old versions of KAMI\* in that directory, and `copy` copies the build release to the `mods/1.12.2` directory. 

\*`rmOld` removes any jars ending in `-release.jar`, which is the format KAMI uses. If you use any other mod that uses that naming scheme please remove old versions manually.

If you prefer copying it manually, find a file in `build/libs` called `KAMI-<minecraftVersion>-<Version>-**release**.jar` which you can copy to the `mods/1.12.2` folder of a minecraft instance that has forge installed.

Note: This assumes your minecraft folder is in the default location under your home folder.

#### Windows
You can build by running these commands in a terminal with the current directory being KAMI. (EG. `cd C:\Users\Username\Downloads\CliNet`)
```
gradlew.bat build
```

To copy on windows run `autocopy.bat`

If you prefer copying it manually, find a file in `build/libs` called `Clinet-<minecraftVersion>-<Version>-**release**.jar` which you can copy to the `mods\1.12.2` folder of a minecraft instance that has forge installed.

Note: This assumes your minecraft folder is in the default location under your %appdata% folder.

## Credits

[zeroeightysix](https://github.com/zeroeightysix) for the original [KAMI](https://github.com/zeroeightysix/KAMI)

[ZeroMemes](https://github.com/ZeroMemes) for [Alpine](https://github.com/ZeroMemes/Alpine)

[ronmamo](https://github.com/ronmamo/) for [Reflections](https://github.com/ronmamo/reflections)

The [Minecraft Forge team](https://github.com/MinecraftForge) for [forge](https://files.minecraftforge.net/)

[TBM](https://github.com/TheBritishMidget) & [Darki](https://github.com/DarkiBoi) for being OG dev

Contributors:
[hub](https://github.com/blockparole)
[Bella](https://github.com/S-B99)
[3arthqu4ke](https://github.com/3arthqu4ke)
