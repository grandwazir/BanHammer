package name.richardson.james.bukkit.banhammer.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.banhammer.PlayerRecord;

@Entity
@Table(name="banhammer_bans")
public final class SQLBanRecord implements BanRecord {

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private int id;
  
  @NotNull
  private int playerId;
  
  @NotNull
  private int creatorId;
  
  @NotNull
  private long createdAt;
  
  @NotNull
  private String reason;
  
  @NotNull
  private long expiresAt;
  
  @NotNull
  private State state;
  
  @ManyToOne(optional=false)
  @JoinColumn(name="player_id")
  private PlayerRecord player;
  
  @ManyToOne(optional=false)
  @JoinColumn(name="creator_id")
  private PlayerRecord creator;
  
  public long getCreatedAt(long time) {
    return createdAt;
  }

  public PlayerRecord getCreater() {
    return creator;
  }

  public int getCreatorId() {
    return creatorId;
  }

  public long getExpiresAt() {
    return expiresAt;
  }

  public int getId() {
    return id;
  }

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
    return (this.expiresAt <= System.currentTimeMillis()) ? State.EXPIRED : state;
  }

  public Type getType() {
    return (this.expiresAt == 0) ? Type.PERMENANT : Type.TEMPORARY;
  }

  public void setCreatedAt(long time) {
    this.createdAt = time;
  }

  public void setCreator(PlayerRecord creator) {
    this.creator = creator;
  }

  public void setCreatorId(int id) {
    this.creatorId = id;
  }

  public void setExpiresAt(long expiresAt) {
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
