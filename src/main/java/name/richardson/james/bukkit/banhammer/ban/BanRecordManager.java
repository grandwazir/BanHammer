package name.richardson.james.bukkit.banhammer.ban;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.avaje.ebean.EbeanServer;

public class BanRecordManager {

	private EbeanServer database;

	public BanRecordManager(EbeanServer database) {
		if (database == null) throw new IllegalArgumentException();
		this.database = database;
	}

	public void delete(OldBanRecord ban) {
		this.delete(Arrays.asList(ban));
	}

	public int delete(Collection<OldBanRecord> bans) {
		return this.database.delete(bans);
	}

	public boolean save(OldBanRecord record) {
		if (record.getPlayer().isBanned()) return false;
		this.database.save(record);
		return true;
	}

	public List<OldBanRecord> list() {
		return this.database.find(OldBanRecord.class).findList();
	}

	public List<OldBanRecord> list(int limit) {
		return this.database.find(OldBanRecord.class).setMaxRows(limit).orderBy().desc("createdAt").findList();
	}

	public int count() {
		return this.database.find(OldBanRecord.class).findRowCount();
	}
}
