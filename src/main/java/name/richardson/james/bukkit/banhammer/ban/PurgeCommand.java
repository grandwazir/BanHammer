/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * PurgeCommand.java is part of BanHammer.
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
package name.richardson.james.bukkit.banhammer.ban;

import java.util.*;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractAsynchronousCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.argument.PlayerNamePositionalArgument;
import name.richardson.james.bukkit.banhammer.player.PlayerRecord;

public class PurgeCommand extends AbstractAsynchronousCommand {

	private static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	private static final String PERMISSION_ALL = "banhammer.purge";
	private static final String PERMISSION_OWN = "banhammer.purge.own";
	private static final String PERMISSION_OTHERS = "banhammer.purge.others";
	private final Argument players;

	public PurgeCommand(final Plugin plugin, final BukkitScheduler scheduler) {
		super(plugin, scheduler);
		this.players = PlayerNamePositionalArgument.getInstance(0, true, PlayerRecord.Status.ANY);
		addArgument(players);
	}

	@Override public String getDescription() {
		return MESSAGES.purgeCommandDescription();
	}

	@Override public String getName() {
		return MESSAGES.purgeCommandName();
	}

	@Override public Set<String> getPermissions() {
		return new HashSet<>(Arrays.asList(PERMISSION_ALL, PERMISSION_OTHERS, PERMISSION_OWN));
	}

	@Override
	protected void execute() {
		Collection<String> playerNames = this.players.getStrings();
		Collection<BanRecord> records = new ArrayList<>();
		boolean own = getContext().isAuthorised(PERMISSION_OWN);
		boolean others = getContext().isAuthorised(PERMISSION_OTHERS);
		for (String playerName : playerNames) {
			PlayerRecord record = PlayerRecord.find(playerName);
			if (record != null) {
				for (BanRecord ban : record.getBans()) {
					final boolean banCreatedBySender = (ban.getCreator().getId().compareTo(getContext().getCommandSenderUUID()) == 0);
					if (banCreatedBySender && !own) continue;
					if (!banCreatedBySender && !others) continue;
					records.add(ban);
				}
			} else {
				String message = MESSAGES.playerNotBanned(playerName);
				addMessage(message);
			}
			String message = MESSAGES.bansPurged(records.size());
			addMessage(message);
		}
	}

}

