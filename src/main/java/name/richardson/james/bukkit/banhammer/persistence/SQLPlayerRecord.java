package name.richardson.james.bukkit.banhammer.persistence;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord.State;

@Entity
@Table(name="banhammer_players")
public class SQLPlayerRecord implements PlayerRecord {

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private long id;
  
  @NotNull
  private String name;

  @OneToMany(mappedBy="player", targetEntity=SQLBanRecord.class)
  private List<BanRecord> bans;

  @OneToMany(mappedBy="creator", targetEntity=SQLBanRecord.class)
  private List<BanRecord> createdBans;
  
  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<BanRecord> getBans() {
    return this.bans;
  }

  public void setBans(List<BanRecord> records) {
    this.bans = records;
  }

  public List<BanRecord> getCreatedBans() {
    return createdBans;
  }

  public void setCreatedBans(List<BanRecord> records) {
    this.createdBans = records;
  }

  public boolean isBanned() {
    for (BanRecord ban : bans) {
      if (ban.getState() == State.NORMAL) return true;
    }
    return false;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

}
