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

BanHammer - Bans, kicks and records

Version: v1.2.1

A lightweight and fully featured banning solution that makes administrating your server easier. It allows administrators and other trusted users to ban and kick players. Additionally the plugin stores all bans using the Bukkit persistence system, using permissions for all commands (or gracefully falls back if not available) and is simple to configure.

Features:

    Kick players with a reason.
    Ban players permanently or for a specified amount of time.
    Ban offline players.
    If banned, login screen lets players know how long their ban will last.
    Broadcasts actions to all players.
    Review previous bans of a player, even after they have expired.
    Review recent bans to see what has been happening while you are away.
    Caches player names, ban times and ban lengths intelligently for fast logins.
    Reload the cache on command (useful if you add a ban from a web interface for example)
    Auto completes names when banning players, no need to type every letter.
    Multiple language support.
    Uses Bukkit persistence for data storage; you choose what is best for you!
    Supports permissions if available (falls back to OP only without)
    Simple. Nothing to setup, just put the .jar in your plugin directory.

Requirements

    Bukkit Persistence needs to be configured in bukkit.yml
    If using MySQL for Persistence, you need a MySQL database

Downloads:

    Download the latest version from armathia.net (temporary due to issues at GitHub)
    View the source code on Github.

Commands:

    /ban (banhammer.ban)
    /tempban (banhammer.tempban)
    /kick (banhammer.kick)
    /pardon (banhammer.pardon)
    /bh check (banhammer.check)
    /bh history (banhammer.history)
    /bh purge (banhammer.purge)
    /bh recent (banhammer.recent)
    /bh reload (banhammer.reload)

Examples uses:

You can ban players in a variety of interesting ways. Have a look at the examples below:

    /ban MrChuckles Being silly - (ban MrChuckles permanently with a reason)
    /tempban HarryField 1 day Testing (ban HarryField for one day)
    /tempban HorribleGriefer 3 months Testing (same as above but this time for 3 months)
    /ban -f TerryTibbs Testing the plugin (ban the offline player TerryTibbs permanently with a reason)
    /tempban -f TerryTibbs 2 weeks Testing (ban the offline player TerryTibbs for two weeks)

You can check the ban history of a player to see how many times they have been banned before. Here are some examples:

    /bh check MrChuckles - (check if MrChuckles has any active bans)
    /bh history MrChuckles - (show all of MrChuckles bans even if they have expired)

You can also choose to pardon players in the same way; -a for active bans and -A to pardon all bans.

    /pardon MrChuckles - (pardon MrChuckles and delete any active bans)
    /bh purge MrChuckles - (delete all of MrChuckles bans even if they have expired)

Screenshots:

    What players see when they are temporarily banned.
    What players see when they are permanently banned.

Changelog:

Version 1.1.1

    Fixed bug where new commands were looking for bh. type permissions.

Version 1.1.0

    Fixed a bug where banned players could join
    Separated commands to make them easier to use
    Fixed various typos in plugin.yml
    Fixed a bug where player's names were not being automatically completed correctly
    Reasons are now required when banning someone
    CamelCase BanHammer in server log. All messages are now consistant.
    When temp banning a player, we also notify everyone about the time of the ban.
    history now shows a total of many bans a player has.
    check now shows the details of the current ban.
    Pardon now only removes active bans. To remove others use purge instead.

Version: 1.0.1

    Fixed various typos
    Permanent bans displayed as 'permanent' rather than 0 second in ban history.

Version: 1.0.0

    Initial release.
