package name.richardson.james.bukkit.banhammer.persistence;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.utilities.persistence.SQLStorage;

@Entity
@Table(name="banhammer_players")
public class PlayerRecord {

  public static PlayerRecord find(SQLStorage storage, String playerName) {
    PlayerRecord record = storage.getEbeanServer().find(PlayerRecord.class).where().ieq("name", playerName).findUnique();
    if (record != null) {
      record = new PlayerRecord();
      record.setName(playerName);
      storage.getEbeanServer().save(record);
    }
    return record;
  }
  
  public static boolean exists(SQLStorage storage, String playerName) {
    PlayerRecord record = storage.getEbeanServer().find(PlayerRecord.class).where().ieq("name", playerName).findUnique();
    return (record != null);
  }
 
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private long id;
  
  @NotNull
  private String name;

  @OneToMany(mappedBy="player", targetEntity=BanRecord.class)
  private List<BanRecord> bans;

  @OneToMany(mappedBy="creator", targetEntity=BanRecord.class)
  private List<BanRecord> createdBans;
  
  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @OneToMany(targetEntity=BanRecord.class)
  public List<BanRecord> getBans() {
    return this.bans;
  }

  public void setBans(List<BanRecord> records) {
    this.bans = records;
  }

  @OneToMany(targetEntity=BanRecord.class)
  public List<BanRecord> getCreatedBans() {
    return createdBans;
  }

  public void setCreatedBans(List<BanRecord> records) {
    this.createdBans = records;
  }

  public boolean isBanned() {
    for (BanRecord ban : bans) {
      if (ban.getState() == BanRecord.State.NORMAL) return true;
    }
    return false;
  }
  
  public BanRecord getActiveBan() {
    for (BanRecord ban : bans) {
      if (ban.getState() == BanRecord.State.NORMAL) return ban;
    }
    return null;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

}
