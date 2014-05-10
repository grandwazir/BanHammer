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
package name.richardson.james.bukkit.banhammer.event;

import java.util.*;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import name.richardson.james.bukkit.banhammer.record.BanRecord;

/**
 * A abstract event from which all other BanHammer events inherit.
 */
public abstract class BanHammerPlayerEvent extends Event {

	/**
	 * The constant listener handlers.
	 */
	private static final HandlerList handlers = new HandlerList();

	public Set<BanRecord> getRecords() {
		return Collections.unmodifiableSet(records);
	}

	/**
	 * The ban record.
	 */
	private final Set<BanRecord> records = new HashSet<BanRecord>();

	/**
	 * If this event is silent.
	 */
	private final boolean silent;

	/**
	 * Instantiates a new BanHammer player event.
	 *
	 * @param records the BanRecord associated with this event
	 * @param silent if this event should be silent to players
	 */
	public BanHammerPlayerEvent(final Collection<BanRecord> records, final boolean silent) {
		this.records.addAll(records);
		this.silent = silent;
	}

	@Override
	public HandlerList getHandlers() {
		return BanHammerPlayerEvent.handlers;
	}

	/**
	 * Checks if this event should be silent.
	 *
	 * @return true, if players should not be notified
	 */
	public boolean isSilent() {
		return this.silent;
	}

}
