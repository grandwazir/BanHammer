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
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.DefaultColourFormatter;
import name.richardson.james.bukkit.utilities.localisation.Localisation;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundleByClassLocalisation;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.formatters.BanCountChoiceFormatter;

public class ImportCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.import";

	private static final String BANS_IMPORTED_KEY = "bans-imported";
	private static final String NO_PERMISSION_KEY = "no-permission";
	private static final String DEFAULT_BAN_IMPORT_REASON_KEY = "default-ban-import-reason";

	private final ChoiceFormatter choiceFormatter;
	private final PlayerRecordManager playerRecordManager;
	private final Server server;
	private final ColourFormatter colourFormatter = new DefaultColourFormatter();
	private final Localisation localisation = new ResourceBundleByClassLocalisation(ImportCommand.class);

	private String reason;

	public ImportCommand(PlayerRecordManager playerRecordManager, Server server) {
		this.playerRecordManager = playerRecordManager;
		this.server = server;
		this.choiceFormatter = new BanCountChoiceFormatter();
		this.choiceFormatter.setMessage(colourFormatter.format(localisation.getMessage(BANS_IMPORTED_KEY), ColourFormatter.FormatStyle.INFO));
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
			context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(NO_PERMISSION_KEY), ColourFormatter.FormatStyle.ERROR));
		}
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		if (permissible.hasPermission(PERMISSION_ALL)) return true;
		return false;
	}

	private void setReason(CommandContext context) {
		if (context.has(0)) {
			reason = context.getJoinedArguments(0);
		} else {
			reason = localisation.getMessage(DEFAULT_BAN_IMPORT_REASON_KEY);
		}
	}

}
