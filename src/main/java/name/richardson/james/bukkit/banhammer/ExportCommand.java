/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * ExportCommand.java is part of BanHammer.
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

import org.bukkit.Server;
import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.DefaultColourFormatter;
import name.richardson.james.bukkit.utilities.localisation.Localisation;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundleByClassLocalisation;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.formatters.BanCountChoiceFormatter;

public class ExportCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.export";

	private static final String BANS_EXPORTED_KEY = "bans-exported";
	private static final String NO_PERMISSION_KEY = "no-permission";

	private final PlayerRecordManager playerRecordManager;
	private final Server server;
	private final ChoiceFormatter choiceFormatter;
	private final Localisation localisation = new ResourceBundleByClassLocalisation(ExportCommand.class);
	private final ColourFormatter colourFormatter = new DefaultColourFormatter();

	public ExportCommand(PlayerRecordManager playerRecordManager, Server server) {
		this.playerRecordManager = playerRecordManager;
		this.server = server;
		this.choiceFormatter = new BanCountChoiceFormatter();
		this.choiceFormatter.setMessage(colourFormatter.format(localisation.getMessage(BANS_EXPORTED_KEY), ColourFormatter.FormatStyle.INFO));
	}

	@Override
	public void execute(CommandContext context) {
		if (isAuthorised(context.getCommandSender())) {
			for (PlayerRecord playerRecord : playerRecordManager.list("", PlayerRecordManager.PlayerStatus.BANNED)) {
				this.server.getOfflinePlayer(playerRecord.getName()).setBanned(true);
			}
			this.choiceFormatter.setArguments(playerRecordManager.count());
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

}
