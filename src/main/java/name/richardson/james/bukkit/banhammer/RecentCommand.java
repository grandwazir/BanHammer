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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.permissions.Permissible;

import com.google.common.collect.Lists;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.argument.BanCountOptionArgument;
import name.richardson.james.bukkit.utilities.command.argument.IntegerMarshaller;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.*;

public class RecentCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.recent";
	private static final int DEFAULT_LIMIT = 5;
	private final BanRecordManager banRecordManager;
	private IntegerMarshaller count;

	public RecentCommand(BanRecordManager banRecordManager) {
		super(RECENT_COMMAND_NAME, RECENT_COMMAND_DESC);
		this.banRecordManager = banRecordManager;
		this.count = BanCountOptionArgument.getInstance(DEFAULT_LIMIT);
		addArgument(count);
	}

	@Override
	public boolean isAsynchronousCommand() {
		return true;
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL);
	}

	@Override
	protected void execute() {
		int count = this.count.getInteger();
		List<BanRecord> bans = banRecordManager.list(count);
		List<String> messages = new ArrayList<String>();
		if (bans.isEmpty()) {
			messages.add(RECENT_NO_BANS.asInfoMessage());
		} else {
			// reverse the list so the most recent ban is at the bottom of the list
			// this makes sense since the console scrolls down.
			for (BanRecord ban : Lists.reverse(bans)) {
				BanRecord.BanRecordFormatter formatter = ban.getFormatter();
				messages.addAll(formatter.getMessages());
			}
		}
		getContext().getCommandSender().sendMessage(messages.toArray(new String[messages.size()]));
	}

}
