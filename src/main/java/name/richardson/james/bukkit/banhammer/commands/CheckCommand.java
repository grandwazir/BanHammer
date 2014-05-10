/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * CheckCommand.java is part of BanHammer.
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

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.PlayerNamePositionalArgument;

import name.richardson.james.bukkit.banhammer.record.*;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerMessages.*;

public class CheckCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.check";
	private final EbeanServer database;
	private final Argument player;

	public CheckCommand(EbeanServer database) {
		super(CHECK_COMMAND_NAME, CHECK_COMMAND_DESC);
		this.database = database;
		this.player = PlayerNamePositionalArgument.getInstance(database, 0, true, PlayerRecord.PlayerStatus.BANNED);
		addArgument(player);
	}

	@Override
	public boolean isAsynchronousCommand() {
		return true;
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL);
	}

	@Override
	protected void execute() {
		final CommandSender commandSender = getContext().getCommandSender();
		final Collection<String> playerNames = player.getStrings();
		final Collection<String> messages = new ArrayList<String>();
		for (String playerName : playerNames) {
			PlayerRecord playerRecord = CurrentPlayerRecord.find(database, playerName);
			if (playerRecord != null && playerRecord.isBanned()) {
				BanRecord ban = playerRecord.getActiveBan();
				BanRecordFormatter formatter = new BanRecordFormatter(ban);
				messages.addAll(formatter.getMessages());
			} else {
				String message = PLAYER_NOT_BANNED.asInfoMessage(playerName);
				messages.add(message);
			}
		}
		commandSender.sendMessage(messages.toArray(new String[messages.size()]));
	}

}
