package name.richardson.james.bukkit.banhammer.api;

import org.bukkit.event.Event;

import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.banhammer.BanRecord.Type;

public class BanHammerPlayerBannedEvent extends Event {

  private static final long serialVersionUID = -4966452608656819339L;
  
  private final String playerName;

  private final BanRecord record;

  public BanHammerPlayerBannedEvent(BanRecord record) {
    this.record = record;
    this.playerName = record.getPlayer().getPlayerName();
  }

  public String getPlayerName() {
    return playerName;
  }
  
  public String getReason() {
    return record.getReason();
  }
  
  public long getExpiresAt() {
    return record.getExpiresAt();
  }
  
  public BanRecord.Type getBanType() {
    return record.getType();
  }
  
  public BanRecord getRecord() {
    return record;
  }
  
}
