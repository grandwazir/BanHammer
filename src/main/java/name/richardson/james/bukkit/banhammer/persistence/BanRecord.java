package name.richardson.james.bukkit.banhammer.persistence;

import name.richardson.james.bukkit.banhammer.PlayerRecord;

public interface BanRecord {

  public enum State {
    NORMAL,
    PARDONED,
    EXPIRED
  }
  
  public enum Type {
    TEMPORARY,
    PERMENANT
  }
  
  public PlayerRecord getPlayer();
  
  public void setPlayer(PlayerRecord player);
  
  public int getPlayerId();
  
  public void setPlayerId(int playerId);
  
  public void setId(int id);
  
  public int getId();
  
  public void setCreatedAt(long time);
  
  public long getCreatedAt(long time);
  
  public PlayerRecord getCreater();
  
  public void setCreator(PlayerRecord creator);
  
  public void setCreatorId(int creatorId);
  
  public int getCreatorId();
  
  public void setReason(String reason);
  
  public String getReason();
  
  public State getState();
  
  public void setState(State state);
  
  public Type getType();
  
  public void setExpiresAt(long expiresAt);
  
  public long getExpiresAt();
  
}
