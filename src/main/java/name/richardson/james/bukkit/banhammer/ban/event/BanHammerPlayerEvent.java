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
package name.richardson.james.bukkit.banhammer.ban.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import name.richardson.james.bukkit.banhammer.ban.OldBanRecord;

/**
 * A abstract event from which all other BanHammer events inherit.
 */
public abstract class BanHammerPlayerEvent extends Event {

	/**
	 * The constant listener handlers.
	 */
	private static final HandlerList handlers = new HandlerList();

	/**
	 * The player name.
	 */
	private final String playerName;
	/**
	 * The ban record.
	 */
	private final OldBanRecord record;
	/**
	 * If this event is silent.
	 */
	private final boolean silent;

	/**
	 * Instantiates a new BanHammer player event.
	 *
	 * @param record the OldBanRecord associated with this event
	 * @param silent if this event should be silent to players
	 */
	public BanHammerPlayerEvent(final OldBanRecord record, final boolean silent) {
		this.record = record;
		this.playerName = record.getPlayer().getName();
		this.silent = silent;
	}

	@Override
	public HandlerList getHandlers() {
		return BanHammerPlayerEvent.handlers;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public OldBanRecord getRecord() {
		return this.record;
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
