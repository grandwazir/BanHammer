BanHammer: bans, kicks and records.
====================================

BanHammer is a plugin for the Minecraft wrapper [Bukkit](http://bukkit.org/) that allows adminstrators and other trusted users to ban and kick players. The basic idea is to have a lightweight banning solution that includes all the features you help manage your server. Additionally the plugin stores all bans using the Bukkit persistance system, using permissions for all commands and is simple to configure.

## Features

- Simple and easy to configure.
- Ban offline and online players.
- Supports permenant as well as temporary bans.
- Ability to pardon bans without removing the record itself.
- Supports setting a list of players who are immune to banning.
- Custom configurable limits to prevent moderators banning for too long.
- Player name matching when kicking or banning players (no need to type every letter)
- Can broadcast notifications to other players.
- Review previous bans of a player, even after they have expired.
- Review recent bans to see what has been happening while you have been away.
- Uses Bukkit persistence for data storage; you choose what is best for you.
- Supports built in Bukkit permissions, operators have all commands by default.

## License

BanHammer is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

## Documentation

If you are a server administrator, many of the features specific to BanHammer are documented [on the wiki](https://github.com/grandwazir/BanHammer/wiki). If you are looking to change the messages used in BanHammer or localise the plugin into your own language you will want to look at [this page](https://github.com/grandwazir/BukkitUtilities/wiki/Localisation) instead.

If you are a developer you may find the [JavaDocs](http://grandwazir.github.com/BanHammer/apidocs/index.html) and a [Maven website](http://grandwazir.github.com/BanHammer/) useful to you as well.

## Installation

Before installing, you need to make sure you are running at least the latest [recommended build](http://dl.bukkit.org/latest-rb/craftbukkit.jar) for Bukkit. Support is only given for problems when using a recommended build. This does not mean that the plugin will not work on other versions of Bukkit, the likelihood is it will, but it is not supported.

### Getting the latest version

The best way to install BanHammer is to use the [symbolic link](http://repository.james.richardson.name/symbolic/BanHammer.jar) to the latest version. This link always points to the latest version of BanHammer, so is safe to use in scripts or update plugins. A [feature changelog](https://github.com/grandwazir/BanHammer/wiki/changelog) is also available.

### Getting older versions

Alternatively [older versions](http://repository.james.richardson.name/releases/name/richardson/james/bukkit/ban-hammer/) are available as well, however they are not supported. If you are forced to use an older version for whatever reason, please let me know why by [opening a issue](https://github.com/grandwazir/BanHammer/issues/new) on GitHub.

### Building from source

You can also build BanHammer from the source if you would prefer to do so. This is useful for those who wish to modify BanHammer before using it. Note it is no longer necessary to do this to alter messages in the plugin. Instead you should read the documentation on how to localise the plugin instead. This assumes that you have Maven and git installed on your computer.

    git clone git://github.com/grandwazir/BanHammer.git
    cd BanHammer
    mvn install

## Reporting issues

If you are a server administrator and you are requesting support in installing or using the plugin you should [make a post](http://dev.bukkit.org/server-mods/banhammer/forum/create-thread/) in the forum on BukkitDev. If you want to make a bug report or feature request please do so using the [issue tracking](https://github.com/grandwazir/BanHammer/issues) on GitHub.
