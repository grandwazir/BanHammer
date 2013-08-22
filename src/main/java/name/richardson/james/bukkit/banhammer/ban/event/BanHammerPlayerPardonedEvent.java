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
package name.richardson.james.bukkit.banhammer.ban.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;

/**
 * This event is fired every time a player is pardoned through BanHammer.
 */
public class BanHammerPlayerPardonedEvent extends BanHammerPlayerEvent {

	private static final HandlerList handlers = new HandlerList();
	private final CommandSender sender;

	public static HandlerList getHandlerList() {
		return BanHammerPlayerPardonedEvent.handlers;
	}

	public BanHammerPlayerPardonedEvent(final BanRecord record, CommandSender sender,  final boolean silent) {
		super(record, silent);
		this.sender = sender;
	}

	@Override
	public HandlerList getHandlers() {
		return BanHammerPlayerPardonedEvent.handlers;
	}

	public CommandSender getSender() {
		return sender;
	}

}
