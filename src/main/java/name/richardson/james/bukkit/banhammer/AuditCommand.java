/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * AuditCommand.java is part of BanHammer.
 *
 * BanHammer is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;
import name.richardson.james.bukkit.utilities.permissions.Permissions;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

@Permissions(permissions = {"banhammer.audit", "banhammer.audit.self", "banhammer.audit.others"})
public class AuditCommand extends AbstractCommand {

	private final BanRecordManager banRecordManager;
	private final PlayerRecordManager playerRecordManager;

	private OfflinePlayer player;

	public AuditCommand(PermissionManager permissionManager, PlayerRecordManager playerRecordManager, BanRecordManager banRecordManager) {
		super(permissionManager);
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
	}

	@Override
	public void execute(CommandContext context) {
		//TODO: Reimplement this in a nice way.
	}

	private boolean hasPermission(final CommandSender sender) {
		final boolean isSenderCheckingSelf = this.player.getName().equalsIgnoreCase(sender.getName());
		if (sender.hasPermission("banhammer.audit.self") && isSenderCheckingSelf) return true;
		if (sender.hasPermission("banhammer.audit.others") && !isSenderCheckingSelf) return true;
		return false;
	}

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
}
