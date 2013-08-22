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
import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.DefaultColourFormatter;
import name.richardson.james.bukkit.utilities.localisation.Localisation;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundleByClassLocalisation;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

public class UndoCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.undo";
	public static final String PERMISSION_OWN = "banhammer.undo.own";
	public static final String PERMISSION_OTHERS = "banhammer.undo.others";
	public static final String PERMISSION_UNRESTRICTED = "banhammer.undo.unrestricted";

	private static final String BAN_UNDONE_KEY = "ban-undone";
	private static final String MAY_NOT_UNDO_THAT_PLAYERS_BAN_KEY = "may-not-undo-that-players-ban";
	private static final String PLAYER_IS_NOT_BANNED_KEY = "player-is-not-banned";
	private static final String MUST_SPECIFY_PLAYER_KEY = "must-specify-player";
	private static final String PLAYER_HAS_NEVER_BEEN_BANNED_KEY = "player-has-never-been-banned";
	private static final String UNDO_TIME_EXPIRED = "undo-time-expired";

	private final BanRecordManager banRecordManager;
	private final PlayerRecordManager playerRecordManager;
	private final Localisation localisation = new ResourceBundleByClassLocalisation(UndoCommand.class);
	private final ColourFormatter colourFormatter = new DefaultColourFormatter();
	private final long undoTime;

	private BanRecord ban;
	private String playerName;
	private PlayerRecord playerRecord;

	public UndoCommand(PlayerRecordManager playerRecordManager, BanRecordManager banRecordManager, final long undoTime) {
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
		context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(BAN_UNDONE_KEY), ColourFormatter.FormatStyle.INFO, playerName));
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		if (permissible.hasPermission(PERMISSION_ALL)) return true;
		if (permissible.hasPermission(PERMISSION_OWN)) return true;
		if (permissible.hasPermission(PERMISSION_OTHERS)) return true;
		return false;
	}

	private boolean hasPermission(CommandSender sender) {
		final boolean isSenderTargetingSelf = (this.ban.getCreator().getName().equalsIgnoreCase(sender.getName()));
		final boolean withinTimeLimit = this.withinTimeLimit(sender);
		if (sender.hasPermission(PERMISSION_OWN) && withinTimeLimit && isSenderTargetingSelf) return true;
		if (sender.hasPermission(PERMISSION_OTHERS) && withinTimeLimit && !isSenderTargetingSelf) return true;
		sender.sendMessage(colourFormatter.format(localisation.getMessage(MAY_NOT_UNDO_THAT_PLAYERS_BAN_KEY), ColourFormatter.FormatStyle.ERROR, ban.getCreator().getName()));
		return false;
	}

	private boolean setBan(CommandContext context) {
		ban = playerRecord.getActiveBan();
		if (ban == null) {
			context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(PLAYER_IS_NOT_BANNED_KEY), ColourFormatter.FormatStyle.INFO, playerName));
			return false;
		} else {
			return true;
		}
	}

	private boolean setPlayer(CommandContext context) {
		playerName = null;
		if (context.has(0)) playerName = context.getString(0);
		if (playerName == null) {
			context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(MUST_SPECIFY_PLAYER_KEY), ColourFormatter.FormatStyle.ERROR));
			return false;
		} else {
			return true;
		}
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(playerName);
		if (playerRecord == null || playerRecord.getBans().size() == 0) {
			context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(PLAYER_HAS_NEVER_BEEN_BANNED_KEY), ColourFormatter.FormatStyle.WARNING, playerName));
			return false;
		} else {
			return true;
		}
	}

	private boolean withinTimeLimit(CommandSender sender) {
		if (sender.hasPermission(PERMISSION_UNRESTRICTED)) return true;
		if ((System.currentTimeMillis() - ban.getCreatedAt().getTime()) <= this.undoTime) return true;
		sender.sendMessage(colourFormatter.format(localisation.getMessage(UNDO_TIME_EXPIRED), ColourFormatter.FormatStyle.ERROR));
		return false;
	}

}
