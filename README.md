# BanHammer 

A plugin for the Bukkit framework that allows adminstrators and other trusted users to ban and kick players. The basic idea is to have a lightweight banning solution that includes all the features you help manage your server. Additionally the plugin stores all bans using the Bukkit persistance system, using permissions for all commands (or gracefully fallsback if not available) and is simple to configure.

## Features

- Kick players with a reason.
- Ban players permanently or for a specified amount of time.
- Ban offline players.
- If banned, login screen lets players know how long their ban will last.
- Broadcasts actions to all players.
- Review previous bans of a player, even after they have expired.
- Review recent bans to see what has been happening while you are away.
- Caches player names, ban times and ban lengths intelligently for fast logins.
- Reload the cache on command (useful if you add a ban from a web interface for example)
- Auto completes names when banning players, no need to type every letter.
- Multiple language support.
- Uses Bukkit persistence for data storage; you choose what is best for you!
- Supports permissions if available (falls back to OP only without)
- Simple. Nothing to setup, just put the .jar in your plugin directory.

## Requirements

- Bukkit Persistence needs to be configured in bukkit.yml
- If using MySQL for Persistence, you need a MySQL database