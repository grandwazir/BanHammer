package name.richardson.james.bukkit.banhammer.api;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord;

public class BanHammerPlayerBannedEvent extends BanHammerPlayerEvent {

  private static final long serialVersionUID = 768545683909385614L;

  public BanHammerPlayerBannedEvent(final BanRecord record, final boolean silent) {
    super(record, silent);
  }

}
