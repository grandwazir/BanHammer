/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * RecentCommand.java is part of BanHammer.
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

import java.util.List;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;
import name.richardson.james.bukkit.utilities.permissions.Permissions;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordFormatter;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;

@Permissions(permissions = {RecentCommand.PERMISSION_ALL})
public class RecentCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.recent";
	public static final int DEFAULT_LIMIT = 5;

	private final BanRecordManager banRecordManager;

	private int count;

	public RecentCommand(PermissionManager permissionManager, BanRecordManager banRecordManager) {
		super(permissionManager);
		this.banRecordManager = banRecordManager;
	}

	@Override
	public void execute(CommandContext context) {
		if (isAuthorised(context.getCommandSender())) {
			setLimit(context);
			List<BanRecord> bans = banRecordManager.list(count);
			for (BanRecord ban : bans) {
				BanRecordFormatter formatter = new BanRecordFormatter(ban);
				context.getCommandSender().sendMessage(formatter.getMessages());
			}
		} else {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "no-permission"));
		}
	}

	private void setLimit(CommandContext context) {
		count = DEFAULT_LIMIT;
		if (context.has(0)) count = context.getInt(0);
	}

}
