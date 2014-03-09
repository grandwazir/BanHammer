/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * ImportCommand.java is part of BanHammer.
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

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.localisation.PluginLocalisation;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.formatters.BanCountChoiceFormatter;
import name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation;

public class ImportCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.import";

	private final ChoiceFormatter choiceFormatter;
	private final PlayerRecordManager playerRecordManager;
	private final Server server;
	private String reason;

	public ImportCommand(PlayerRecordManager playerRecordManager, Server server) {
		this.playerRecordManager = playerRecordManager;
		this.server = server;
		this.choiceFormatter = new BanCountChoiceFormatter();
		this.choiceFormatter.setMessage(getLocalisation().formatAsInfoMessage(BanHammerLocalisation.IMPORT_SUMMARY));
	}

	@Override
	public void execute(CommandContext context) {
		if (isAuthorised(context.getCommandSender())) {
			setReason(context);
			for (OfflinePlayer player : this.server.getBannedPlayers()) {
				PlayerRecordManager.BannedPlayerBuilder builder = playerRecordManager.getBannedPlayerBuilder();
				builder.setPlayer(player.getName());
				builder.setCreator(context.getCommandSender().getName());
				builder.setReason(this.reason);
				builder.save();
				player.setBanned(false);
			}
			choiceFormatter.setArguments(this.server.getBannedPlayers().size());
			context.getCommandSender().sendMessage(choiceFormatter.getMessage());
		} else {
			String message = getLocalisation().formatAsErrorMessage(PluginLocalisation.COMMAND_NO_PERMISSION);
			context.getCommandSender().sendMessage(message);
		}
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL);
	}

	private void setReason(CommandContext context) {
		if (context.hasArgument(0)) {
			reason = context.getJoinedArguments(0);
		} else {
			reason = getLocalisation().getMessage(BanHammerLocalisation.IMPORT_DEFAULT_REASON);
		}
	}

}
