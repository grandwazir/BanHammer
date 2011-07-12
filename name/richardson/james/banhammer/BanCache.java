package name.richardson.james.banhammer;

import java.util.HashMap;
import java.util.logging.Level;

import name.richardson.james.banhammer.persistant.BanRecord;

public class BanCache {
	private HashMap<String, String> permenant;
	private HashMap<String, Long> temporary;
	
	public BanCache() {
		this.load();
	}
	
	public void load () {
		for (BanRecord ban : BanRecord.list()) {
			switch (ban.getType()) {
				case PERMENANT: permenant.put(ban.getPlayer(), null);
				case TEMPORARY: temporary.put(ban.getPlayer(), ban.getExpiresAt());
			}
		}
		BanHammer.log(Level.INFO, String.format(BanHammer.messages.getString("permenantBansLoaded"), Integer.toString(permenant.size())));
		BanHammer.log(Level.INFO, String.format(BanHammer.messages.getString("temporaryBansLoaded"), Integer.toString(temporary.size())));
	}
	
	public void reload() {
		this.unload();
		this.load();
	}
	
	public void unload() {
		permenant.clear();
		temporary.clear();
	}
	
}
