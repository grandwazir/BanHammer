package name.richardson.james.banhammer;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "bh_bans")

public class BanHammerRecord {

	private static BanHammerPlugin plugin;
	
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

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getPlayer() {
		return player;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public void setExpiresAt(long expiresAt) {
		this.expiresAt = expiresAt;
	}

	public long getExpiresAt() {
		return expiresAt;
	}
	
	static public void setPlugin(BanHammerPlugin plugin) {
		BanHammerRecord.plugin = plugin;
	}
	
	static public void find(String player) {
		return;
	}
	
}
