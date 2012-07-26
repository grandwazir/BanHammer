package name.richardson.james.bukkit.banhammer.api;

import org.bukkit.event.Event;

import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.banhammer.BanRecord.Type;

public class BanHammerPlayerPardonedEvent extends Event {

  private static final long serialVersionUID = -8229648653550290621L;
  
  private final BanRecord.Type banType;
  
  private final String playerName;

  public BanHammerPlayerPardonedEvent(String playerName, BanRecord.Type type) {
    this.playerName = playerName;
    this.banType = type;
  }

  public String getPlayerName() {
    return playerName;
  }

  public BanRecord.Type getBanType() {
    return banType;
  }

}
