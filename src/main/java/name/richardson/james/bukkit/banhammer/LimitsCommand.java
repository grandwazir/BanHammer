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
import name.richardson.james.bukkit.utilities.formatters.*;
import name.richardson.james.bukkit.utilities.localisation.Localisation;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundleByClassLocalisation;

import name.richardson.james.bukkit.banhammer.utilities.formatters.BanLimitChoiceFormatter;

public class LimitsCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.limits";

	private static final String HEADER_KEY = "header";
	private static final String LIMIT_KEY = "limit";
	private static final String NO_PERMISSION_KEY = "no-permission";

	private final Map<String, Long> limits;
	private final ChoiceFormatter choiceFormatter = new BanLimitChoiceFormatter();
	private final Localisation localisation = new ResourceBundleByClassLocalisation(LimitsCommand.class);
	private final ColourFormatter colourFormatter = new DefaultColourFormatter();
	private final TimeFormatter timeFormatter = new PreciseDurationTimeFormatter();

	public LimitsCommand(Map<String, Long> limits) {
		this.limits = limits;
		this.choiceFormatter.setArguments(limits.size());
		this.choiceFormatter.setMessage(colourFormatter.format(localisation.getMessage(HEADER_KEY), ColourFormatter.FormatStyle.HEADER));
	}

	@Override
	public void execute(CommandContext context) {
		if (isAuthorised(context.getCommandSender())) {
			List<String> messages = new ArrayList<String>(limits.size());
			for (final Entry<String, Long> limit : this.limits.entrySet()) {
				ChatColor colour = ChatColor.RED;
				if (context.getCommandSender().hasPermission(BanCommand.PERMISSION_ALL + "." + limit.getKey())) colour = ChatColor.GREEN;
				String time = timeFormatter.getHumanReadableDuration(limit.getValue());
				messages.add(colour + localisation.getMessage(LIMIT_KEY, limit.getKey(), time));
			}
			context.getCommandSender().sendMessage(choiceFormatter.getMessage());
			context.getCommandSender().sendMessage(StringUtils.join(messages, ", "));
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
