/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * HistoryCommand.java is part of BanHammer.
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

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.localisation.PluginLocalisation;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation;

public class HistoryCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.history";
	public static final String PERMISSION_OWN = "banhammer.history.own";
	public static final String PERMISSION_OTHERS = "banhammer.history.others";

	private final PlayerRecordManager playerRecordManager;
	private String playerName;
	private PlayerRecord playerRecord;

	public HistoryCommand(PlayerRecordManager playerRecordManager) {
		this.playerRecordManager = playerRecordManager;
	}

	@Override
	public void execute(CommandContext context) {
		if (!setPlayerName(context)) return;
		if (!setPlayerRecord(context)) return;
		if (!hasPermission(context.getCommandSender())) return;
		List<BanRecord> bans = playerRecord.getBans();
		for (BanRecord ban : bans) {
			BanRecord.BanRecordFormatter formatter = ban.getFormatter();
			context.getCommandSender().sendMessage(formatter.getMessages());
		}
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(playerName);
		if (playerRecord == null || playerRecord.getBans().size() == 0) {
			String message = getLocalisation().formatAsInfoMessage(BanHammerLocalisation.PLAYER_NEVER_BEEN_BANNED, playerName);
			context.getCommandSender().sendMessage(message);
		}
		return (playerRecord != null);
	}

	private boolean setPlayerName(CommandContext context) {
		playerName = null;
		if (context.hasArgument(0)) {
			playerName = context.getString(0);
		} else {
			playerName = context.getCommandSender().getName();
		}
		return true;
	}

	private boolean hasPermission(CommandSender sender) {
		final boolean isSenderTargetingSelf = playerName.equalsIgnoreCase(sender.getName());
		if (sender.hasPermission(PERMISSION_OWN) && isSenderTargetingSelf) return true;
		if (sender.hasPermission(PERMISSION_OTHERS) && !isSenderTargetingSelf) return true;
		String message = getLocalisation().formatAsErrorMessage(PluginLocalisation.COMMAND_NO_PERMISSION);
		sender.sendMessage(message);
		return false;
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		if (permissible.hasPermission(PERMISSION_ALL)) return true;
		if (permissible.hasPermission(PERMISSION_OWN)) return true;
		if (permissible.hasPermission(PERMISSION_OTHERS)) return true;
		return false;
	}

}
