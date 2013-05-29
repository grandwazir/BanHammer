/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * LimitsCommand.java is part of BanHammer.
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;

@ConsoleCommand
public class LimitsCommand extends AbstractCommand {

	private final ChoiceFormatter formatter;

	/* The ban limits */
	private final Map<String, Long> limits;

	public LimitsCommand(final BanHammer plugin, final Map<String, Long> limits) {
		super(plugin);
		this.limits = limits;
		this.formatter = new ChoiceFormatter(this.getLocalisation());
		this.formatter.setLimits(0, 1, 2);
		this.formatter.setMessage(this, "header");
		this.formatter.setArguments(limits.size());
		this.formatter.setFormats(this.getLocalisation().getMessage(this, "no-limit"), this.getLocalisation().getMessage(this, "one-limits"), this
			.getLocalisation().getMessage(this, "many-limits"));
	}

	public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
		sender.sendMessage(this.formatter.getMessage());
		for (final Entry<String, Long> limit : this.limits.entrySet()) {
			ChatColor colour;
			if (sender.hasPermission("banhammer.ban." + limit.getKey())) {
				colour = ChatColor.GREEN;
			} else {
				colour = ChatColor.RED;
			}
			sender.sendMessage(colour + this.getLocalisation().getMessage(this, "list-item", limit.getKey(), TimeFormatter.millisToLongDHMS(limit.getValue())));
		}
	}

	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] arguments) {
		final List<String> list = new ArrayList<String>();
		return list;
	}

	public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
		return;
	}

	public void execute(List<String> arguments, CommandSender sender) {
		// TODO Auto-generated method stub

	}

}
