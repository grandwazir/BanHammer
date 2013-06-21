package name.richardson.james.bukkit.banhammer.persistence;

import com.avaje.ebean.EbeanServer;

public class BanRecordManager {

	private EbeanServer database;

	public BanRecordManager(EbeanServer database) {
		this.database = database;
	}

	public void save(BanRecord record) {
		this.database.save(record);
	}

	public void update(BanRecord record) {
		this.database.update(record);
	}

	public int count() {
		return this.database.find(BanRecord.class).findRowCount();
	}
}
