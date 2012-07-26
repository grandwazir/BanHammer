package name.richardson.james.bukkit.banhammer;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.alias.PlayerNameRecord;
import name.richardson.james.bukkit.banhammer.BanRecord.State;
import name.richardson.james.bukkit.utilities.internals.Logger;
import name.richardson.james.bukkit.utilities.persistence.SQLStorage;

@Entity
@Table(name = "banhammer_players")
public class PlayerRecord {

  private static final Logger logger = new Logger(PlayerRecord.class);

  public static PlayerRecord findByName(final SQLStorage database, final String playerName) {
    logger.debug(String.format("Attempting to return PlayerNameRecord matching the name %s.", playerName));
    final PlayerRecord record = database.getEbeanServer().find(PlayerRecord.class).where().ieq("playerName", playerName).findUnique();
    return record;
  }
  
  public static boolean exists(final SQLStorage database, final String playerName) {
    final PlayerRecord record = database.getEbeanServer().find(PlayerRecord.class).where().ieq("playerName", playerName).findUnique();
    if (record != null) {
      return true;
    } else {
      return false;
    }
  }
  
  /** The primary key for this record. */
  @Id
  private int id;

  /** The player's name. */
  @NotNull
  private String playerName;

  /** The bans associated with this player. */
  private List<BanRecord> bans;

  /** The bans created by this player */
  private List<BanRecord> createdBans;

  /**
   * Gets a list of all bans associated with this player.
   *
   * @return the bans
   */
  @OneToMany(mappedBy="player")
  public List<BanRecord> getBans() {
    return this.bans;
  }
  
  @OneToMany(mappedBy="creator")
  public List<BanRecord> getCreatedBans() {
    return this.createdBans;
  }

  /**
   * Gets the primary key for this record.
   *
   * @return the primary key
   */
  public int getId() {
    return id;
  }

  /**
   * Gets the player's name.
   *
   * @return the player's name
   */
  public String getPlayerName() {
    return playerName;
  }

  /**
   * Checks if this player is currently banned.
   *
   * @return true, if is banned
   */
  public boolean isBanned() {
    for (BanRecord ban : this.bans) {
      if (ban.getState() == State.ACTIVE) return true;
    }
    return false;
  }
  
  /**
   * Associate bans with this player.
   *
   * @param bans the bans to associate with this player.
   */
  public void setBans(List<BanRecord> bans) {
    this.bans = bans;
  }
  
  /**
   * Sets the primary key.
   *
   * @param id the new primary key.
   */
  public void setId(int id) {
    this.id = id;
  }
  
  /**
   * Sets the player's name.
   *
   * @param playerName the new player's name
   */
  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }
  
}
