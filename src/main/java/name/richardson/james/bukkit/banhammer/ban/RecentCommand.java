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
package name.richardson.james.bukkit.banhammer.ban;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractAsynchronousCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.IntegerMarshaller;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.argument.BanCountOptionArgument;

public class RecentCommand extends AbstractAsynchronousCommand {

	private static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	private static final int DEFAULT_LIMIT = 5;
	private static final String PERMISSION_ALL = "banhammer.recent";
	private IntegerMarshaller count;

	protected RecentCommand(final Plugin plugin, final BukkitScheduler scheduler) {
		super(plugin, scheduler);
		count = BanCountOptionArgument.getInstance(DEFAULT_LIMIT);
		addArgument((Argument) count);
	}

	@Override public String getDescription() {
		return MESSAGES.recentCommandDescription();
	}

	@Override public String getName() {
		return MESSAGES.recentCommandName();
	}

	@Override public Set<String> getPermissions() {
		return new HashSet<>(Arrays.asList(PERMISSION_ALL));
	}

	@Override
	protected void execute() {
		int count = this.count.getInteger();
		List<BanRecord> bans = BanRecord.list(count);
		if (bans.isEmpty()) {
			addMessage(MESSAGES.noBansMade());
		} else {
			for (BanRecord ban : bans) {
				BanRecordFormatter formatter = ban.getFormatter();
				addMessages(formatter.getMessages());
			}
		}
	}

}
