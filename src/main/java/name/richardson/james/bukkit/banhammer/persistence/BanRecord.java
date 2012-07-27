package name.richardson.james.bukkit.banhammer.persistence;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
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

import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.utilities.persistence.SQLStorage;

@Entity
@Table(name="banhammer_bans")
public class BanRecord {
  
  public static List<BanRecord> getRecentBans(SQLStorage storage, int count) {
    return storage.getEbeanServer().find(BanRecord.class).setMaxRows(count).orderBy().desc("createdAt").findList();
  }

  public enum State {
    NORMAL,
    EXPIRED,
    PARDONED
  }
  
  public enum Type {
    PERMENANT,
    TEMPORARY
  }
  
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
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
  
  @ManyToOne(targetEntity=PlayerRecord.class)
  @JoinColumn(name="player_id")
  private PlayerRecord player;
  
  @ManyToOne(optional=false)
  @JoinColumn(name="creator_id")
  private PlayerRecord creator;
  
  public Timestamp getCreatedAt() {
    return this.createdAt;
  }

  public Timestamp getCreatedAt(long time) {
    return createdAt;
  }

  @ManyToOne(targetEntity=PlayerRecord.class)
  public PlayerRecord getCreater() {
    return creator;
  }

  public int getCreatorId() {
    return creatorId;
  }

  public Timestamp getExpiresAt() {
    return expiresAt;
  }

  public int getId() {
    return id;
  }

  @ManyToOne(targetEntity=PlayerRecord.class)
  public PlayerRecord getPlayer() {
    return player;
  }

  public int getPlayerId() {
    return playerId;
  }
  
  public String getReason() {
    return reason;
  }

  public State getState() {
    final Timestamp now = new Timestamp(System.currentTimeMillis());
    return (now.after(this.expiresAt)) ? State.EXPIRED : state;
  }

  public BanRecord.Type getType() {
    return (this.expiresAt == null) ? BanRecord.Type.PERMENANT : BanRecord.Type.TEMPORARY;
  }

  public void setCreatedAt(Timestamp time) {
    this.createdAt = time;
  }

  public void setCreator(PlayerRecord creator) {
    this.creator = creator;
  }

  public void setCreatorId(int id) {
    this.creatorId = id;
  }
  
  public void setExpiresAt(Timestamp expiresAt) {
    this.expiresAt = expiresAt; 
  }
  

  public void setId(int id) {
    this.id = id;
  }

  public void setPlayer(PlayerRecord player) {
    this.player = player;
  }

  public void setPlayerId(int playerId) {
    this.playerId = playerId;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public void setState(State state) {
    this.state = state;
  }
  
}
