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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.PlayerNamePositionalArgument;
import name.richardson.james.bukkit.utilities.localisation.BukkitUtilities;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.HISTORY_COMMAND_NAME;
import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.HISTORY_COMMAND_DESC;
import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.PLAYER_NEVER_BEEN_BANNED;

public class HistoryCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.history";
	public static final String PERMISSION_OWN = "banhammer.history.own";
	public static final String PERMISSION_OTHERS = "banhammer.history.others";
	private final Argument playerName;
	private final PlayerRecordManager playerRecordManager;

	public HistoryCommand(PlayerRecordManager playerRecordManager) {
		super(HISTORY_COMMAND_NAME, HISTORY_COMMAND_DESC);
		this.playerRecordManager = playerRecordManager;
		this.playerName = PlayerNamePositionalArgument.getInstance(playerRecordManager, 0, false, PlayerRecordManager.PlayerStatus.ANY);
		addArgument(playerName);
	}

	@Override
	public boolean isAsynchronousCommand() {
		return false;
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL) || permissible.hasPermission(PERMISSION_OWN) || permissible.hasPermission(PERMISSION_OTHERS);
	}

	@Override
	protected void execute() {
		final CommandSender sender = getContext().getCommandSender();
		final String playerName = (this.playerName.getString() == null) ? sender.getName() : this.playerName.getString();
		final List<String> messages = new ArrayList<String>();
		if (!hasPermission(sender, playerName)) {
			messages.add(BukkitUtilities.INVOKER_NO_PERMISSION.asErrorMessage());
		} else {
			PlayerRecord record = playerRecordManager.find(playerName);
			if (record != null && !record.getBans().isEmpty()) {
				for (BanRecord ban : record.getBans()) {
					BanRecord.BanRecordFormatter formatter = ban.getFormatter();
					messages.addAll(formatter.getMessages());
				}
			} else {
				messages.add(PLAYER_NEVER_BEEN_BANNED.asInfoMessage(playerName));
			}
		}
		sender.sendMessage(messages.toArray(new String[messages.size()]));
	}

	private boolean hasPermission(CommandSender sender, String playerName) {
		final boolean isSenderTargetingSelf = playerName.equalsIgnoreCase(sender.getName());
		return sender.hasPermission(PERMISSION_OWN) && isSenderTargetingSelf || sender.hasPermission(PERMISSION_OTHERS) && !isSenderTargetingSelf;
	}

}
