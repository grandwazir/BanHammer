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
package name.richardson.james.bukkit.banhammer.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord;

public abstract class BanHammerPlayerEvent extends Event {

  private static final HandlerList handlers = new HandlerList();
  
  private static final long serialVersionUID = 1L;

  private final String playerName;

  private final BanRecord record;

  private final boolean silent;

  public BanHammerPlayerEvent(final BanRecord record, final boolean silent) {
    this.record = record;
    this.playerName = record.getPlayer().getName();
    this.silent = silent;
  }

  public String getPlayerName() {
    return this.playerName;
  }

  public BanRecord getRecord() {
    return this.record;
  }

  public boolean isSilent() {
    return this.silent;
  }

}
