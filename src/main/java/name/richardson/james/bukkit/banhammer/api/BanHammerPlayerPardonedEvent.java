package name.richardson.james.bukkit.banhammer.api;

import org.bukkit.event.Event;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord;

public class BanHammerPlayerPardonedEvent extends Event {

  private static final long serialVersionUID = -8229648653550290621L;
  
  private final String playerName;

  private final BanRecord record;

  private final boolean silent;

  public BanHammerPlayerPardonedEvent(BanRecord record, boolean silent) {
    this.playerName = record.getPlayer().getName();
    this.record = record;
    this.silent = silent;
  }

  public String getPlayerName() {
    return playerName;
  }


  public BanRecord getRecord() {
    return this.record;
  }
  
  public boolean isSilent() {
    return silent;
  }

}
