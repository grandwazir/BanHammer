/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * CachedBan.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with BanHammer.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.ban;

import name.richardson.james.bukkit.banhammer.ban.BanRecord.Type;


public final class CachedBan {

  private long expiresAt;
  private String player;
  private String reason;
  private String createdBy;
  private long createdAt;

  public CachedBan(Long expiresAt, String player, String reason, String createdBy, Long createdAt) {
    this.expiresAt = expiresAt;
    this.player = player;
    this.reason = reason;
    this.createdBy = createdBy;
    this.createdAt = createdAt;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public long getExpiresAt() {
    return this.expiresAt;
  }

  public String getPlayer() {
    return this.player;
  }

  public String getReason() {
    return this.reason;
  }


  public Type getType() {
    if (this.expiresAt == 0)
      return Type.PERMENANT;
    else return Type.TEMPORARY;
  }


  public boolean isActive() {
    if (this.expiresAt == 0)
      return true;
    else if (this.expiresAt > System.currentTimeMillis())
      return true;
    else return false;
  }

}
