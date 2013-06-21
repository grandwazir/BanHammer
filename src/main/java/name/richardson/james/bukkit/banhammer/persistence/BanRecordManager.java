package name.richardson.james.bukkit.banhammer.persistence;

import java.util.List;

import com.avaje.ebean.EbeanServer;

public class BanRecordManager {

	private EbeanServer database;

	public BanRecordManager(EbeanServer database) {
		this.database = database;
	}

	public void delete(BanRecord ban) {
		this.database.delete(ban);
	}

	public int delete(List<BanRecord> bans) {
		return this.database.delete(bans);
	}

	public void save(BanRecord record) {
		this.database.save(record);
	}

	public void update(BanRecord record) {
		this.database.update(record);
	}

	public List<BanRecord> list() {
		return this.database.find(BanRecord.class).findList();
	}

	public List<BanRecord> list(int limit) {
		return this.database.find(BanRecord.class).setMaxRows(limit).findList();
	}

	public int count() {
		return this.database.find(BanRecord.class).findRowCount();
	}
}
