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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.utilities.persistence.SQLStorage;

@Entity()
@Table(name="banhammer_bans")
public class BanRecord {

  public enum State {
    NORMAL,
    EXPIRED,
    PARDONED
  }

  public enum Type {
    PERMENANT,
    TEMPORARY
  }

  public static List<BanRecord> getRecentBans(final EbeanServer database, final int count) {
    return database.find(BanRecord.class).setMaxRows(count).orderBy().desc("createdAt").findList();
  }

  public static int getTemporaryBanCount(final EbeanServer database) {
    return database.find(BanRecord.class).where().isNotNull("expiresAt").findRowCount();
  }
  
  public static int getPermenantBanCount(final EbeanServer database) {
    return database.find(BanRecord.class).where().isNull("expiresAt").findRowCount();
  }
  
  public static int getPardonedBanCount(final EbeanServer database) {
    return database.find(BanRecord.class).where().eq("state", 2).findRowCount();
  }
  
  @Id
  private int id;

  @NotNull
  private int playerId;

  @NotNull
  private int creatorId;

  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  private Timestamp createdAt;

  @NotNull
  private String reason;

  @Temporal(TemporalType.TIMESTAMP)
  private Timestamp expiresAt;

  @NotNull
  private State state;

  @ManyToOne(targetEntity = PlayerRecord.class)
  @JoinColumn(name = "player_id")
  private PlayerRecord player;

  @ManyToOne(optional = false)
  @JoinColumn(name = "creator_id")
  private PlayerRecord creator;

  public Timestamp getCreatedAt() {
    return this.createdAt;
  }

  public Timestamp getCreatedAt(final long time) {
    return this.createdAt;
  }

  @ManyToOne(targetEntity = PlayerRecord.class)
  public PlayerRecord getCreator() {
    return this.creator;
  }

  public int getCreatorId() {
    return this.creatorId;
  }

  public Timestamp getExpiresAt() {
    return this.expiresAt;
  }

  public int getId() {
    return this.id;
  }

  @ManyToOne(targetEntity = PlayerRecord.class)
  public PlayerRecord getPlayer() {
    return this.player;
  }

  public int getPlayerId() {
    return this.playerId;
  }

  public String getReason() {
    return this.reason;
  }

  public State getState() {
    if (this.expiresAt == null) return this.state;
    final Timestamp now = new Timestamp(System.currentTimeMillis());
    return (now.after(this.expiresAt)) ? State.EXPIRED : this.state;
  }

  public BanRecord.Type getType() {
    return (this.expiresAt == null) ? BanRecord.Type.PERMENANT : BanRecord.Type.TEMPORARY;
  }

  public void setCreatedAt(final Timestamp time) {
    this.createdAt = time;
  }

  public void setCreator(final PlayerRecord creator) {
    this.creator = creator;
    this.creatorId = creator.getId();
  }

  public void setCreatorId(final int id) {
    this.creatorId = id;
  }

  public void setExpiresAt(final Timestamp expiresAt) {
    this.expiresAt = expiresAt;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public void setPlayer(final PlayerRecord player) {
    this.player = player;
    this.playerId = player.getId();
  }

  public void setPlayerId(final int playerId) {
    this.playerId = playerId;
  }

  public void setReason(final String reason) {
    this.reason = reason;
  }

  public void setState(final State state) {
    this.state = state;
  }



}
