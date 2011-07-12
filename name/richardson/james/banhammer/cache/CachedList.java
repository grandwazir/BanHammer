package name.richardson.james.banhammer.cache;

import java.util.HashMap;
import java.util.logging.Level;

import name.richardson.james.banhammer.persistant.BanRecord;

public class CachedList {
	private HashMap<String, BanRecord.type> list;
	private HashMap<String, String> permenant;
	private HashMap<String, Long> temporary;
	
	public CachedList() {
		this.load();
	}
	
	public void add(String playerName) {
		BanRecord ban = BanRecord.findFirst(playerName);
		list.put(playerName, ban.getType());
		if (ban.getType().equals(BanRecord.type.PERMENANT)) {
			permenant.put(playerName, ban.getReason());
		} else if (ban.getType().equals(BanRecord.type.TEMPORARY)) {
			temporary.put(playerName, ban.getExpiresAt());
		}
	}
	
	public boolean contains(String playerName) {
		if (list.containsKey(playerName)) return true;
		return false;
	}
	
	public CachedBan get(String playerName) {
		BanRecord.type type = list.get(playerName);
		if (type.equals(BanRecord.type.PERMENANT)) {
			if (!permenant.containsKey(playerName)) add(playerName);
			return new CachedBan(0, playerName, permenant.get(playerName));
	  } else {
	  	if (!temporary.containsKey(playerName)) add(playerName);
	  	return new CachedBan(temporary.get(playerName), playerName, null);
		}
	}
	
	public void load () {
		for (BanRecord ban : BanRecord.list()) {
				list.put(ban.getPlayer().toLowerCase(), ban.getType());
		}
	}
	
	public void reload() {
		this.unload();
		this.load();
	}
	
	public void remove(String playerName) {
		list.remove(playerName);
		permenant.remove(playerName);
		temporary.remove(playerName);
	}
	
	public void unload() {
		permenant.clear();
		temporary.clear();
	}
	
}
