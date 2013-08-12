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

@Permissions(permissions = {UndoCommand.PERMISSION_ALL, UndoCommand.PERMISSION_OWN, UndoCommand.PERMISSION_OTHERS, UndoCommand.PERMISSION_UNRESTRICTED})
public class UndoCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.undo";
	public static final String PERMISSION_OWN = "banhammer.undo.own";
	public static final String PERMISSION_OTHERS = "banhammer.undo.others";
	public static final String PERMISSION_UNRESTRICTED = "banhammer.undo.unrestricted";

	private final BanRecordManager banRecordManager;
	private final PlayerRecordManager playerRecordManager;
	private final long undoTime;

	private BanRecord ban;
	private String playerName;
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
		context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.INFO, "ban-undone", playerName));
	}

	private boolean hasPermission(CommandSender sender) {
		final boolean isSenderTargetingSelf = (this.ban.getCreator().getName().equalsIgnoreCase(sender.getName()));
		final boolean withinTimeLimit = this.withinTimeLimit(sender);
		if (sender.hasPermission(PERMISSION_OWN) && withinTimeLimit && isSenderTargetingSelf) return true;
		if (sender.hasPermission(PERMISSION_OTHERS) && withinTimeLimit && !isSenderTargetingSelf) return true;
		sender.sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "may-not-undo-that-players-ban", ban.getCreator().getName()));
		return false;
	}

	private boolean setBan(CommandContext context) {
		ban = playerRecord.getActiveBan();
		if (ban == null) {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.WARNING, "player-is-not-banned", playerName));
			return false;
		} else {
			return true;
		}
	}

	private boolean setPlayer(CommandContext context) {
		playerName = null;
		if (context.has(0)) playerName = context.getString(0);
		if (playerName == null) {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "must-specify-player"));
			return false;
		} else {
			return true;
		}
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(playerName);
		if (playerRecord == null || playerRecord.getBans().size() == 0) {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.WARNING, "player-has-never-been-banned", playerName));
			return false;
		} else {
			return true;
		}
	}

	private boolean withinTimeLimit(CommandSender sender) {
		if (sender.hasPermission(PERMISSION_UNRESTRICTED)) return true;
		if ((System.currentTimeMillis() - ban.getCreatedAt().getTime()) <= this.undoTime) return true;
		sender.sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "undo-time-expired"));
		return false;
	}

}
