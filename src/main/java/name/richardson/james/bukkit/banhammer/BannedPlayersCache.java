package name.richardson.james.bukkit.banhammer;

import java.util.HashMap;
import java.util.LinkedHashMap;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.persistence.SQLStorage;

public class BannedPlayersCache {

  private final HashMap<String, BanRecord> cache = new LinkedHashMap<String, BanRecord>();
  
  private final SQLStorage storage;

  public BannedPlayersCache(SQLStorage storage) {
    this.storage = storage;
  }
  
  public void reload() {
    for (final Object record : storage.list(PlayerRecord.class)) {
      final PlayerRecord player = (PlayerRecord) record;
      if (player.isBanned()) cache.put(player.getName(), null);
    }
  }
  
  public int size() {
    return cache.size();
  }
  
  public BanRecord get(String playerName) {
    if (cache.get(playerName) == null) {
      this.set(playerName);
    }
    return cache.get(playerName);
  }
  
  public boolean contains(String playerName) {
    this.checkBanIsValid(playerName);
    return cache.containsKey(playerName);
  }
  
  public void set(String playerName) {
    cache.put(playerName, PlayerRecord.find(storage, playerName).getActiveBan());
  }
  
  public void set(String playerName, BanRecord ban) {
    cache.put(playerName, ban);
  }
  
  public void remove(String playerName) {
    cache.remove(playerName);
  }
  
  private void checkBanIsValid(String playerName) {
    if (cache.get(playerName).getState() != BanRecord.State.NORMAL) {
      cache.remove(playerName);
    }
  }
  
}
