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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord.State;
import name.richardson.james.bukkit.utilities.persistence.SQLStorage;

@Entity()
@Table(name="banhammer_players")
public class PlayerRecord {

  public static boolean exists(final EbeanServer database, final String playerName) {
    final PlayerRecord record = database.find(PlayerRecord.class).where().ieq("name", playerName).findUnique();
    return (record != null);
  }

  public static PlayerRecord find(final EbeanServer database, final String playerName) {
    return database.find(PlayerRecord.class).where().ieq("name", playerName).findUnique();
  }
  
  public static List<PlayerRecord> findBanned(final EbeanServer database) {
    return database.find(PlayerRecord.class).where().eq("state", State.NORMAL).findList();
  }

  @Id
  private int id;

  @NotNull
  private String name;

  @OneToMany(mappedBy = "player", targetEntity = BanRecord.class)
  private List<BanRecord> bans;

  @OneToMany(mappedBy = "creator", targetEntity = BanRecord.class)
  private List<BanRecord> createdBans;

  public BanRecord getActiveBan() {
    for (final BanRecord ban : this.bans) {
      if (ban.getState() == BanRecord.State.NORMAL) {
        return ban;
      }
    }
    return null;
  }

  @OneToMany(targetEntity = BanRecord.class)
  public List<BanRecord> getBans() {
    return this.bans;
  }

  @OneToMany(targetEntity = BanRecord.class)
  public List<BanRecord> getCreatedBans() {
    return this.createdBans;
  }

  public int getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public boolean isBanned() {
    for (final BanRecord ban : this.bans) {
      if (ban.getState() == BanRecord.State.NORMAL) {
        return true;
      }
    }
    return false;
  }

  public void setBans(final List<BanRecord> records) {
    this.bans = records;
  }

  public void setCreatedBans(final List<BanRecord> records) {
    this.createdBans = records;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public void setName(final String name) {
    this.name = name;
  }

}
