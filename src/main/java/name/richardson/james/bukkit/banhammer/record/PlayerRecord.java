package name.richardson.james.bukkit.banhammer.record;

import java.util.List;
import java.util.UUID;

public interface PlayerRecord {

	public enum PlayerStatus {
		ANY,
		BANNED,
		CREATOR
	}

	public CurrentBanRecord getActiveBan();

	public List<CurrentBanRecord> getBans();

	public List<CurrentBanRecord> getCreatedBans();

	public String getLastKnownName();

	public UUID getUuid();

	public boolean isBanned();

	public void setBans(List<CurrentBanRecord> bans);

	public void setCreatedBans(List<CurrentBanRecord> createdBans);

	public void setLastKnownName(String lastKnownName);

	public void setUuid(UUID uuid);

}
