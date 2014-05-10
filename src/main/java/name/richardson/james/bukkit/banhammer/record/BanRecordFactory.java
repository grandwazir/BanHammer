package name.richardson.james.bukkit.banhammer.record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.avaje.ebean.EbeanServer;

public final class BanRecordFactory {

	private BanRecordFactory() {}

	public static int count(EbeanServer database) {
		return database.find(CurrentBanRecord.class).findRowCount();
	}

	public static BanRecord create(PlayerRecord player, PlayerRecord creator, String reason) {
		return new CurrentBanRecord(player, creator, reason);
	}

	/**
	 * Find all BanRecords that match a specific state
	 *
	 * @param database the database to use.
	 * @param state the state to match.
	 * @return the BanRecords that match.
	 */
	public static Collection<BanRecord> find(EbeanServer database, BanRecord.State state) {
		List<BanRecord> records = new ArrayList<BanRecord>();
		records.addAll(database.find(CurrentBanRecord.class).where().eq("state", state.ordinal()).findList());
		return records;
	}

	public static List<BanRecord> list(EbeanServer database) {
		List<BanRecord> records = new ArrayList<BanRecord>();
		records.addAll(database.find(CurrentBanRecord.class).findList());
		return records;
	}

	public static List<BanRecord> list(EbeanServer database, int count) {
		List<BanRecord> records = new ArrayList<BanRecord>();
		records.addAll(database.find(CurrentBanRecord.class).setMaxRows(count).findList());
		return records;
	}
}
