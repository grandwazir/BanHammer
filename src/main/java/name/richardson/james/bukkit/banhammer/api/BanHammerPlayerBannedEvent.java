/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * BanHammerPlayerBannedEvent.java is part of BanHammer.
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
package name.richardson.james.bukkit.banhammer.api;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord;

import org.bukkit.event.HandlerList;

// TODO: Auto-generated Javadoc
/**
 * The Class BanHammerPlayerBannedEvent.
 */
public class BanHammerPlayerBannedEvent extends BanHammerPlayerEvent {

  /** The Constant handlers. */
  private static final HandlerList handlers = new HandlerList();

  /*
   * (non-Javadoc)
   * @see org.bukkit.event.Event#getHandlerList()
   */
  public static HandlerList getHandlerList() {
    return BanHammerPlayerBannedEvent.handlers;
  }

  /*
   * (non-Javadoc)
   * @see name.richardson.james.bukkit.banhammer.api.BanHammerPlayerEvent#
   * BanHammerPlayerEvent()
   */
  public BanHammerPlayerBannedEvent(final BanRecord record, final boolean silent) {
    super(record, silent);
  }

  /*
   * (non-Javadoc)
   * @see org.bukkit.event.Event#getHandlers()
   */
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

}
