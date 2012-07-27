package name.richardson.james.bukkit.banhammer.api;

import org.bukkit.event.Event;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord;

public class BanHammerPlayerBannedEvent extends Event {

  private static final long serialVersionUID = -4966452608656819339L;
  
  private final String playerName;

  private final BanRecord record;

  private boolean silent;

  public BanHammerPlayerBannedEvent(BanRecord record, boolean silent) {
    this.record = record;
    this.playerName = record.getPlayer().getName();
    this.silent = silent;
  }

  public String getPlayerName() {
    return playerName;
  }
  
  public BanRecord getRecord() {
    return record;
  }
  
  public boolean isSilent() {
    return silent;
  }
  
}
