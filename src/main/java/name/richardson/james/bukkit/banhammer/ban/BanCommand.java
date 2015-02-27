/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 BanCommand.java is part of BanHammer.

 BanHammer is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option) any
 later version.

 BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.ban;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractAsynchronousCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;

import name.richardson.james.bukkit.banhammer.BanHammerMessages;
import name.richardson.james.bukkit.banhammer.BanHammerMessagesCreator;
import name.richardson.james.bukkit.banhammer.BanHammerPluginConfiguration;
import name.richardson.james.bukkit.banhammer.ban.event.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.model.BanRecord;
import name.richardson.james.bukkit.banhammer.model.PlayerRecord;
import name.richardson.james.bukkit.banhammer.argument.PlayerNamePositionalArgument;
import name.richardson.james.bukkit.banhammer.argument.ReasonPositionalArgument;
import name.richardson.james.bukkit.banhammer.argument.SilentSwitchArgument;
import name.richardson.james.bukkit.banhammer.argument.TimeMarshaller;
import name.richardson.james.bukkit.banhammer.argument.TimeOptionArgument;
import name.richardson.james.bukkit.banhammer.model.PlayerNotFoundException;
import org.bukkit.Bukkit;

public class BanCommand extends AbstractAsynchronousCommand {

	public static final String PERMISSION_ALL = "banhammer.ban";
	public static final String PERMISSION_PERMANENT = "banhammer.ban.permanent";
	private static final BanHammerMessages MESSAGES = BanHammerMessagesCreator.getColouredMessages();
	private final BanHammerPluginConfiguration configuration;
	private final Argument player;
	private final Argument reason;
	private final SilentSwitchArgument silent;
	private final TimeMarshaller time;
        private final Plugin plugin;
        private final BukkitScheduler scheduler;
        
	public BanCommand(final Plugin plugin, final BukkitScheduler scheduler, BanHammerPluginConfiguration configuration, Server server) {
		super(plugin, scheduler);
                this.plugin = plugin;
                this.scheduler = scheduler;
		this.configuration = configuration;
		player = PlayerNamePositionalArgument.getInstance(server, 0, true);
		time = (TimeMarshaller) TimeOptionArgument.getInstance();
		silent = SilentSwitchArgument.getInstance();
		reason = ReasonPositionalArgument.getInstance(1, true);
		addArgument(silent);
		addArgument((Argument) time);
		addArgument(player);
		addArgument(reason);
	}

	@Override
	public void execute() {
		Collection<String> playerNames = player.getStrings();
		final Collection<BanRecord> records = new ArrayList<BanRecord>();
		final boolean silent = this.silent.isSet();
		long time = this.time.getTime();
		PlayerRecord creatorRecord;
		try {
			creatorRecord = PlayerRecord.create(getContext().getCommandSenderUUID());
			for (String playerName : playerNames) {
				PlayerRecord playerRecord = PlayerRecord.create(playerName);
				if (isPlayerImmune(playerName)) {
					addMessage(MESSAGES.playerIsImmune(playerName));
				} else if (isBanWithinLimit()) {
					if (!playerRecord.isBanned()) {
						BanRecord record = BanRecord.create(creatorRecord, playerRecord, reason.getString());
						if (time > 0) record.setExpiryDuration(this.time.getTime());
						records.add(record);
						record.save();
						if (silent) addMessage(MESSAGES.playerBanned(playerName));
					} else {
						addMessage(MESSAGES.playerIsAlreadyBanned(playerName));
					}
				} else {
					addMessage(MESSAGES.unableToBanForThatLong(playerName));
				}
			}
                       this.scheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
                        @Override
                        public void run() {
                            new BanHammerPlayerBannedEvent(records, getContext().getCommandSender(), silent);
                                }
                        }, 0L);
                        } catch (PlayerNotFoundException e) {
			addMessage(MESSAGES.playerLookupException());
		}
	}

	@Override public String getDescription() {
		return MESSAGES.banCommandDescription();
	}

	@Override public String getName() {
		return MESSAGES.banCommandName();
	}

	@Override public Set<String> getPermissions() {
		Set<String> limitNames = configuration.getBanLimits().keySet();
		Set<String> permissions = new HashSet<>();
		permissions.add(PERMISSION_ALL);
		permissions.add(PERMISSION_PERMANENT);
		for (String limit : limitNames) {
			permissions.add(getPermissionFromLimit(limit));
		}
		return permissions;
	}

	public static String getPermissionFromLimit(final String limit) {
		return BanCommand.PERMISSION_ALL + "." + limit;
	}

	private boolean isPlayerImmune(String playerName) {
		return (configuration.getImmunePlayers().contains(playerName) && !getContext().isAuthorised(PERMISSION_PERMANENT));
	}

	private boolean isBanWithinLimit() {
		if (time.getTime() == 0 && isAuthorised(PERMISSION_PERMANENT)) {
			return true;
		} else {
			for (final Map.Entry<String, Long> limit : configuration.getBanLimits().entrySet()) {
				String node = getPermissionFromLimit(limit.getKey());
				if (!isAuthorised(node)) continue;
				if (limit.getValue() >= time.getTime()) return true;
			}
		}
		return false;
	}

}
