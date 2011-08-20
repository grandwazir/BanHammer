
package name.richardson.james.banhammer.cache;

import java.util.HashMap;

import name.richardson.james.banhammer.persistant.BanRecord;

public class CachedList {

  private HashMap<String, BanRecord.type> list = new HashMap<String, BanRecord.type>();
  private HashMap<String, String> permenant = new HashMap<String, String>();
  private HashMap<String, Long> temporary = new HashMap<String, Long>();

  public CachedList() {
    this.load();
  }

  public void add(String playerName) {
    playerName = playerName.toLowerCase();
    BanRecord ban = BanRecord.findFirst(playerName);
    this.list.put(playerName, ban.getType());
    if (ban.getType().equals(BanRecord.type.PERMENANT))
      this.permenant.put(playerName, ban.getReason());
    else if (ban.getType().equals(BanRecord.type.TEMPORARY))
      this.temporary.put(playerName, ban.getExpiresAt());
  }

  public boolean contains(String playerName) {
    playerName = playerName.toLowerCase();
    if (this.list.containsKey(playerName))
      return true;
    else return false;
  }

  public CachedBan get(String playerName) {
    playerName = playerName.toLowerCase();
    BanRecord.type type = this.list.get(playerName);
    if (type.equals(BanRecord.type.PERMENANT)) {
      if (!this.permenant.containsKey(playerName))
        this.add(playerName);
      return new CachedBan(0, playerName, this.permenant.get(playerName));
    } else {
      if (!this.temporary.containsKey(playerName))
        this.add(playerName);
      return new CachedBan(this.temporary.get(playerName), playerName, null);
    }
  }

  public void load() {
    for (BanRecord ban : BanRecord.list())
      if (ban.isActive())
        this.list.put(ban.getPlayer().toLowerCase(), ban.getType());
  }

  public void reload() {
    this.unload();
    this.load();
  }

  public void remove(String playerName) {
    playerName = playerName.toLowerCase();
    this.list.remove(playerName);
    this.permenant.remove(playerName);
    this.temporary.remove(playerName);
  }

  public int size() {
    return this.list.size();
  }

  public HashMap<String, Integer> stats() {
    HashMap<String, Integer> stats = new HashMap<String, Integer>();
    stats.put("permenant", this.permenant.size());
    stats.put("temporary", this.temporary.size());
    return stats;
  }

  public void unload() {
    this.permenant.clear();
    this.temporary.clear();
  }

}
