package name.richardson.james.bukkit.banhammer.record;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PlayerRecord {

	public enum PlayerStatus {
		ANY,
		BANNED,
		CREATOR
	}

	public BanRecord getActiveBan();

	public List<BanRecord> getBans();

	public List<BanRecord> getCreatedBans();

	public String getLastKnownName();

	public UUID getUuid();

	public boolean isBanned();

	public void setBans(Set<BanRecord> bans);

	public void setCreatedBans(Set<BanRecord> createdBans);

	public void setLastKnownName(String lastKnownName);

	public void setUuid(UUID uuid);

}
