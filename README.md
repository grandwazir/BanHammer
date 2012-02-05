BanHammer - bans, kicks and records.
====================================

BanHammer is a plugin for the Minecraft wrapper [Bukkit](http://bukkit.org/) that allows adminstrators and other trusted users to ban and kick players. The basic idea is to have a lightweight banning solution that includes all the features you help manage your server. Additionally the plugin stores all bans using the Bukkit persistance system, using permissions for all commands and is simple to configure.

## Features

- Simple and easy to configure.
- Supports permenant as well as temporary bans.
- Custom configurable limits to prevent moderators banning for too long.
- Ban offline and online players.
- Player name matching when kicking or banning players (no need to type every letter)
- Can broadcast notifications to other players.
- Review previous bans of a player, even after they have expired.
- Review recent bans to see what has been happening while you have been away.
- Banned names are cached for quick logins.
- Reload the banned name cache on command (useful if you alter the database outside the plugin)
- Uses Bukkit persistence for data storage; you choose what is best for you.
- Supports built in Bukkit permissions, operators have all commands by default.
- Command help system, simply type /bh.

## Requirements

- [Bukkit Persistence](https://github.com/grandwazir/BanHammer/wiki/database) needs to be configured in bukkit.yml
- If using MySQL for Persistence, you need a MySQL database

## Installation

### Ensure you are using the latest recommended build.

Before installing, you need to make sure you are running at least the latest [recommended build](http://repo.bukkit.org/service/local/artifact/maven/content?r=releases&g=org.bukkit&a=craftbukkit&v=RELEASE) for Bukkit. Support is only given for problems when using a recommended build. This does not mean that the plugin will not work on other versions of Bukkit, the likelihood is it will, but it is not supported.

### Getting BanHammer

The best way to install BanHammer is to use the [symbolic link](http://repository.james.richardson.name/symbolic/BanHammer.jar) to the latest version. This link always points to the latest version of BanHammer, so is safe to use in scripts or update plugins. Additionally you can to use the [RSS feed](http://dev.bukkit.org/server-mods/banhammer/files.rss) provided by BukkitDev as this also includes a version changelog.
    
Alternatively [older versions](http://repository.james.richardson.name/releases/name/richardson/james/bukkit/ban-hammer/) are available as well, however they are not supported. If you are forced to use an older version for whatever reason, please let me know why by [opening a issue](https://github.com/grandwazir/BanHammer/issues/new) on GitHub.

## Configuration

1. [Configure permissions](https://github.com/grandwazir/BanHammer/wiki/permissions) if necessary.
2. Optionally configure your ban limits (config.yml) and assign them to moderators.

All documentation for BanHammer is available on the [GitHub wiki](https://github.com/grandwazir/BanHammer/wiki), including [example usage](https://github.com/grandwazir/BanHammer/wiki/Instructions) and details on [how to configure](https://github.com/grandwazir/BanHammer/wiki/Permissions) the plugin. 

