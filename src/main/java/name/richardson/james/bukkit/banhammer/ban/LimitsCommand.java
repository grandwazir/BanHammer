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

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractAsynchronousCommand;
import name.richardson.james.bukkit.utilities.time.PreciseDurationTimeFormatter;
import name.richardson.james.bukkit.utilities.time.TimeFormatter;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.PluginConfiguration;

public class LimitsCommand extends AbstractAsynchronousCommand {

	public static final String PERMISSION_ALL = "banhammer.limits";
	private static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	private final PluginConfiguration configuration;
	private TimeFormatter timeFormatter = new PreciseDurationTimeFormatter();

	public LimitsCommand(final Plugin plugin, final BukkitScheduler scheduler, PluginConfiguration configuration) {
		super(plugin, scheduler);
		this.configuration = configuration;
	}

	@Override public String getName() {
		return MESSAGES.limitCommandName();
	}

	@Override public Set<String> getPermissions() {
		Set<String> limitNames = configuration.getBanLimits().keySet();
		Set<String> permissions = new HashSet<>();
		permissions.add(PERMISSION_ALL);
		for (String limit : limitNames) {
			permissions.add(BanCommand.getPermissionFromLimit(limit));
		}
		return permissions;
	}

	@Override public String getDescription() {
		return MESSAGES.limitCommandDescription();
	}

	@Override
	protected void execute() {
		Map<String, Long> limits = configuration.getBanLimits();
		addMessage(MESSAGES.limitsFound(limits.size()));
		for (final Entry<String, Long> limit : limits.entrySet()) {
			final String time = timeFormatter.getHumanReadableDuration(limit.getValue());
			ChatColor colour = ChatColor.RED;
			String limitName = limit.getKey();
			if (getContext().isAuthorised(BanCommand.getPermissionFromLimit(limitName))) colour = ChatColor.GREEN;
			addMessage(colour + "- " + limitName + "(" + time + ")");
		}
	}

}
