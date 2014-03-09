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

import org.bukkit.permissions.Permissible;

import com.google.common.collect.Lists;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.localisation.PluginLocalisation;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation;

public class RecentCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.recent";
	private static final int DEFAULT_LIMIT = 5;

	private final BanRecordManager banRecordManager;
	private int count;

	public RecentCommand(BanRecordManager banRecordManager) {
		this.banRecordManager = banRecordManager;
	}

	@Override
	public void execute(CommandContext context) {
		if (isAuthorised(context.getCommandSender())) {
			setLimit(context);
			List<BanRecord> bans = banRecordManager.list(count);
			if (bans.size() == 0) {
				String message = getLocalisation().formatAsInfoMessage(BanHammerLocalisation.RECENT_NO_BANS);
				context.getCommandSender().sendMessage(message);
			}
			// reverse the list so the most recent ban is at the bottom of the list
			// this makes sense since the console scrolls down.
			bans = Lists.reverse(bans);
			for (BanRecord ban : bans) {
				BanRecord.BanRecordFormatter formatter = ban.getFormatter();
				context.getCommandSender().sendMessage(formatter.getMessages());
			}
		} else {
			String message = getLocalisation().formatAsErrorMessage(PluginLocalisation.COMMAND_NO_PERMISSION);
			context.getCommandSender().sendMessage(message);
		}
	}

	private void setLimit(CommandContext context) {
		try {
			count = Integer.parseInt(context.getString(0));
		} catch (NumberFormatException e) {
			count = DEFAULT_LIMIT;
		}
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL);
	}

}
