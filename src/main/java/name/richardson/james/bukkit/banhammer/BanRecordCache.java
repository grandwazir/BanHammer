package name.richardson.james.bukkit.banhammer;

import java.util.HashMap;
import java.util.LinkedHashMap;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.persistence.SQLStorage;

public class BanRecordCache {

  private final HashMap<String, BanRecord> cache = new LinkedHashMap<String, BanRecord>();

  private final SQLStorage storage;

  public BanRecordCache(final SQLStorage storage) {
    this.storage = storage;
  }

  public boolean contains(final String playerName) {
    this.checkBanIsValid(playerName);
    return this.cache.containsKey(playerName);
  }

  public BanRecord get(final String playerName) {
    if (this.cache.get(playerName) == null) {
      this.set(playerName);
    }
    return this.cache.get(playerName);
  }

  public void reload() {
    for (final Object record : this.storage.list(PlayerRecord.class)) {
      final PlayerRecord player = (PlayerRecord) record;
      if (player.isBanned()) {
        this.cache.put(player.getName(), null);
      }
    }
  }

  public void remove(final String playerName) {
    this.cache.remove(playerName);
  }

  public void set(final String playerName) {
    this.cache.put(playerName, PlayerRecord.find(this.storage, playerName).getActiveBan());
  }

  public void set(final String playerName, final BanRecord ban) {
    this.cache.put(playerName, ban);
  }

  public int size() {
    return this.cache.size();
  }

  private void checkBanIsValid(final String playerName) {
    if (this.cache.get(playerName).getState() != BanRecord.State.NORMAL) {
      this.cache.remove(playerName);
    }
  }

}
