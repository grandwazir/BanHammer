/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * BanCommand.java is part of BanHammer.
 *
 * BanHammer is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.ban;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractAsynchronousCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;

import name.richardson.james.bukkit.banhammer.*;
import name.richardson.james.bukkit.banhammer.argument.*;
import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.banhammer.PlayerRecord;
import name.richardson.james.bukkit.banhammer.player.PlayerNotFoundException;

public class BanCommand extends AbstractAsynchronousCommand {

	public static final String PERMISSION_ALL = "banhammer.ban";
	public static final String PERMISSION_PERMANENT = "banhammer.ban.permanent";
	private static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	private final PluginConfiguration configuration;
	private final Argument player;
	private final Argument reason;
	private final SilentSwitchArgument silent;
	private final TimeMarshaller time;

	public BanCommand(final Plugin plugin, final BukkitScheduler scheduler, PluginConfiguration configuration, Server server) {
		super(plugin, scheduler);
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
		final Collection<String> playerNames = player.getStrings();
		final Collection<BanRecord> records = new ArrayList<BanRecord>();
		final boolean silent = this.silent.isSet();
		final long time = this.time.getTime();
		final PlayerRecord creatorRecord;
		try {
			creatorRecord = PlayerRecord.create(getContext().getCommandSenderUUID());
			for (String playerName : playerNames) {
				PlayerRecord playerRecord = PlayerRecord.create(playerName);
				if (isPlayerImmune(playerName)) {
					addMessage(MESSAGES.playerIsImmune(playerName));
				} else if (isBanWithinLimit()) {
					if (!playerRecord.isBanned()) {
						final BanRecord record = BanRecord.create(playerRecord, creatorRecord, reason.getString());
						if (time > 0) record.setExpiresAt((Timestamp) this.time.getDate());
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
			new BanHammerPlayerBannedEvent(records, getContext().getCommandSender(), silent);
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
		for (String limit : limitNames) {
			permissions.add(getPermissionFromLimit(limit));
		}
		return permissions;
	}

	public static String getPermissionFromLimit(final String limit) {
		return BanCommand.PERMISSION_ALL + "." + limit;
	}

	private boolean isPlayerImmune(String playerName) {
		return (configuration.getImmunePlayers().contains(playerName) || getContext().isAuthorised(PERMISSION_PERMANENT));
	}

	private boolean isBanWithinLimit() {
		if (time.getTime() == 0 && isAuthorised(PERMISSION_PERMANENT)) {
			return true;
		} else {
			for (final String limitName : this.configuration.getBanLimits().keySet()) {
				String node = getPermissionFromLimit(limitName);
				if (!isAuthorised(node)) continue;
				if (this.configuration.getBanLimits().get(limitName) >= this.time.getTime()) return true;
			}
		}
		return false;
	}

}
