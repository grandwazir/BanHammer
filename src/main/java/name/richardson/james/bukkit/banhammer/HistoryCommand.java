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
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;
import name.richardson.james.bukkit.utilities.permissions.Permissions;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordFormatter;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

@Permissions(permissions = {"banhammer.history", "banhammer.history.own", "banhammer.history.others"})
public class HistoryCommand extends AbstractCommand {

	private final PlayerRecordManager playerRecordManager;
	private CommandSender player;
	private PlayerRecord playerRecord;

	public HistoryCommand(PermissionManager permissionManager, PlayerRecordManager playerRecordManager) {
		super(permissionManager);
		this.playerRecordManager = playerRecordManager;
		permissionManager.listPermissions().get(1).setDefault(PermissionDefault.TRUE);
	}

	@Override
	public void execute(CommandContext context) {
		if (!setPlayer(context)) return;
		if (!setPlayerRecord(context)) return;
		List<BanRecord> bans = playerRecord.getBans();
		for (BanRecord ban : bans) {
			BanRecordFormatter formatter = new BanRecordFormatter(ban);
			context.getCommandSender().sendMessage(formatter.getMessages());
		}
	}

	private boolean hasPermission(CommandSender sender) {
		final boolean isSenderTargetingSelf = this.player.getName().equalsIgnoreCase(sender.getName());
		if (sender.hasPermission("banhammer.history.own") && isSenderTargetingSelf) return true;
		if (sender.hasPermission("banhammer.history.others") && !isSenderTargetingSelf) return true;
		return false;
	}

	private boolean setPlayer(CommandContext context) {
		player = null;
		if (context.has(0)) context.getOfflinePlayer(0);
		if (player == null && context.isConsoleCommandSender()) {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "must-specify-player"));
		} else {
			player = context.getCommandSender();
		}
		return (player != null);
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(player.getName());
		if (playerRecord == null || playerRecord.getBans().size() == 0) {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.WARNING, "player-has-never-been-banned", player.getName()));
		}
		return (playerRecord != null);
	}

}
