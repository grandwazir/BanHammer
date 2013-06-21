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

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;

@ConsoleCommand
@CommandPermissions(permissions = {"banhammer.limits"})
public class LimitsCommand extends AbstractCommand {

	private final ChoiceFormatter formatter;
	private final Map<String, Long> limits;

	public LimitsCommand(final Map<String, Long> limits) {
		super();
		this.limits = limits;
		this.formatter = new ChoiceFormatter(this.getClass());
		this.formatter.setLimits(0, 1, 2);
		this.formatter.setMessage("limits-header");
		this.formatter.setArguments(limits.size());
		this.formatter.setFormats("no-limits", "many-limits");
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		if (this.limits.isEmpty()) {
			// No no, no no no no, no no no no, no no there no limits!
			sender.sendMessage(this.formatter.getMessage());
		} else {
			sender.sendMessage(this.formatter.getMessage());
			for (final Entry<String, Long> limit : this.limits.entrySet()) {
				ChatColor colour;
				if (sender.hasPermission("banhammer.ban." + limit.getKey())) {
					colour = ChatColor.GREEN;
				} else {
					colour = ChatColor.RED;
				}
				String message = MessageFormat.format(this.getLocalisation().getString("limits-list-item"), limit.getKey(), TimeFormatter.millisToLongDHMS(limit.getValue()));
				sender.sendMessage(colour + message);
			}
		}
	}

}
