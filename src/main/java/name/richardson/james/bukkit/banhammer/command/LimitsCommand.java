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
package name.richardson.james.bukkit.banhammer.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import com.vityuk.ginger.Localization;
import com.vityuk.ginger.LocalizationBuilder;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;

import name.richardson.james.bukkit.banhammer.excluded.PluginConfiguration;
import name.richardson.james.bukkit.banhammer.localisation.Messages;

public class LimitsCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.limits";

	private static final Localization LOCALIZATION = new LocalizationBuilder().withResourceLocation(Messages.PATH).build();
	private static final Messages MESSAGES = LOCALIZATION.getLocalizable(Messages.class);
	private static final Constants CONSTANTS = LOCALIZATION.getLocalizable(Constants.class);

	private final Map<String, Long> limits;

	public LimitsCommand(PluginConfiguration configuration) {
		this.limits = configuration.getBanLimits();
	}

	@Override
	public boolean isAsynchronousCommand() {
		return false;
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL);
	}

	@Override public String getName() {
		return CONSTANTS.limitCommandName();
	}

	@Override public String getDescription() {
		return CONSTANTS.limitCommandDescription();
	}

	@Override
	protected void execute() {
		final List<String> messages = new ArrayList<String>(limits.size());
		final CommandSender commandSender = getContext().getCommandSender();
		messages.add(MESSAGES.limitsFound(limits.size()));
		for (final Entry<String, Long> limit : this.limits.entrySet()) {
			// final String time = timeFormatter.getHumanReadableDuration(limit.getValue());
			ChatColor colour = ChatColor.RED;
			if (commandSender.hasPermission(BanCommand.PERMISSION_ALL + "." + limit.getKey())) colour = ChatColor.GREEN;
			messages.add(colour + LIMIT_ENTRY.asMessage(limit.getKey(), 0));
		}
		commandSender.sendMessage(messages.toArray(new String[messages.size()]));
	}

}
