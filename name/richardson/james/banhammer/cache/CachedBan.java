package name.richardson.james.banhammer.cache;

import name.richardson.james.banhammer.persistant.BanRecord;

public class CachedBan {

	private long expiresAt;
	private String player;
	private String reason;
	
	public CachedBan (long expiresAt, String player, String reason) {
		this.expiresAt = expiresAt;
		this.player = player;
		this.reason = reason;
	}
	
	public long getExpiresAt() {
		return this.expiresAt;
	}

	public String getPlayer() {
		return this.player;
	}

	public String getReason() {
		return this.reason;
	}
	
	public BanRecord.type getType() {
		if (expiresAt == 0) {
			return BanRecord.type.PERMENANT;
		} else {
			return BanRecord.type.TEMPORARY;
		}
	}
	
	public boolean isActive() {
		if (expiresAt == 0) {
			return true;
		} else if (expiresAt < System.currentTimeMillis()) {
			return true;
		} else {
		  return false;
		}
	}
	
	
	public void setExpiresAt(long expiresAt) {
		this.expiresAt = expiresAt;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
