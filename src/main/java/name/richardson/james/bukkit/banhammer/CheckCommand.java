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

import org.bukkit.OfflinePlayer;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;
import name.richardson.james.bukkit.utilities.permissions.Permissions;

import name.richardson.james.bukkit.banhammer.ban.BanRecordFormatter;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

@Permissions(permissions = {"banhammer.check"})
public class CheckCommand extends AbstractCommand {

	private final PlayerRecordManager playerRecordManager;

	private OfflinePlayer player;
	private PlayerRecord playerRecord;

	public CheckCommand(PermissionManager permissionManager, PlayerRecordManager playerRecordManager) {
		super(permissionManager);
		this.playerRecordManager = playerRecordManager;
	}

	@Override
	public void execute(CommandContext context) {
		if (isAuthorised(context.getCommandSender())) {
			if (!setPlayer(context)) return;
			if (!setPlayerRecord(context)) return;
			BanRecordFormatter banRecordFormatter = new BanRecordFormatter(playerRecord.getActiveBan());
			context.getCommandSender().sendMessage(banRecordFormatter.getMessages());
		} else {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "no-permission"));
		}
	}

	private boolean setPlayer(CommandContext context) {
		player = null;
		if (context.has(0) && context.getString(0).length() != 0) player = context.getOfflinePlayer(0);
		if (player == null) context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "must-specify-player"));
		return (player != null);
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(player.getName());
		if (playerRecord == null || !playerRecord.isBanned()) {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.WARNING, "player-is-not-banned", player.getName()));
			return false;
		} else {
			return true;
		}
	}

}
