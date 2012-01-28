/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * BanRecord.java is part of BanHammer.
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
package name.richardson.james.bukkit.banhammer;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.util.Logger;


@Entity()
@Table(name = "bh_bans")
public class BanRecord {

  public enum Type {
    PERMENANT, TEMPORARY
  }

  private final static Logger logger = new Logger(BanRecord.class);
  
  @Id
  private long createdAt;

  @NotNull
  private String createdBy;

  @NotNull
  private long expiresAt;

  @NotNull
  private String player;

  @NotNull
  private String reason;
  

  static public List<BanRecord> findByName(final DatabaseHandler database, String player) {
    logger.debug(String.format("Attempting to return an BanRecords matching the player name %s.", player));
    return database.getEbeanServer().find(BanRecord.class).where().ilike("player", player).orderBy("created_at DESC").findList();
  }

  static public BanRecord findFirstByName(final DatabaseHandler database, String player) {
    logger.debug(String.format("Attempting to return an active BanRecord matching the player name %s.", player));
    try {
      return database.getEbeanServer().find(BanRecord.class).where().ilike("player", player).orderBy("created_at DESC").findList().get(0);
    } catch (IndexOutOfBoundsException exception) {
      return null;
    }
  }

  static public List<BanRecord> findRecent(final DatabaseHandler database, Integer maxRows) {
    return database.getEbeanServer().find(BanRecord.class).where().orderBy("created_at DESC").setMaxRows(maxRows).findList();
  }

  public long getCreatedAt() {
    return this.createdAt;
  }

  public String getCreatedBy() {
    return this.createdBy;
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
    if (this.expiresAt == 0) {
      return Type.PERMENANT;
    } else {
      return Type.TEMPORARY;
    }
  }

  public boolean isActive() {
    if (this.expiresAt == 0) {
      return true;
    } else if (this.expiresAt > System.currentTimeMillis()) {
      return true;
    } else {
      return false;
    }
  }
  
  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public void setExpiresAt(long expiresAt) {
    this.expiresAt = expiresAt;
  }

  public void setPlayer(String player) {
    this.player = player;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

}
