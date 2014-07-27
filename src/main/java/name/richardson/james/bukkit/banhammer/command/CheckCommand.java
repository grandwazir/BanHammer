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
package name.richardson.james.bukkit.banhammer.command;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractAsyncronousCommand;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.command.argument.Argument;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.command.argument.PlayerNamePositionalArgument;

import name.richardson.james.bukkit.banhammer.model.BanRecord;
import name.richardson.james.bukkit.banhammer.model.PlayerRecord;

@CommandPermissions(permissions = {"banhammer.check"})
public class CheckCommand extends AbstractAsyncronousCommand {

	public static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	private final Argument player;

	public CheckCommand(final Plugin plugin, final BukkitScheduler scheduler) {
		super(plugin, scheduler);
		this.player = PlayerNamePositionalArgument.getInstance(0, true, PlayerRecord.Status.BANNED);
		addArgument(player);
	}

	@Override public String getName() {
		return MESSAGES.checkCommandName();
	}

	@Override public String getDescription() {
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
				//BanRecordFormatter formatter = new SimpleBanRecordFormatter(ban);
				//messages.addAll(formatter.getMessages());
			} else {
				String message = MESSAGES.playerNotBanned(playerName);
				commandSender.sendMessage(message);
			}
		}
	}

}
