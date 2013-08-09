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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.formatters.localisation.LocalisedChoiceFormatter;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;
import name.richardson.james.bukkit.utilities.permissions.Permissions;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

@Permissions(permissions = {AuditCommand.PERMISSION_ALL, AuditCommand.PERMISSION_SELF, AuditCommand.PERMISSION_OTHERS})
public class AuditCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.audit";
	public static final String PERMISSION_SELF = "banhammer.audit.self";
	public static final String PERMISSION_OTHERS = "banhammer.audit.others";

	private final LocalisedChoiceFormatter choiceFormatter;
	private final PlayerRecordManager playerRecordManager;
	private final BanRecordManager banRecordManager;

	private String playerName;
	private PlayerRecord playerRecord;

	public AuditCommand(PermissionManager permissionManager, PlayerRecordManager playerRecordManager, BanRecordManager banRecordManager) {
		super(permissionManager);
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
		this.choiceFormatter = new BanCountChoiceFormatter();
		this.choiceFormatter.setMessage("audit-header");
	}

	@Override
	public void execute(CommandContext context) {
		if (!setPlayerName(context)) return;
		if (!setPlayerRecord(context)) return;
		if (!hasPermission(context.getCommandSender())) return;
		AuditSummary auditSummary = new AuditSummary(playerRecord.getCreatedBans(), banRecordManager.count());
		choiceFormatter.setArguments(auditSummary.getTotalBanCount(), context.getCommandSender().getName(), auditSummary.getTotalBanCountPercentage());
		context.getCommandSender().sendMessage(choiceFormatter.getColouredMessage(ColourScheme.Style.HEADER));
		context.getCommandSender().sendMessage(auditSummary.getMessages());
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(playerName);
		if (playerRecord != null && playerRecord.getCreatedBans().size() != 0 ) {
			return true;
		} else {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.WARNING, "player-has-made-no-bans", playerName));
			return false;
		}
	}

	private boolean setPlayerName(CommandContext context) {
		playerName = null;
		if (context.has(0)) {
			playerName = context.getString(0);
		} else {
			playerName = context.getCommandSender().getName();
		}
		return playerName != null;
	}

	private boolean hasPermission(final CommandSender sender) {
		final boolean isSenderCheckingSelf = playerName.equalsIgnoreCase(sender.getName());
		if (sender.hasPermission(PERMISSION_SELF) && isSenderCheckingSelf) return true;
		if (sender.hasPermission(PERMISSION_OTHERS) && !isSenderCheckingSelf) return true;
		sender.sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "no-permission"));
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

		public int getPermanentBanCount() {
			return permenantBans;
		}

		public float getPermanentBanCountPercentage() {
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

		public String[] getMessages() {
			List<String> messages = new ArrayList<String>();
			messages.add(AuditCommand.this.getColouredMessage(ColourScheme.Style.HEADER, "type-summary"));
			messages.add(AuditCommand.this.getColouredMessage(ColourScheme.Style.INFO, "permanent-bans-percentage", getPermanentBanCount(), getPermanentBanCountPercentage()));
			messages.add(AuditCommand.this.getColouredMessage(ColourScheme.Style.INFO, "temporary-bans-percentage", getTemporaryBanCount(), getTemporaryBanCountPercentage()));
			messages.add(AuditCommand.this.getColouredMessage(ColourScheme.Style.HEADER, "status-summary"));
			messages.add(AuditCommand.this.getColouredMessage(ColourScheme.Style.INFO, "active-bans-percentage", getNormalBanCount(), getNormalBanCountPercentage()));
			messages.add(AuditCommand.this.getColouredMessage(ColourScheme.Style.INFO, "expired-bans-percentage", getExpiredBanCount(), getExpiredBanCountPercentage()));
			messages.add(AuditCommand.this.getColouredMessage(ColourScheme.Style.INFO, "pardoned-bans-percentage", getPardonedBanCount(), getPardonedBanCountPercentage()));
			return messages.toArray(new String[messages.size()]);
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
