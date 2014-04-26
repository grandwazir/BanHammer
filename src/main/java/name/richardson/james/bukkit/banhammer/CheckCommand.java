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
package name.richardson.james.bukkit.banhammer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.PlayerNamePositionalArgument;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.CHECK_COMMAND_DESC;
import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.CHECK_COMMAND_NAME;
import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.PLAYER_NOT_BANNED;

public class CheckCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.check";

	private final Argument player;
	private final PlayerRecordManager playerRecordManager;

	public CheckCommand(PlayerRecordManager playerRecordManager) {
		super(CHECK_COMMAND_NAME, CHECK_COMMAND_DESC);
		this.playerRecordManager = playerRecordManager;
		this.player = PlayerNamePositionalArgument.getInstance(playerRecordManager, 0, true, PlayerRecordManager.PlayerStatus.BANNED);
		addArgument(player);
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL);
	}

	@Override
	public boolean isAsynchronousCommand() {
		return true;
	}

	@Override
	protected void execute() {
		Collection<String> players = player.getStrings();
		Collection<String> messages = new ArrayList<String>();
		for (String player : players) {
			final PlayerRecord playerRecord = playerRecordManager.find(player);
			if (playerRecord != null && playerRecord.isBanned()) {
				BanRecord.BanRecordFormatter formatter = playerRecord.getActiveBan().getFormatter();
				messages.addAll(formatter.getMessages());
			} else {
				String message = PLAYER_NOT_BANNED.asInfoMessage(player);
				messages.add(message);
			}
		}
		getContext().getCommandSender().sendMessage(messages.toArray(new String[messages.size()]));
	}

}
