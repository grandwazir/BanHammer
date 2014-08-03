/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 AuditCommandSummary.java is part of BanHammer.

 BanHammer is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option) any
 later version.

 BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.BanRecord;

public final class AuditCommandSummary {

	private static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	private final Collection<BanRecord> bans;
	private int expiredBans;
	private int normalBans;
	private int pardonedBans;
	private int permanentBans;
	private int temporaryBans;
	private int total;

	protected AuditCommandSummary(Collection<BanRecord> bans) {
		this.bans = bans;
		this.total = BanRecord.count();
		this.update();
	}

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
		messages.add(MESSAGES.auditSummaryType());
		messages.add(MESSAGES.auditPermanentBanPercentage(getPermanentBanCount(), getPermanentBanCountPercentage()));
		messages.add(MESSAGES.auditTemporaryBanPercentage(getTemporaryBanCount(), getTemporaryBanCountPercentage()));
		messages.add(MESSAGES.auditSummaryStatus());
		messages.add(MESSAGES.auditNormalBanPercentage(getNormalBanCount(), getNormalBanCountPercentage()));
		messages.add(MESSAGES.auditExpiredBanPercentage(getExpiredBanCount(), getExpiredBanCountPercentage()));
		messages.add(MESSAGES.auditPardonedBanPercentage(getPardonedBanCount(), getPardonedBanCountPercentage()));
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
