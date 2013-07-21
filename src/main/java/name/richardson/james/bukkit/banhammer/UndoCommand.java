/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * UndoCommand.java is part of BanHammer.
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
import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;
import name.richardson.james.bukkit.utilities.permissions.Permissions;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

@Permissions(permissions = {"banhammer.undo", "banhammer.undo.own", "banhammer.undo.others", "banhammer.undo.unrestricted"})
public class UndoCommand extends AbstractCommand {

	private final BanRecordManager banRecordManager;
	private final PlayerRecordManager playerRecordManager;
	private final long undoTime;

	private BanRecord ban;
	private OfflinePlayer player;
	private PlayerRecord playerRecord;

	public UndoCommand(PermissionManager permissionManager, PlayerRecordManager playerRecordManager, BanRecordManager banRecordManager, final long undoTime) {
		super(permissionManager);
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
		this.undoTime = undoTime;
	}

	@Override
	public void execute(CommandContext context) {
		if (!setPlayer(context)) return;
		if (!setPlayerRecord(context)) return;
		if (!setBan(context)) return;
		if (!hasPermission(context.getCommandSender())) return;
		banRecordManager.delete(ban);
		context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.INFO, "ban-undone", player.getName()));
	}

	private boolean hasPermission(CommandSender sender) {
		final boolean isSenderTargetingSelf = (this.ban.getCreator().getName().equalsIgnoreCase(sender.getName())) ? true : false;
		final boolean withinTimeLimit = this.withinTimeLimit(sender);
		if (sender.hasPermission("banhammer.undo.own") && withinTimeLimit && isSenderTargetingSelf) return true;
		if (sender.hasPermission("banhammer.undo.others") && withinTimeLimit && !isSenderTargetingSelf) return true;
		sender.sendMessage(getColourScheme().format(ColourScheme.Style.ERROR, "may-not-undo-that-players-ban"));
		return false;
	}

	private boolean setBan(CommandContext context) {
		ban = playerRecord.getActiveBan();
		if (ban == null) context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.WARNING, "player-is-not-banned", player.getName()));
		return (ban != null);
	}

	private boolean setPlayer(CommandContext context) {
		player = null;
		if (context.has(0)) context.getOfflinePlayer(0);
		if (player == null) context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "must-specify-player"));
		return (player != null);
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(player.getName());
		if (playerRecord == null || playerRecord.getBans().size() == 0) {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.WARNING, "player-has-never-been-banned", player.getName()));
		}
		return (playerRecord != null);
	}

	private boolean withinTimeLimit(CommandSender sender) {
		if (sender.hasPermission("banhammer.undo.unrestricted")) return true;
		if ((System.currentTimeMillis() - ban.getExpiresAt().getTime()) <= this.undoTime) return true;
		sender.sendMessage(getColourScheme().format(ColourScheme.Style.ERROR, "undo-time-expired"));
		return false;
	}

}
