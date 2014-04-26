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
package name.richardson.james.bukkit.banhammer;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.PlayerNamePositionalArgument;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.formatters.BanCountChoiceFormatter;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.*;

public class PurgeCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.purge";
	public static final String PERMISSION_OWN = "banhammer.purge.own";
	public static final String PERMISSION_OTHERS = "banhammer.purge.others";
	private final BanRecordManager banRecordManager;
	private final ChoiceFormatter choiceFormatter;
	private final PlayerRecordManager playerRecordManager;
	private final Argument players;

	public PurgeCommand(PlayerRecordManager playerRecordManager, BanRecordManager banRecordManager) {
		super(PURGE_COMMAND_NAME, PURGE_COMMAND_DESC);
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
		this.choiceFormatter = new BanCountChoiceFormatter();
		this.choiceFormatter.setMessage(PURGE_SUMMARY.asInfoMessage());
		this.players = PlayerNamePositionalArgument.getInstance(playerRecordManager, 0, true, PlayerRecordManager.PlayerStatus.ANY);
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
		Collection<String> players = this.players.getStrings();
		Collection<BanRecord> records = new ArrayList<BanRecord>();
		final CommandSender sender = getContext().getCommandSender();
		boolean own = sender.hasPermission(PERMISSION_OWN);
		boolean others = sender.hasPermission(PERMISSION_OTHERS);
		for (String playerName : players) {
			PlayerRecord record = playerRecordManager.find(playerName);
			if (record != null) {
				for (BanRecord ban : record.getBans()) {
					boolean banCreatedBySender = ban.getCreator().getName().equalsIgnoreCase(sender.getName());
					if (banCreatedBySender && !own) continue;
					if (!banCreatedBySender && !others) continue;
					records.add(ban);
				}
			} else {
				sender.sendMessage(PLAYER_NEVER_BEEN_BANNED.asInfoMessage(playerName));
			}
			this.choiceFormatter.setArguments(records.size(), playerName);
			banRecordManager.delete(records);
			sender.sendMessage(choiceFormatter.getMessage());
		}
	}

}

