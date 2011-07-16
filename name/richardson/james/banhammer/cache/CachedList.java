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
		list.put(playerName, ban.getType());
		if (ban.getType().equals(BanRecord.type.PERMENANT)) {
			permenant.put(playerName, ban.getReason());
		} else if (ban.getType().equals(BanRecord.type.TEMPORARY)) {
			temporary.put(playerName, ban.getExpiresAt());
		}
	}
	
	public boolean contains(String playerName) {
		playerName = playerName.toLowerCase();
		if (list.containsKey(playerName)) {
			return true;
		} else {
			return false;
		}
	}
	
	public CachedBan get(String playerName) {
		playerName = playerName.toLowerCase();
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
				if(ban.isActive()) {
					list.put(ban.getPlayer().toLowerCase(), ban.getType());
				}
		}
	}
	
	public void reload() {
		this.unload();
		this.load();
	}
	
	public void remove(String playerName) {
		playerName = playerName.toLowerCase();
		list.remove(playerName);
		permenant.remove(playerName);
		temporary.remove(playerName);
	}
	
	public int size() {
		return list.size();
	}
	
	public HashMap<String, Integer> stats() {
		HashMap<String, Integer> stats = new HashMap<String, Integer>();
		stats.put("permenant", permenant.size());
		stats.put("temporary", temporary.size());
		return stats;
	}
	
	public void unload() {
		permenant.clear();
		temporary.clear();
	}
	
}
