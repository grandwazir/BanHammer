/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * BanHammerPlayerPardonedEvent.java is part of BanHammer.
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

import java.util.Collection;

import org.bukkit.event.HandlerList;

import name.richardson.james.bukkit.banhammer.record.BanRecord;
import name.richardson.james.bukkit.banhammer.record.CurrentBanRecord;

/**
 * This event is fired every time a player is pardoned through BanHammer.
 */
public class BanHammerPlayerPardonedEvent extends BanHammerPlayerEvent {

	private static final HandlerList handlers = new HandlerList();

	public String getSource() {
		return source;
	}

	private final String source;

	/**
	 * Instantiates a new BanHammer player event.
	 *
	 * @param records the BanRecord associated with this event
	 * @param silent if this event should be silent to players
	 */
	public BanHammerPlayerPardonedEvent(final Collection<BanRecord> records, final boolean silent, String source) {
		super(records, silent);
		this.source = source;
	}

	public static HandlerList getHandlerList() {
		return BanHammerPlayerPardonedEvent.handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return BanHammerPlayerPardonedEvent.handlers;
	}

}
