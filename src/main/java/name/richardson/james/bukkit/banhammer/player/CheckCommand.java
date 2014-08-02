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
package name.richardson.james.bukkit.banhammer.player;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractAsynchronousCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.PlayerRecord;
import name.richardson.james.bukkit.banhammer.argument.PlayerNamePositionalArgument;
import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordFormatter;

public class CheckCommand extends AbstractAsynchronousCommand {

	public static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	private static final String PERMISSION_ALL = "banhammer.check";
	private final Argument player;

	public CheckCommand(final Plugin plugin, final BukkitScheduler scheduler) {
		super(plugin, scheduler);
		this.player = PlayerNamePositionalArgument.getInstance(0, true, PlayerRecord.Status.BANNED);
		addArgument(player);
	}

	@Override
	public String getName() {
		return MESSAGES.checkCommandName();
	}

	@Override public Set<String> getPermissions() {
		return new HashSet<>(Arrays.asList(PERMISSION_ALL));
	}

	@Override
	public String getDescription() {
		return MESSAGES.checkCommandDescription();
	}

	@Override
	protected void execute() {
		final CommandSender commandSender = getContext().getCommandSender();
		final Collection<String> playerNames = player.getStrings();
		for (String playerName : playerNames) {
			PlayerRecord playerRecord = PlayerRecord.find(playerName);
			if (playerRecord != null && playerRecord.isBanned()) {
				BanRecord ban = playerRecord.getActiveBan();
				BanRecordFormatter formatter = ban.getFormatter();
				addMessages(formatter.getMessages());
			} else {
				String message = MESSAGES.playerNotBanned(playerName);
				commandSender.sendMessage(message);
			}
		}
	}

}
