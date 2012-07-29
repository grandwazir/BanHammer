/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * PlayerRecord.java is part of BanHammer.
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
package name.richardson.james.bukkit.banhammer.persistence;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "banhammer_players")
public class PlayerRecord {

  /**
   * Check if a PlayerRecord exists for this player.
   * 
   * @param database the database to use
   * @param playerName the player name
   * @return true, if successful
   */
  public static boolean exists(final EbeanServer database, final String playerName) {
    final PlayerRecord record = database.find(PlayerRecord.class).where().ieq("name", playerName).findUnique();
    return (record != null);
  }

  /**
   * Find a PlayerRecord matching a specific player.
   * 
   * @param database the database
   * @param playerName the player name
   * @return the player record
   */
  public static PlayerRecord find(final EbeanServer database, final String playerName) {
    PlayerRecord record = database.find(PlayerRecord.class).where().ieq("name", playerName).findUnique();
    if (record == null) {
      record = new PlayerRecord();
      record.setName(playerName); 
    } 
    return record;
  }
  

  /**
   * Get a list containing all players.
   * 
   * @param database the database
   * @return all the players in the database
   */
  public static List<PlayerRecord> list(final EbeanServer database) {
    return database.find(PlayerRecord.class).findList();
  }
  
  /** The id. */
  @Id
  private int id;

  /** The name. */
  @NotNull
  private String name;

  /** The bans. */
  @OneToMany(mappedBy = "player", targetEntity = BanRecord.class)
  private List<BanRecord> bans;

  /** The created bans. */
  @OneToMany(mappedBy = "creator", targetEntity = BanRecord.class)
  private List<BanRecord> createdBans;

  /**
   * Gets the active ban.
   * 
   * @return the active ban
   */
  public BanRecord getActiveBan() {
    for (final BanRecord ban : this.bans) {
      if (ban.getState() == BanRecord.State.NORMAL) {
        return ban;
      }
    }
    return null;
  }

  /**
   * Gets the bans.
   * 
   * @return the bans
   */
  @OneToMany(targetEntity = BanRecord.class, fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
  public List<BanRecord> getBans() {
    return (this.bans == null) ? new LinkedList<BanRecord>() : this.bans;
  }

  /**
   * Gets the created bans.
   * 
   * @return the created bans
   */
  @OneToMany(targetEntity = BanRecord.class, fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
  public List<BanRecord> getCreatedBans() {
    return this.createdBans;
  }

  /**
   * Gets the id.
   * 
   * @return the id
   */
  public int getId() {
    return this.id;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Checks if is banned.
   * 
   * @return true, if is banned
   */
  public boolean isBanned() {
    for (final BanRecord ban : this.bans) {
      if (ban.getState() == BanRecord.State.NORMAL) {
        return true;
      }
    }
    return false;
  }

  /**
   * Sets the bans.
   * 
   * @param records the new bans
   */
  public void setBans(final List<BanRecord> records) {
    this.bans = records;
  }

  /**
   * Sets the created bans.
   * 
   * @param records the new created bans
   */
  public void setCreatedBans(final List<BanRecord> records) {
    this.createdBans = records;
  }

  /**
   * Sets the id.
   * 
   * @param id the new id
   */
  public void setId(final int id) {
    this.id = id;
  }

  /**
   * Sets the name.
   * 
   * @param name the new name
   */
  public void setName(final String name) {
    this.name = name;
  }

}
