package name.richardson.james.bukkit.banhammer.management;

import java.util.List;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord;

public class AuditSummary {

	private final List<BanRecord> bans;

	private int permenantBans;
	private int temporaryBans;
	private int normalBans;
	private int pardonedBans;
	private int expiredBans;
	private int total;

	public AuditSummary(List<BanRecord> bans, int total) {
		this.bans = bans;
		this.total = total;
		this.update();
	}

	public int getExpiredBanCount() {
		return expiredBans;
	}

	public float getExpiredBanCountPercentage() {
		return (float) expiredBans / this.bans.size();
	}

	public int getNormalBanCount() {
		return normalBans;
	}

	public float getNormalBanCountPercentage() {
		return (float) normalBans / this.bans.size();
	}

	public int getPardonedBanCount() {
		return pardonedBans;
	}

	public float getPardonedBanCountPercentage() {
		return (float) pardonedBans / this.bans.size();
	}

	public int getPermenantBanCount() {
		return permenantBans;
	}

	public float getPermenantBanCountPercentage() {
		return (float) permenantBans / this.bans.size();
	}

	public int getTemporaryBanCount() {
		return temporaryBans;
	}

	public float getTemporaryBanCountPercentage() {
		return (float) temporaryBans / this.bans.size();
	}

	public int getTotalBanCount() {
		return this.bans.size();
	}

	public float getTotalBanCountPercentage() {
		return (float) this.bans.size() / total;
	}

	private void update() {
		for (BanRecord record : bans) {
			switch (record.getType()) {
				case PERMANENT:
					permenantBans++;
					break;
				case TEMPORARY:
					temporaryBans++;
					break;
			}
			switch (record.getState()) {
				case NORMAL:
					normalBans++;
					break;
				case PARDONED:
					pardonedBans++;
					break;
				case EXPIRED:
					expiredBans++;
					break;
			}
		}
	}

}
