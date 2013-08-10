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

import org.apache.commons.lang.StringUtils;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;
import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;
import name.richardson.james.bukkit.utilities.permissions.Permissions;

@Permissions(permissions = {LimitsCommand.PERMISSION_ALL})
public class LimitsCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.limits";

	private final Map<String, Long> limits;

	public LimitsCommand(PermissionManager permissionManager,  Map<String, Long> limits) {
		super(permissionManager);
		this.limits = limits;
	}

	@Override
	public void execute(CommandContext context) {
		if (isAuthorised(context.getCommandSender())) {
			List<String> messages = new ArrayList<String>(limits.size());
			for (final Entry<String, Long> limit : this.limits.entrySet()) {
				ChatColor colour = ChatColor.RED;
				if (context.getCommandSender().hasPermission(BanCommand.PERMISSION_ALL + "." + limit.getKey())) colour = ChatColor.GREEN;
				messages.add(colour + getMessage("limits-list-item", limit.getKey(), TimeFormatter.millisToLongDHMS(limit.getValue())));
			}
			context.getCommandSender().sendMessage(StringUtils.join(messages, ", "));
		} else {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "no-permission"));
		}
	}

}
