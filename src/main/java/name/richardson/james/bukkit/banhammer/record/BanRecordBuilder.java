package name.richardson.james.bukkit.banhammer.record;

import java.sql.Timestamp;
import java.util.UUID;

import com.avaje.ebean.EbeanServer;

public final class BanRecordBuilder {

	private final EbeanServer database;
	private final CurrentBanRecord record = new CurrentBanRecord();
	private long now = System.currentTimeMillis();

	public BanRecordBuilder(EbeanServer database, String playerName, UUID creatorUUID, String reason) {
		this.database = database;
		record.setReason(reason);
		Timestamp now = new Timestamp(this.now);
		record.setCreatedAt(now);
		this.setPlayer(playerName);
		this.setCreator(creatorUUID);
	}

	public BanRecordBuilder(EbeanServer database, UUID playerUUID, UUID creatorUUID, String reason) {
		this.database = database;
		record.setReason(reason);
		Timestamp now = new Timestamp(this.now);
		record.setCreatedAt(now);
		this.setPlayer(playerUUID);
		this.setCreator(creatorUUID);
	}

	public void save() {
		this.database.save(record);
	}

	public void setCreator(UUID creatorUUID) {
		final PlayerRecord record = CurrentPlayerRecord.findOrCreate(database, creatorUUID);
		this.record.setCreator(record);
	}

	public void setExpiresAt(Timestamp timestamp) {
		now = System.currentTimeMillis();
		this.record.setCreatedAt(new Timestamp(now));
		this.record.setExpiresAt(timestamp);
	}

	public void setExpiryTime(long time) {
		this.now = System.currentTimeMillis();
		this.record.setCreatedAt(new Timestamp(now));
		if (time != 0) this.record.setExpiresAt(new Timestamp(now + time));
	}

	public void setPlayer(UUID playerUUID) {
		PlayerRecord record = CurrentPlayerRecord.findOrCreate(database, playerUUID);
		this.record.setPlayer(record);
	}

	public void setPlayer(String playerName) {
		PlayerRecord record = CurrentPlayerRecord.findOrCreate(database, playerName);
		record.setLastKnownName(playerName);
		this.record.setPlayer(record);
	}

	public CurrentBanRecord getRecord() {
		return record;
	}

}
