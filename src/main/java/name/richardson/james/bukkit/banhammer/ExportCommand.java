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

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.formatters.localisation.LocalisedChoiceFormatter;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;
import name.richardson.james.bukkit.utilities.permissions.Permissions;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

@Permissions(permissions = {ExportCommand.PERMISSION_ALL})
public class ExportCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.export";

	private final PlayerRecordManager playerRecordManager;
	private final Server server;
	private final LocalisedChoiceFormatter choiceFormatter;

	public ExportCommand(PermissionManager permissionManager, PlayerRecordManager playerRecordManager, Server server) {
		super(permissionManager);
		this.playerRecordManager = playerRecordManager;
		this.server = server;
		this.choiceFormatter = new BanCountChoiceFormatter();
		this.choiceFormatter.setMessage("bans-exported");
	}

	@Override
	public void execute(CommandContext context) {
		if (isAuthorised(context.getCommandSender())) {
			for (PlayerRecord playerRecord : playerRecordManager.list("", PlayerRecordManager.PlayerStatus.BANNED)) {
				this.server.getOfflinePlayer(playerRecord.getName()).setBanned(true);
			}
			this.choiceFormatter.setArguments(playerRecordManager.count());
			context.getCommandSender().sendMessage(choiceFormatter.getColouredMessage(ColourScheme.Style.INFO));
		} else {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "no-permission"));
		}
	}

}
