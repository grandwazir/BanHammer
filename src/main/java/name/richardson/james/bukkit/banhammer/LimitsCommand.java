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
package name.richardson.james.bukkit.banhammer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.permissions.Permissible;

import org.apache.commons.lang.StringUtils;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.formatters.time.PreciseDurationTimeFormatter;
import name.richardson.james.bukkit.utilities.formatters.time.TimeFormatter;
import name.richardson.james.bukkit.utilities.localisation.PluginLocalisation;

import name.richardson.james.bukkit.banhammer.utilities.formatters.BanLimitChoiceFormatter;
import name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation;

public class LimitsCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.limits";

	private final ChoiceFormatter choiceFormatter = new BanLimitChoiceFormatter();
	private final Map<String, Long> limits;
	private final TimeFormatter timeFormatter = new PreciseDurationTimeFormatter();

	public LimitsCommand(Map<String, Long> limits) {
		this.limits = limits;
		this.choiceFormatter.setArguments(limits.size());
		this.choiceFormatter.setMessage(getLocalisation().formatAsHeaderMessage(BanHammerLocalisation.LIMIT_SUMMARY));
	}

	@Override
	public void execute(CommandContext context) {
		if (isAuthorised(context.getCommandSender())) {
			List<String> messages = new ArrayList<String>(limits.size());
			for (final Entry<String, Long> limit : this.limits.entrySet()) {
				ChatColor colour = ChatColor.RED;
				if (context.getCommandSender().hasPermission(BanCommand.PERMISSION_ALL + "." + limit.getKey())) colour = ChatColor.GREEN;
				String time = timeFormatter.getHumanReadableDuration(limit.getValue());
				messages.add(colour + getLocalisation().getMessage(BanHammerLocalisation.LIMIT_ENTRY, limit.getKey(), time));
			}
			context.getCommandSender().sendMessage(choiceFormatter.getMessage());
			context.getCommandSender().sendMessage(StringUtils.join(messages, ", "));
		} else {
			String message = getLocalisation().formatAsErrorMessage(PluginLocalisation.COMMAND_NO_PERMISSION);
			context.getCommandSender().sendMessage(message);
		}
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL);
	}

}
