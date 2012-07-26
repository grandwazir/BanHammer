package name.richardson.james.bukkit.banhammer;

import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.avaje.ebean.validation.NotNull;

public class BanRecord {

  /* The state of this ban */
  public enum State {
    ACTIVE,
    EXPIRED,
    PARDONED
  }
  
  /* The type of ban */
  public enum Type {
    TEMPORARY,
    PERMENANT
  }
  
  
  @Id
  private int id;

  @NotNull
  private long createdAt;

  private PlayerRecord createdBy;

  @NotNull
  private long expiresAt;
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="player_id")
  private PlayerRecord player;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="creator_id")
  private PlayerRecord creator;
  
  @NotNull
  private int creatorId;
  
  @NotNull
  private int playerId;

  @NotNull
  private String reason;
  
  @NotNull
  private State state;
  
  @NotNull
  private Type type;

  public PlayerRecord getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(PlayerRecord createdBy) {
    this.createdBy = createdBy;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public long getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(long expiresAt) {
    this.expiresAt = expiresAt;
  }

  public PlayerRecord getPlayer() {
    return player;
  }

  public void setPlayer(PlayerRecord player) {
    this.player = player;
  }

  public int getPlayerId() {
    return playerId;
  }

  public void setPlayerId(int playerId) {
    this.playerId = playerId;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public void setCreator(PlayerRecord playerRecord) {
    // TODO Auto-generated method stub
    
  }

}
