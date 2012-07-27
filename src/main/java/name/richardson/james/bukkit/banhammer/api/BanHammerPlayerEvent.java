package name.richardson.james.bukkit.banhammer.api;

import org.bukkit.event.Event;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord;

public abstract class BanHammerPlayerEvent extends Event {

  private static final long serialVersionUID = 1L;

  private final String playerName;

  private final BanRecord record;

  private final boolean silent;

  public BanHammerPlayerEvent(final BanRecord record, final boolean silent) {
    this.record = record;
    this.playerName = record.getPlayer().getName();
    this.silent = silent;
  }

  public String getPlayerName() {
    return this.playerName;
  }

  public BanRecord getRecord() {
    return this.record;
  }

  public boolean isSilent() {
    return this.silent;
  }

}
