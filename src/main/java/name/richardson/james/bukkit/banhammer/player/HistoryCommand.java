/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 HistoryCommand.java is part of BanHammer.

 BanHammer is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option) any
 later version.

 BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractAsynchronousCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;

import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.PlayerRecord;
import name.richardson.james.bukkit.banhammer.argument.PlayerNamePositionalArgument;
import name.richardson.james.bukkit.banhammer.ban.BanRecordFormatter;

public class HistoryCommand extends AbstractAsynchronousCommand {

	private static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	public static final String PERMISSION_ALL = "banhammer.history";
	public static final String PERMISSION_OWN = "banhammer.history.own";
	public static final String PERMISSION_OTHERS = "banhammer.history.others";
	private final Argument playerName;

	public HistoryCommand(final Plugin plugin, final BukkitScheduler scheduler) {
		super(plugin, scheduler);
		playerName = PlayerNamePositionalArgument.getInstance(0, false, PlayerRecord.Status.ANY);
		addArgument(playerName);
	}

	@Override public String getDescription() {
		return MESSAGES.historyCommandDescription();
	}

	@Override public String getName() {
		return MESSAGES.historyCommandName();
	}

	@Override public Set<String> getPermissions() {
		return new HashSet<>(Arrays.asList(PERMISSION_ALL, PERMISSION_OTHERS, PERMISSION_OWN));
	}

	@Override
	protected void execute() {
		if (playerName.getStrings().isEmpty()) {
			String playerName = getContext().getCommandSender().getName();
			createPlayerHistory(playerName);
		} else {
			for (String playerName : this.playerName.getStrings()) {
				createPlayerHistory(playerName);
			}
		}
	}

	private void createPlayerHistory(final String playerName) {
		if (hasPermission(playerName)) {
			PlayerRecord record = PlayerRecord.find(playerName);
			if (record == null || record.getBans().isEmpty()) {
				addMessage(MESSAGES.playerNeverBanned(playerName));
			} else {
				Set<BanRecord> bans = record.getBans();
				addMessage(MESSAGES.playerHasBansOnRecord(playerName, bans.size()));
				for (BanRecord ban : bans) {
					BanRecordFormatter formatter = ban.getFormatter();
					addMessages(formatter.getMessages());
				}
			}
		} else {
			addMessage(MESSAGES.notAllowedToAuditThatPlayer(playerName));
		}
	}

	private boolean hasPermission(String playerName) {
		boolean isSenderTargetingSelf = playerName.equalsIgnoreCase(getContext().getCommandSender().getName());
		return isAuthorised(PERMISSION_OWN) && isSenderTargetingSelf || isAuthorised(PERMISSION_OTHERS) && !isSenderTargetingSelf;
	}

}
