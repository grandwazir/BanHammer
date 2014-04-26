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
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.formatters.time.PreciseDurationTimeFormatter;
import name.richardson.james.bukkit.utilities.formatters.time.TimeFormatter;

import name.richardson.james.bukkit.banhammer.utilities.formatters.BanLimitChoiceFormatter;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.*;

public class LimitsCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.limits";

	private final ChoiceFormatter choiceFormatter = new BanLimitChoiceFormatter();
	private final Map<String, Long> limits;
	private final TimeFormatter timeFormatter = new PreciseDurationTimeFormatter();

	public LimitsCommand(Map<String, Long> limits) {
		super(LIMIT_COMMAND_NAME, LIMIT_COMMAND_DESC);
		this.limits = limits;
		this.choiceFormatter.setArguments(limits.size());
		this.choiceFormatter.setMessage(LIMIT_SUMMARY.asHeaderMessage());
	}

	@Override
	protected void execute() {
		List<String> messages = new ArrayList<String>(limits.size());
		final CommandSender commandSender = getContext().getCommandSender();
		for (final Entry<String, Long> limit : this.limits.entrySet()) {
			ChatColor colour = ChatColor.RED;
			if (commandSender.hasPermission(BanCommand.PERMISSION_ALL + "." + limit.getKey())) colour = ChatColor.GREEN;
			String time = timeFormatter.getHumanReadableDuration(limit.getValue());
			messages.add(colour + LIMIT_ENTRY.asMessage(limit.getKey(), time));
		}
		commandSender.sendMessage(choiceFormatter.getMessage());
		commandSender.sendMessage(messages.toArray(new String[messages.size()]));
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL);
	}

	@Override
	public boolean isAsynchronousCommand() {
		return false;
	}

}
