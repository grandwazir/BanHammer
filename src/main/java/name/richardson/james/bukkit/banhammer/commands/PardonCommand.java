/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * PardonCommand.java is part of BanHammer.
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
package name.richardson.james.bukkit.banhammer.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.PluginManager;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.PlayerNamePositionalArgument;
import name.richardson.james.bukkit.utilities.command.argument.SilentSwitchArgument;

import name.richardson.james.bukkit.banhammer.event.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.record.BanRecord;
import name.richardson.james.bukkit.banhammer.record.CurrentBanRecord;
import name.richardson.james.bukkit.banhammer.record.CurrentPlayerRecord;
import name.richardson.james.bukkit.banhammer.record.PlayerRecord;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerMessages.*;

public class PardonCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.pardon";
	public static final String PERMISSION_OWN = "banhammer.pardon.own";
	public static final String PERMISSION_OTHERS = "banhammer.pardon.others";
	private final Argument players;
	private final PluginManager pluginManager;
	private final EbeanServer database;
	private final SilentSwitchArgument silent;

	public PardonCommand(PluginManager pluginManager, EbeanServer database) {
		super(PARDON_COMMAND_NAME, PARDON_COMMAND_DESC);
		this.pluginManager = pluginManager;
		this.database = database;
		this.silent = SilentSwitchArgument.getInstance();
		this.players = PlayerNamePositionalArgument.getInstance(database, 0, true, PlayerRecord.PlayerStatus.BANNED);
		addArgument(silent);
		addArgument(players);
	}

	@Override
	public boolean isAsynchronousCommand() {
		return false;
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL) || permissible.hasPermission(PERMISSION_OWN) || permissible.hasPermission(PERMISSION_OTHERS);
	}

	@Override
	protected void execute() {
		boolean silent = this.silent.isSet();
		final List<String> messages = new ArrayList<String>();
		final Collection<String> players = this.players.getStrings();
		final CommandSender sender = getContext().getCommandSender();
		final Collection<CurrentBanRecord> bans = new ArrayList<CurrentBanRecord>();
		for (String playerName : players) {
			final PlayerRecord playerRecord = CurrentPlayerRecord.find(database, playerName);
			final CurrentBanRecord ban = playerRecord.getActiveBan();
			if (ban != null) {
				if (hasPermission(sender, ban.getCreator().getUuid())) {
					ban.setState(BanRecord.State.PARDONED);
					bans.add(ban);
					if (silent) messages.add(PARDON_PLAYER.asInfoMessage(playerName));
				} else {
					messages.add(PARDON_UNABLE_TO_TARGET_PLAYER.asErrorMessage(playerName));
				}
			} else {
				messages.add(PLAYER_NOT_BANNED.asInfoMessage(playerName));
			}
		}
		CurrentBanRecord.save(database, bans);
		new BanHammerPlayerPardonedEvent(bans, silent, sender.getName());
		sender.sendMessage(messages.toArray(new String[messages.size()]));
	}

	private boolean hasPermission(CommandSender sender, UUID playerUUID) {
		final boolean isSenderTargetingSelf = (getCommandSenderUUID().compareTo(playerUUID) == 0);
		return sender.hasPermission(PERMISSION_OWN) && isSenderTargetingSelf || sender.hasPermission(PERMISSION_OTHERS) && !isSenderTargetingSelf;
	}

	private UUID getCommandSenderUUID() {
		if (getContext().getCommandSender() instanceof Player) {
			return ((Player) getContext().getCommandSender()).getUniqueId();
		} else {
			return null;
		}
	}

}
