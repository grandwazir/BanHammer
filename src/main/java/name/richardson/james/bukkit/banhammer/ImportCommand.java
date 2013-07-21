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

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;
import name.richardson.james.bukkit.utilities.permissions.Permissions;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

@Permissions(permissions = {"banhammer.import"})
public class ImportCommand extends AbstractCommand {

	private final PlayerRecordManager playerRecordManager;
	private final Server server;

	private String reason;

	public ImportCommand(PermissionManager permissionManager, PlayerRecordManager playerRecordManager, Server server) {
		super(permissionManager);
		this.playerRecordManager = playerRecordManager;
		this.server = server;
	}

	@Override
	public void execute(CommandContext context) {
		setReason(context);
		for (OfflinePlayer player : this.server.getBannedPlayers()) {
			playerRecordManager.new BannedPlayerBuilder().setPlayer(player.getName()).setCreator(context.getCommandSender().getName()).setReason(this.reason).save();
			player.setBanned(false);
		}
	}

	private void setReason(CommandContext context) {
		if (context.has(0)) {
			reason = context.getJoinedArguments(0);
		} else {
			reason = getMessage("default-ban-message");
		}
	}

}
