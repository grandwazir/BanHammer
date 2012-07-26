package name.richardson.james.bukkit.banhammer;

public class CachedBan {

  private final String playerName;
  private final String reason;
  private final long expiresAt;
  private final int id;

  public CachedBan(BanRecord record) {
    this.id = record.getId();
    this.playerName = record.getPlayer().getPlayerName();
    this.reason = record.getReason();
    this.expiresAt = record.getExpiresAt();
  }

  public String getPlayerName() {
    return playerName;
  }

  public String getReason() {
    return reason;
  }

  public long getExpiresAt() {
    return expiresAt;
  }
  
  public boolean isActive() {
    return (this.expiresAt > System.currentTimeMillis()) ? true : false;
  }

  public int getId() {
    return id;
  }
  
  public BanRecord.Type getType() {
    return (this.expiresAt == 0) ? BanRecord.Type.PERMENANT : BanRecord.Type.TEMPORARY;
  }
  
  
}
