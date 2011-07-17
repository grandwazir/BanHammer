package name.richardson.james.banhammer.persistant;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import name.richardson.james.banhammer.BanHammer;

import com.avaje.ebean.ExampleExpression;
import com.avaje.ebean.LikeType;
import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "bh_bans")
public class BanRecord {
	public enum type {
		PERMENANT, TEMPORARY
	}

	static public void create(String playerName, String senderName, Long Expiry, Long creationTime, String banReason) {
		BanRecord banHammerRecord = new BanRecord();
		banHammerRecord.player = playerName;
		banHammerRecord.createdBy = senderName;
		banHammerRecord.createdAt = creationTime;
		banHammerRecord.expiresAt = Expiry;
		banHammerRecord.reason = banReason;
		BanHammer.getDb().save(banHammerRecord);
	}

	static public void destroy(List<BanRecord> banHammerRecords) {
		for (BanRecord ban : banHammerRecords) {
			BanHammer.getDb().delete(ban);
		}
	}

	static public List<BanRecord> find(String player) {
		// create the example
		BanRecord example = new BanRecord();
		example.setPlayer(player);
		// create the example expression
		ExampleExpression expression = BanHammer.getDb().getExpressionFactory().exampleLike(
				example, true, LikeType.EQUAL_TO);
		// find and return all bans that match the expression
		return BanHammer.getDb().find(BanRecord.class).where().add(expression).orderBy("created_at DESC").findList();
	}
	
	static public BanRecord findFirst(String player) {
		// create the example
		BanRecord example = new BanRecord();
		example.setPlayer(player);
		// create the example expression
		ExampleExpression expression = BanHammer.getDb().getExpressionFactory().exampleLike(
				example, true, LikeType.EQUAL_TO);
		// find and return all bans that match the expression
		return BanHammer.getDb().find(BanRecord.class).where().add(expression).orderBy("created_at DESC").findList().get(0);
	}
	
	static public List<BanRecord> findRecent(Integer maxRows) {
	  return BanHammer.getDb().find(BanRecord.class).where().orderBy("created_at DESC").setMaxRows(maxRows).findList();
	}

	static public List<BanRecord> list() {
		return BanHammer.getDb().find(BanRecord.class).findList();
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
		BanHammer.getDb().delete(this);
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
	
	
	public boolean isActive() {
		if (expiresAt == 0) {
			return true;
		} else if (expiresAt > System.currentTimeMillis()) {
			return true;
		} else {
		  return false;
		}
	}

}
