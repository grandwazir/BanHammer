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
package name.richardson.james.banhammer.ban;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.ExampleExpression;
import com.avaje.ebean.LikeType;
import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "bh_bans")
public class BanRecord {

  public enum Type {
    PERMENANT, TEMPORARY
  }

  private static EbeanServer database;

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

  public static void setDatabase(EbeanServer database) {
    if (BanRecord.database == null) {
      BanRecord.database = database;
    }
  }
  
  static void create(String playerName, String senderName, Long Expiry, Long creationTime, String banReason) {
    BanRecord banHammerRecord = new BanRecord();
    banHammerRecord.player = playerName;
    banHammerRecord.createdBy = senderName;
    banHammerRecord.createdAt = creationTime;
    banHammerRecord.expiresAt = Expiry;
    banHammerRecord.reason = banReason;
    BanRecord.database.save(banHammerRecord);
  }

  static void destroy(List<BanRecord> banHammerRecords) {
    for (BanRecord ban : banHammerRecords)
      BanRecord.database.delete(ban);
  }

  static List<BanRecord> find(String player) {
    // create the example
    BanRecord example = new BanRecord();
    example.setPlayer(player);
    // create the example expression
    ExampleExpression expression = BanRecord.database.getExpressionFactory().exampleLike(example, true, LikeType.EQUAL_TO);
    // find and return all bans that match the expression
    return BanRecord.database.find(BanRecord.class).where().add(expression).orderBy("created_at DESC").findList();
  }

  static BanRecord findFirst(String player) {
    // create the example
    BanRecord example = new BanRecord();
    example.setPlayer(player);
    // create the example expression
    ExampleExpression expression = BanRecord.database.getExpressionFactory().exampleLike(example, true, LikeType.EQUAL_TO);
    // find and return all bans that match the expression
    return BanRecord.database.find(BanRecord.class).where().add(expression).orderBy("created_at DESC").findList().get(0);
  }

  static List<BanRecord> findRecent(Integer maxRows) {
    return BanRecord.database.find(BanRecord.class).where().orderBy("created_at DESC").setMaxRows(maxRows).findList();
  }

  static List<BanRecord> list() {
    return BanRecord.database.find(BanRecord.class).findList();
  }

  private void setPlayer(String player) {
    this.player = player;
  }

  void destroy() {
    BanRecord.database.delete(this);
  }
  
  long getCreatedAt() {
    return this.createdAt;
  }

  String getCreatedBy() {
    return this.createdBy;
  }

  long getExpiresAt() {
    return this.expiresAt;
  }

  String getPlayer() {
    return this.player;
  }

  String getReason() {
    return this.reason;
  }

  Type getType() {
    if (this.expiresAt == 0)
      return Type.PERMENANT;
    else return Type.TEMPORARY;
  }

  boolean isActive() {
    if (this.expiresAt == 0)
      return true;
    else if (this.expiresAt > System.currentTimeMillis())
      return true;
    else return false;
  }

  CachedBan toCachedBan() {
    return new CachedBan(this.expiresAt, this.player, this.reason, this.createdBy, this.createdAt);
  }

}
