/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * BanRecord.java is part of BanHammer.
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

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "banhammer_bans")
public class BanRecord {

  /**
   * The valid states of a BanRecord
   */
  public enum State {

    /** If a ban is currently active. */
    NORMAL,

    /** If a ban has expired. */
    EXPIRED,

    /** If a ban has been pardoned. */
    PARDONED
  }

  /**
   * The valid types of a BanRecord
   */
  public enum Type {

    /** A ban which will never expire. */
    PERMANENT,

    /** A ban which will expire after a period of time. */
    TEMPORARY
  }

  /**
   * Removes bans without an optimistic lock error
   * One of these days I will learn why it always throws these.
   * 
   * @param database the database
   * @param bans the bans to delete
   * @return the number of bans deleted.
   */
  public static int deleteBans(EbeanServer database, List<BanRecord> bans) {
    int i = 0;
    for (BanRecord ban : bans) {
      i++;
      database.createSqlUpdate("DELETE from banhammer_bans WHERE id='" + ban.getId()+ "'").execute();
    }
    return i;
  }
  
  /**
   * Gets the total number of pardoned bans.
   * 
   * @param database the database
   * @return the pardoned ban count
   */
  public static int getPardonedBanCount(final EbeanServer database) {
    return database.find(BanRecord.class).where().eq("state", 2).findRowCount();
  }

  /**
   * Gets the total number of permanent bans.
   * 
   * @param database the database
   * @return the permanent ban count
   */
  public static int getPermanentBanCount(final EbeanServer database) {
    return database.find(BanRecord.class).where().isNull("expiresAt").findRowCount();
  }

  /**
   * Gets a list of the recent bans.
   * 
   * @param database the database
   * @param count the number of bans to retrieve
   * @return the recent bans, with the most recent first
   */
  public static List<BanRecord> getRecentBans(final EbeanServer database, final int count) {
    return database.find(BanRecord.class).setMaxRows(count).orderBy().desc("createdAt").findList();
  }

  /**
   * Gets the total number of temporary bans.
   * 
   * @param database the database
   * @return the temporary ban count
   */
  public static int getTemporaryBanCount(final EbeanServer database) {
    return database.find(BanRecord.class).where().isNotNull("expiresAt").findRowCount();
  }

  /**
   * Get a list of only bans which are currently active.
   * 
   * @param database the database
   * @return all the bans which are currently active.
   */
  public static List<BanRecord> listActive(final EbeanServer database) {
    return database.find(BanRecord.class).where().eq("state", State.NORMAL).findList();
  }

  /**
   * Get a list containing all bans.
   * 
   * @param database the database
   * @return all the bans in the database
   */
  public static List<BanRecord> list(final EbeanServer database) {
    return database.find(BanRecord.class).findList();
  }
  
  /** The id. */
  @Id
  private int id;

  /** The created at. */
  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  private Timestamp createdAt;

  /** The reason. */
  @NotNull
  private String reason;

  /** The expires at. */
  @Temporal(TemporalType.TIMESTAMP)
  private Timestamp expiresAt;

  /** The state. */
  @NotNull
  private State state;

  /** The player. */
  @ManyToOne(targetEntity = PlayerRecord.class, fetch = FetchType.EAGER, cascade={CascadeType.PERSIST})
  @PrimaryKeyJoinColumn(name = "playerId", referencedColumnName = "id")
  private PlayerRecord player;

  /** The creator. */
  @ManyToOne(targetEntity = PlayerRecord.class, fetch = FetchType.EAGER, cascade={CascadeType.PERSIST})
  @PrimaryKeyJoinColumn(name = "creatorId", referencedColumnName = "id")
  private PlayerRecord creator;

  /**
   * Gets the created at.
   * 
   * @return the created at
   */
  public Timestamp getCreatedAt() {
    return this.createdAt;
  }

  /**
   * Gets the created at.
   * 
   * @param time the time
   * @return the created at
   */
  public Timestamp getCreatedAt(final long time) {
    return this.createdAt;
  }

  /**
   * Gets the creator.
   * 
   * @return the creator
   */
  @ManyToOne(targetEntity = PlayerRecord.class)
  public PlayerRecord getCreator() {
    return this.creator;
  }

  /**
   * Gets the expires at.
   * 
   * @return the expires at
   */
  public Timestamp getExpiresAt() {
    return this.expiresAt;
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
   * Gets the player.
   * 
   * @return the player
   */
  public PlayerRecord getPlayer() {
    return this.player;
  }

  /**
   * Gets the reason.
   * 
   * @return the reason
   */
  public String getReason() {
    return this.reason;
  }

  /**
   * Gets the state.
   * 
   * @return the state
   */
  public State getState() {
    if (this.expiresAt == null) {
      return this.state;
    }
    final Timestamp now = new Timestamp(System.currentTimeMillis());
    return (now.after(this.expiresAt)) ? State.EXPIRED : this.state;
  }

  /**
   * Gets the type.
   * 
   * @return the type
   */
  public BanRecord.Type getType() {
    return (this.expiresAt == null) ? BanRecord.Type.PERMANENT : BanRecord.Type.TEMPORARY;
  }

  /**
   * Sets the created at.
   * 
   * @param time the new created at
   */
  public void setCreatedAt(final Timestamp time) {
    this.createdAt = time;
  }

  /**
   * Sets the creator.
   * 
   * @param creator the new creator
   */
  public void setCreator(final PlayerRecord creator) {
    this.creator = creator;
  }

  /**
   * Sets the expires at.
   * 
   * @param expiresAt the new expires at
   */
  public void setExpiresAt(final Timestamp expiresAt) {
    this.expiresAt = expiresAt;
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
   * Sets the player.
   * 
   * @param player the new player
   */
  public void setPlayer(final PlayerRecord player) {
    this.player = player;
  }

  /**
   * Sets the reason.
   * 
   * @param reason the new reason
   */
  public void setReason(final String reason) {
    this.reason = reason;
  }

  /**
   * Sets the state.
   * 
   * @param state the new state
   */
  public void setState(final State state) {
    this.state = state;
  }

}
