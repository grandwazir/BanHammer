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
package name.richardson.james.bukkit.banhammer.management;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;

@CommandPermissions(permissions = {"banhammer.export"})
public class ExportCommand extends AbstractCommand {

	private final ChoiceFormatter formatter;
	private final PlayerRecordManager playerRecordManager;
	private final Server server;

	public ExportCommand(final PlayerRecordManager playerRecordManager, final Server server) {
		this.server = server;
		this.playerRecordManager = playerRecordManager;
		this.formatter = new ChoiceFormatter(this.getClass());
		this.formatter.setLimits(0, 1, 2);
		this.formatter.setMessage("bans-exported");
		this.formatter.setFormats("no-bans", "one-ban", "many-bans");
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		int exported = 0;
		for (final Object record : playerRecordManager.list()) {
			final PlayerRecord playerRecord = (PlayerRecord) record;
			if (playerRecord.isBanned()) {
				final OfflinePlayer player = this.server.getOfflinePlayer(playerRecord.getName());
				player.setBanned(true);
				exported++;
			}
		}
		this.formatter.setArguments(exported);
		sender.sendMessage(this.formatter.getMessage());
	}

}
