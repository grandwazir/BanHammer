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

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;

@CommandPermissions(permissions = { "banhammer.export" })
public class ExportCommand extends AbstractCommand {

	/** The database handler for this plugin. */
	private final EbeanServer database;

	private final ChoiceFormatter formatter;

	/** A instance of the Bukkit server. */
	private final Server server;

	public ExportCommand(final BanHammer plugin) {
		super();
		this.server = plugin.getServer();
		this.database = plugin.getDatabase();
		this.formatter = new ChoiceFormatter();
		this.formatter.setLimits(0, 1, 2);
		this.formatter.setMessage("notice.bans-exported");
		this.formatter.setFormats(this.getMessage("shared.choice.no-bans"), this.getMessage("shared.choice.one-ban"), this.getMessage("shared.choice.many-bans"));
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		int exported = 0;
		for (final Object record : PlayerRecord.list(this.database)) {
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
