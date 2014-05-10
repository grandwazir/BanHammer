package name.richardson.james.bukkit.banhammer.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerMessages.*;

public final class AuditCommandSummary {

	private final Collection<BanRecord> bans;
	private int expiredBans;
	private int normalBans;
	private int pardonedBans;
	private int permanentBans;
	private int temporaryBans;
	private int total;

	protected AuditCommandSummary(Collection<BanRecord> bans, int total) {
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

	public List<String> getMessages() {
		List<String> messages = new ArrayList<String>();
		messages.add(AUDIT_TYPE_SUMMARY.asHeaderMessage());
		messages.add(AUDIT_PERMANENT_BANS_PERCENTAGE.asInfoMessage(getPermanentBanCount(), getPardonedBanCountPercentage()));
		messages.add(AUDIT_TEMPORARY_BANS_PERCENTAGE.asInfoMessage(getTemporaryBanCount(), getTemporaryBanCountPercentage()));
		messages.add(AUDIT_STATUS_SUMMARY.asHeaderMessage());
		messages.add(AUDIT_ACTIVE_BANS_PERCENTAGE.asInfoMessage(getNormalBanCount(), getNormalBanCountPercentage()));
		messages.add(AUDIT_EXPIRED_BANS_PERCENTAGE.asInfoMessage(getExpiredBanCount(), getExpiredBanCountPercentage()));
		messages.add(AUDIT_PARDONED_BANS_PERCENTAGE.asInfoMessage(getPardonedBanCount(), getPardonedBanCountPercentage()));
		return messages;
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

	public int getPermanentBanCount() {
		return permanentBans;
	}

	public float getPermanentBanCountPercentage() {
		return (float) permanentBans / this.bans.size();
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
					permanentBans++;
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
