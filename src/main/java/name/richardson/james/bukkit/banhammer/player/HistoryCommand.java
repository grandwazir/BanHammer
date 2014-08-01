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
package name.richardson.james.bukkit.banhammer.player;

import java.util.*;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractAsynchronousCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.argument.PlayerNamePositionalArgument;
import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordFormatter;

public class HistoryCommand extends AbstractAsynchronousCommand {

	private static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	public static final String PERMISSION_ALL = "banhammer.history";
	public static final String PERMISSION_OWN = "banhammer.history.own";
	public static final String PERMISSION_OTHERS = "banhammer.history.others";
	private final Argument playerName;

	public HistoryCommand(final Plugin plugin, final BukkitScheduler scheduler) {
		super(plugin, scheduler);
		this.playerName = PlayerNamePositionalArgument.getInstance(0, false, PlayerRecord.Status.ANY);
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
		final CommandSender sender = getContext().getCommandSender();
		final String playerName = (this.playerName.getString() == null) ? sender.getName() : this.playerName.getString();
		final List<String> messages = new ArrayList<String>();
		if (hasPermission(sender, playerName)) {
			PlayerRecord record = PlayerRecord.find(playerName);
			Set<BanRecord> bans = record.getBans();
			if (record != null && !bans.isEmpty()) {
				for (BanRecord ban : bans) {
					BanRecordFormatter formatter = ban.getFormatter();
					messages.addAll(formatter.getMessages());
				}
			} else {
				messages.add(MESSAGES.playerNeverBanned(playerName));
			}
		} else {
			messages.add(MESSAGES.notAllowedToAuditThatPlayer(playerName));
		}
		sender.sendMessage(messages.toArray(new String[messages.size()]));
	}

	private boolean hasPermission(CommandSender sender, String playerName) {
		final boolean isSenderTargetingSelf = playerName.equalsIgnoreCase(sender.getName());
		return sender.hasPermission(PERMISSION_OWN) && isSenderTargetingSelf || sender.hasPermission(PERMISSION_OTHERS) && !isSenderTargetingSelf;
	}

}
