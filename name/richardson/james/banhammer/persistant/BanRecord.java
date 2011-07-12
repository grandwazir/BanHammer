package name.richardson.james.banhammer.persistant;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import name.richardson.james.banhammer.BanHammer;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.ExampleExpression;
import com.avaje.ebean.LikeType;
import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "bh_bans")
public class BanRecord {
	public enum type {
		PERMENANT, TEMPORARY
	}

	private static EbeanServer database = BanHammer.getDb();

	static public void create(String playerName, String senderName, Long Expiry,
			Long creationTime, String banReason) {
		BanRecord banHammerRecord = new BanRecord();
		banHammerRecord.player = playerName;
		banHammerRecord.createdBy = senderName;
		banHammerRecord.createdAt = creationTime;
		banHammerRecord.expiresAt = Expiry;
		banHammerRecord.reason = banReason;
		database.save(banHammerRecord);
		// BanHammer.log.info(String.format("[BanHammer] %s was banned by %s",
		// playerName, senderName));
	}

	static public void destroy(List<BanRecord> banHammerRecords) {
		for (BanRecord ban : banHammerRecords) {
			database.delete(ban);
		}
	}

	static public List<BanRecord> find(String player) {
		// create the example
		BanRecord example = new BanRecord();
		example.setPlayer(player);
		// create the example expression
		ExampleExpression expression = database.getExpressionFactory().exampleLike(
				example, true, LikeType.EQUAL_TO);
		// find and return all bans that match the expression
		return database.find(BanRecord.class).where().add(expression).findList();
	}

	static public boolean isBanned(String player) {
		final List<BanRecord> banHammerRecords = BanRecord.find(player);
		// check to see if the player is banned
		for (BanRecord banHammerRecord : banHammerRecords) {
			if (banHammerRecord.expiresAt == 0) return true;
			if (banHammerRecord.expiresAt > System.currentTimeMillis()) return true;
		}
		return false;
	}

	static public List<BanRecord> list() {
		return database.find(BanRecord.class).findList();
	}

	@Id
	private long createdAt;

	@NotNull
	private String createdBy;

	@NotNull
	private long expiresAt;

	@NotNull
	private String player;

	@NotNull
	private String reason;

	public void destroy() {
		database.delete(this);
	}

	public long getCreatedAt() {
		return this.createdAt;
	}

	public String getCreatedBy() {
		return this.createdBy;
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
		if (this.expiresAt == 0)
			return BanRecord.type.PERMENANT;
		else {
			return BanRecord.type.TEMPORARY;
		}
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
