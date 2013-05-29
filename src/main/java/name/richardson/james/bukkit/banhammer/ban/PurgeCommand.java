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
package name.richardson.james.bukkit.banhammer.ban;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;

@ConsoleCommand
public class PurgeCommand extends AbstractCommand {

	private final EbeanServer database;

	private final ChoiceFormatter formatter;

	/** The player from whom we are going to purge bans */
	private OfflinePlayer player;

	/** A instance of the Bukkit server. */
	private final Server server;

	public PurgeCommand(final BanHammer plugin) {
		super(plugin);
		this.database = plugin.getDatabase();
		this.server = plugin.getServer();
		this.formatter = new ChoiceFormatter(this.getLocalisation());
		this.formatter.setLimits(0, 1, 2);
		this.formatter.setMessage(this, "purged");
		this.formatter.setFormats(this.getLocalisation().getMessage(BanHammer.class, "no-bans"), this.getLocalisation().getMessage(BanHammer.class, "one-ban"),
			this.getLocalisation().getMessage(BanHammer.class, "many-bans"));
		this.registerPermissions();
	}

	public void execute(final CommandSender sender) throws CommandPermissionException, CommandUsageException {
		final PlayerRecord playerRecord = PlayerRecord.find(this.database, this.player.getName());
		final List<BanRecord> playerBans = playerRecord.getBans();
		final Iterator<BanRecord> playerBansIter = playerBans.iterator();
		final boolean own = this.getPermissionManager().hasPlayerPermission(sender, this.getPermissions().get(1));
		final boolean others = this.getPermissionManager().hasPlayerPermission(sender, this.getPermissions().get(2));
		int i = 0;

		while (playerBansIter.hasNext()) {
			final BanRecord ban = playerBansIter.next();
			if (!own && (ban.getCreator().getName().equalsIgnoreCase(sender.getName()))) {
				playerBansIter.remove();
				continue;
			}
			if (!others && (!ban.getCreator().getName().equalsIgnoreCase(sender.getName()))) {
				playerBansIter.remove();
				continue;
			}
		}

		if (playerRecord != null) {
			i = BanRecord.deleteBans(this.database, playerBans);
		}

		this.formatter.setArguments(i, this.player.getName());
		sender.sendMessage(this.formatter.getMessage());
	}

	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] arguments) {
		final List<String> list = new ArrayList<String>();
		final Set<String> temp = new TreeSet<String>();
		if (arguments.length <= 1) {
			for (final Player player : this.server.getOnlinePlayers()) {
				if (arguments.length < 1) {
					temp.add(player.getName());
				} else
					if (player.getName().startsWith(arguments[0])) {
						temp.add(player.getName());
					}
				if (arguments[0].length() >= 3) {
					temp.addAll(PlayerRecord.getPlayersWithBansThatStartWith(this.database, arguments[0]));
				}
			}
		}
		list.addAll(temp);
		return list;
	}

	public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
		if (arguments.length == 0) {
			throw new CommandArgumentException(this.getLocalisation().getMessage(BanHammer.class, "must-specify-player"), null);
		} else {
			this.player = this.matchPlayer(arguments[0]);
		}

	}

	private OfflinePlayer matchPlayer(final String name) {
		return this.server.getOfflinePlayer(name);
	}

	private void registerPermissions() {
		final Permission own = this.getPermissionManager().createPermission(this, "own", PermissionDefault.TRUE, this.getPermissions().get(0), true);
		this.addPermission(own);
		final Permission others = this.getPermissionManager().createPermission(this, "others", PermissionDefault.OP, this.getPermissions().get(0), true);
		this.addPermission(others);
	}

	public void execute(List<String> arguments, CommandSender sender) {
		// TODO Auto-generated method stub

	}

}
