package name.richardson.james.bukkit.banhammer.ban;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.UUID;

public interface PlayerRecord {

	public BanRecord getActiveBan();

	@OneToMany(targetEntity = OldBanRecord.class, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	public List<BanRecord> getBans();

	@OneToMany(targetEntity = OldBanRecord.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	public List<BanRecord> getCreatedBans();

	public int getId();

	public String getName();

	public boolean isBanned();

	public void setBans(List<BanRecord> records);

	public void setCreatedBans(List<BanRecord> records);

	public void setId(int id);

	public UUID getUUID();

	public void setUUID(UUID uuid);

}
