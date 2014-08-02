/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * BanHammerPlayerEvent.java is part of BanHammer.
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

import name.richardson.james.bukkit.banhammer.BanRecord;

public abstract class AbstractBanHammerPlayerEvent extends Event implements BanHammerPlayerEvent {

	private final CommandSender commandSender;
	private final Set<BanRecord> records = new HashSet<BanRecord>();
	private final boolean silent;

	public AbstractBanHammerPlayerEvent(final Collection<BanRecord> records, CommandSender commandSender, final boolean silent) {
		this.commandSender = commandSender;
		this.records.addAll(records);
		this.silent = silent;
		Bukkit.getPluginManager().callEvent(this);
	}

	public CommandSender getCommandSender() {
		return commandSender;
	}

	@Override public Set<BanRecord> getRecords() {
		return Collections.unmodifiableSet(records);
	}

	@Override public boolean isSilent() {
		return this.silent;
	}

}
