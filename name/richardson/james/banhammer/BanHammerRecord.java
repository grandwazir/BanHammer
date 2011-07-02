package name.richardson.james.banhammer;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.ExampleExpression;
import com.avaje.ebean.LikeType;
import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "bh_bans")
public class BanHammerRecord {
	private static EbeanServer database;
	// private static Server server;

	static public void create(String playerName, String senderName, Long Expiry, String banReason) {
		BanHammerRecord ban = new BanHammerRecord();
		ban.player = playerName;
		ban.createdBy = senderName;
		ban.createdAt = System.currentTimeMillis();
		ban.expiresAt = Expiry;
		ban.reason = banReason;
		database.save(ban);
		BanHammerPlugin.log.info(String.format("[BanHammer] %s was banned by %s", playerName, senderName));
	}
	
	static public void remove(List<BanHammerRecord> bans, String senderName) {
		for (BanHammerRecord ban : bans)
			database.delete(ban);
		BanHammerPlugin.log.info(String.format("[BanHammer] %s ban(s) were deleted by %s", Integer.toString(bans.size()), senderName));
	}
	
	static public List<BanHammerRecord> find(String player) {
		// create the example
		BanHammerRecord example = new BanHammerRecord();
		example.setPlayer(player);
		// create the example expression
		ExampleExpression expression = database.getExpressionFactory().exampleLike(example, true, LikeType.EQUAL_TO);
		// find and return all bans that match the expression
		return database.find(BanHammerRecord.class).where().add(expression).findList();
	}
	
	static public List<BanHammerRecord> findPermenantBans() {
		// find and return all bans that have an expiry time of 0
		return database.find(BanHammerRecord.class).where().eq("expiresAt", 0).findList();
	}
	
	static public List<BanHammerRecord> findTemporaryBans() {
		// find and return all bans that are temporary (time_now > expiresAt)
		return database.find(BanHammerRecord.class).where().between("expiresAt", System.currentTimeMillis(), "9999999999999").findList();
	}

	static public void setup(BanHammerPlugin plugin) {
		BanHammerRecord.database = plugin.getDatabase();
		// BanHammerRecord.server = plugin.getServer();
		// add permanent bans to memory
	}

	static public boolean isBanned(String player) {
		final List<BanHammerRecord> bans = BanHammerRecord.find(player);
		// check to see if the player is banned
		for (BanHammerRecord ban : bans) {
			if (ban.expiresAt == 0) return true;
			if (ban.expiresAt > System.currentTimeMillis()) return true;
		}
		return false;
	}
	
	@Id
	private long createdAt;

	@NotNull
	private String player;

	@NotNull
	private String createdBy;

	@NotNull
	private String reason;

	@NotNull
	private long expiresAt;

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
