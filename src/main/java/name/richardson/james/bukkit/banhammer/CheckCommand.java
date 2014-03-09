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

import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.localisation.PluginLocalisation;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation;

public class CheckCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.check";

	private final PlayerRecordManager playerRecordManager;
	private String playerName;
	private PlayerRecord playerRecord;

	public CheckCommand(PlayerRecordManager playerRecordManager) {
		this.playerRecordManager = playerRecordManager;
	}

	@Override
	public void execute(CommandContext context) {
		if (isAuthorised(context.getCommandSender())) {
			if (!setPlayer(context)) return;
			if (!setPlayerRecord(context)) return;
			BanRecord.BanRecordFormatter banRecordFormatter = playerRecord.getActiveBan().getFormatter();
			context.getCommandSender().sendMessage(banRecordFormatter.getMessages());
		} else {
			String message = getLocalisation().formatAsErrorMessage(PluginLocalisation.COMMAND_NO_PERMISSION);
			context.getCommandSender().sendMessage(message);
		}
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		if (permissible.hasPermission(PERMISSION_ALL)) return true;
		return false;
	}

	private boolean setPlayer(CommandContext context) {
		playerName = context.getString(0);
		if (playerName == null) {
			String message = getLocalisation().formatAsErrorMessage(PluginLocalisation.COMMAND_MUST_SPECIFY_PLAYER);
			context.getCommandSender().sendMessage(message);
			return false;
		} else {
			return true;
		}
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(playerName);
		if (playerRecord == null || !playerRecord.isBanned()) {
			String message = getLocalisation().formatAsInfoMessage(BanHammerLocalisation.PLAYER_NOT_BANNED, playerName);
			context.getCommandSender().sendMessage(message);
			return false;
		} else {
			return true;
		}
	}

}
