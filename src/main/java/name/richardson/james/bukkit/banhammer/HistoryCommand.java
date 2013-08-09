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

@Permissions(permissions = {HistoryCommand.PERMISSION_ALL, HistoryCommand.PERMISSION_OTHERS, HistoryCommand.PERMISSION_OWN})
public class HistoryCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.history";
	public static final String PERMISSION_OWN = "banhammer.history.own";
	public static final String PERMISSION_OTHERS = "banhammer.history.others";

	private final PlayerRecordManager playerRecordManager;

	private String playerName;
	private PlayerRecord playerRecord;

	public HistoryCommand(PermissionManager permissionManager, PlayerRecordManager playerRecordManager) {
		super(permissionManager);
		this.playerRecordManager = playerRecordManager;
		permissionManager.listPermissions().get(1).setDefault(PermissionDefault.TRUE);
	}

	@Override
	public void execute(CommandContext context) {
		if (!setPlayerName(context)) return;
		if (!setPlayerRecord(context)) return;
		if (!hasPermission(context.getCommandSender())) return;
		List<BanRecord> bans = playerRecord.getBans();
		for (BanRecord ban : bans) {
			BanRecordFormatter formatter = new BanRecordFormatter(ban);
			context.getCommandSender().sendMessage(formatter.getMessages());
		}
	}

	private boolean hasPermission(CommandSender sender) {
		final boolean isSenderTargetingSelf = playerName.equalsIgnoreCase(sender.getName());
		if (sender.hasPermission(PERMISSION_OWN) && isSenderTargetingSelf) return true;
		if (sender.hasPermission(PERMISSION_OTHERS) && !isSenderTargetingSelf) return true;
		sender.sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "no-permission"));
		return false;
	}

	private boolean setPlayerName(CommandContext context) {
		playerName = null;
		if (context.has(0)) {
			playerName = context.getString(0);
		} else {
			playerName = context.getCommandSender().getName();
		}
		return true;
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(playerName);
		if (playerRecord == null || playerRecord.getBans().size() == 0) {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.WARNING, "player-has-never-been-banned", playerName));
		}
		return (playerRecord != null);
	}

}
