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
package name.richardson.james.bukkit.banhammer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.PlayerNamePositionalArgument;
import name.richardson.james.bukkit.utilities.command.argument.SilentSwitchArgument;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.ban.event.BanHammerPlayerPardonedEvent;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.*;

public class PardonCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.pardon";
	public static final String PERMISSION_OWN = "banhammer.pardon.own";
	public static final String PERMISSION_OTHERS = "banhammer.pardon.others";
	private final BanRecordManager banRecordManager;
	private final PlayerRecordManager playerRecordManager;
	private final Argument players;
	private final PluginManager pluginManager;
	private final SilentSwitchArgument silent;

	public PardonCommand(PluginManager pluginManager, BanRecordManager banRecordManager, PlayerRecordManager playerRecordManager) {
		super(PARDON_COMMAND_NAME, PARDON_COMMAND_DESC);
		this.pluginManager = pluginManager;
		this.banRecordManager = banRecordManager;
		this.playerRecordManager = playerRecordManager;
		this.silent = SilentSwitchArgument.getInstance();
		this.players = PlayerNamePositionalArgument.getInstance(playerRecordManager, 0, true, PlayerRecordManager.PlayerStatus.BANNED);
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
		for (String playerName : players) {
			BanRecord record = (playerRecordManager.exists(playerName)) ? playerRecordManager.find(playerName).getActiveBan() : null;
			if (record == null) {
				messages.add(PLAYER_NOT_BANNED.asInfoMessage(playerName));
			} else if (hasPermission(sender, record.getCreator().getName())) {
				record.setState(BanRecord.State.PARDONED);
				banRecordManager.save(record);
				if (silent) messages.add(PARDON_PLAYER.asInfoMessage(playerName));
				BanHammerPlayerPardonedEvent event = new BanHammerPlayerPardonedEvent(record, sender, silent);
				pluginManager.callEvent(event);
			} else {
				messages.add(PARDON_UNABLE_TO_TARGET_PLAYER.asErrorMessage(playerName));
			}
		}
		sender.sendMessage(messages.toArray(new String[messages.size()]));
	}

	private boolean hasPermission(CommandSender sender, String creatorName) {
		final boolean isSenderTargetingSelf = (creatorName.equalsIgnoreCase(sender.getName()));
		return sender.hasPermission(PERMISSION_OWN) && isSenderTargetingSelf || sender.hasPermission(PERMISSION_OTHERS) && !isSenderTargetingSelf;
	}

}
