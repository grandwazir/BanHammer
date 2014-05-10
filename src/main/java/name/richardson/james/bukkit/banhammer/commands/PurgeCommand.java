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
package name.richardson.james.bukkit.banhammer.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.PlayerNamePositionalArgument;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;

import name.richardson.james.bukkit.banhammer.record.BanRecord;
import name.richardson.james.bukkit.banhammer.record.CurrentBanRecord;
import name.richardson.james.bukkit.banhammer.record.CurrentPlayerRecord;
import name.richardson.james.bukkit.banhammer.record.PlayerRecord;
import name.richardson.james.bukkit.banhammer.utilities.formatters.BanCountChoiceFormatter;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerMessages.*;

public class PurgeCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.purge";
	public static final String PERMISSION_OWN = "banhammer.purge.own";
	public static final String PERMISSION_OTHERS = "banhammer.purge.others";
	private final ChoiceFormatter choiceFormatter;
	private final EbeanServer database;
	private final Argument players;

	public PurgeCommand(EbeanServer database) {
		super(PURGE_COMMAND_NAME, PURGE_COMMAND_DESC);
		this.database = database;
		this.choiceFormatter = new BanCountChoiceFormatter();
		this.choiceFormatter.setMessage(PURGE_SUMMARY.asInfoMessage());
		this.players = PlayerNamePositionalArgument.getInstance(database, 0, true, PlayerRecord.PlayerStatus.ANY);
		addArgument(players);
	}

	@Override
	public boolean isAsynchronousCommand() {
		return false;
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL) || permissible.hasPermission(PERMISSION_OTHERS) || permissible.hasPermission(PERMISSION_OWN);
	}

	@Override
	protected void execute() {
		final Collection<String> playerNames = this.players.getStrings();
		final Collection<BanRecord> records = new ArrayList<BanRecord>();
		final CommandSender sender = getContext().getCommandSender();
		final List<String> messages = new ArrayList<String>();
		boolean own = sender.hasPermission(PERMISSION_OWN);
		boolean others = sender.hasPermission(PERMISSION_OTHERS);
		for (String playerName : playerNames) {
			PlayerRecord record = CurrentPlayerRecord.find(database, playerName);
			if (record != null) {
				for (BanRecord ban : record.getBans()) {
					final boolean banCreatedBySender = (ban.getCreator().getUuid().compareTo(getCommandSenderUUID()) == 0);
					if (banCreatedBySender && !own) continue;
					if (!banCreatedBySender && !others) continue;
					records.add(ban);
				}
			} else {
				messages.add(PLAYER_NEVER_BEEN_BANNED.asInfoMessage(playerName));
			}
			this.choiceFormatter.setArguments(records.size(), playerName);
			messages.add(0, choiceFormatter.getMessage());
			database.delete(records);
			sender.sendMessage(messages.toArray(new String[messages.size()]));
		}
	}

	private UUID getCommandSenderUUID() {
		if (getContext().getCommandSender() instanceof Player) {
			return ((Player) getContext().getCommandSender()).getUniqueId();
		} else {
			return null;
		}
	}

}

