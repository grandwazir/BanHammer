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
import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.localisation.PluginLocalisation;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.formatters.BanCountChoiceFormatter;
import name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation;

public class AuditCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.audit";
	public static final String PERMISSION_SELF = "banhammer.audit.self";
	public static final String PERMISSION_OTHERS = "banhammer.audit.others";

	private final BanRecordManager banRecordManager;
	private final ChoiceFormatter choiceFormatter;
	private final PlayerRecordManager playerRecordManager;
	private String playerName;
	private PlayerRecord playerRecord;

	public AuditCommand(PlayerRecordManager playerRecordManager, BanRecordManager banRecordManager) {
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
		this.choiceFormatter = new BanCountChoiceFormatter();
		this.choiceFormatter.setMessage(getLocalisation().formatAsHeaderMessage(BanHammerLocalisation.AUDIT_COMMAND_HEADER));
	}

	@Override
	public void execute(CommandContext context) {
		if (!setPlayerName(context)) return;
		if (!setPlayerRecord(context)) return;
		if (!hasPermission(context.getCommandSender())) return;
		AuditSummary auditSummary = new AuditSummary(playerRecord.getCreatedBans(), banRecordManager.count());
		choiceFormatter.setArguments(auditSummary.getTotalBanCount(), playerName, auditSummary.getTotalBanCountPercentage());
		context.getCommandSender().sendMessage(choiceFormatter.getMessage());
		context.getCommandSender().sendMessage(auditSummary.getMessages());
	}

	private boolean hasPermission(final CommandSender sender) {
		final boolean isSenderCheckingSelf = playerName.equalsIgnoreCase(sender.getName());
		if (sender.hasPermission(PERMISSION_SELF) && isSenderCheckingSelf) return true;
		if (sender.hasPermission(PERMISSION_OTHERS) && !isSenderCheckingSelf) return true;
		String message = getLocalisation().formatAsErrorMessage(PluginLocalisation.COMMAND_NO_PERMISSION);
		sender.sendMessage(message);
		return false;
	}

	private boolean setPlayerName(CommandContext context) {
		playerName = null;
		if (context.hasArgument(0)) {
			playerName = context.getString(0);
		} else {
			playerName = context.getCommandSender().getName();
		}
		return playerName != null;
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(playerName);
		if (playerRecord != null && playerRecord.getCreatedBans().size() != 0 ) {
			return true;
		} else {
			String message = getLocalisation().formatAsInfoMessage(name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation.PLAYER_HAS_MADE_NO_BANS, playerName);
			context.getCommandSender().sendMessage(message);
			return false;
		}
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		if (permissible.hasPermission(PERMISSION_ALL)) return true;
		if (permissible.hasPermission(PERMISSION_OTHERS)) return true;
		if (permissible.hasPermission(PERMISSION_SELF)) return true;
		return false;
	}

	public final class AuditSummary {

		private final List<BanRecord> bans;
		private int expiredBans;
		private int normalBans;
		private int pardonedBans;
		private int permanentBans;
		private int temporaryBans;
		private int total;

		private AuditSummary(List<BanRecord> bans, int total) {
			this.bans = bans;
			this.total = total;
			this.update();
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

		public String[] getMessages() {
			List<String> messages = new ArrayList<String>();
			messages.add(getLocalisation().formatAsHeaderMessage(BanHammerLocalisation.AUDIT_TYPE_SUMMARY));
			messages.add(getLocalisation().formatAsInfoMessage(BanHammerLocalisation.AUDIT_PERMANENT_BANS_PERCENTAGE, getPermanentBanCount(), getPardonedBanCountPercentage()));
			messages.add(getLocalisation().formatAsInfoMessage(BanHammerLocalisation.AUDIT_TEMPORARY_BANS_PERCENTAGE, getTemporaryBanCount(), getTemporaryBanCountPercentage()));
			messages.add(getLocalisation().formatAsHeaderMessage(BanHammerLocalisation.AUDIT_STATUS_SUMMARY));
			messages.add(getLocalisation().formatAsInfoMessage(BanHammerLocalisation.AUDIT_ACTIVE_BANS_PERCENTAGE, getNormalBanCount(), getNormalBanCountPercentage()));
			messages.add(getLocalisation().formatAsInfoMessage(BanHammerLocalisation.AUDIT_EXPIRED_BANS_PERCENTAGE, getExpiredBanCount(), getExpiredBanCountPercentage()));
			messages.add(getLocalisation().formatAsInfoMessage(BanHammerLocalisation.AUDIT_PARDONED_BANS_PERCENTAGE, getPardonedBanCount(), getPardonedBanCountPercentage()));
			return messages.toArray(new String[messages.size()]);
		}

		public float getTemporaryBanCountPercentage() {
			return (float) temporaryBans / this.bans.size();
		}

		public int getTemporaryBanCount() {
			return temporaryBans;
		}

		public int getPermanentBanCount() {
			return permanentBans;
		}

		public float getPardonedBanCountPercentage() {
			return (float) pardonedBans / this.bans.size();
		}

		public int getPardonedBanCount() {
			return pardonedBans;
		}

		public float getNormalBanCountPercentage() {
			return (float) normalBans / this.bans.size();
		}

		public int getNormalBanCount() {
			return normalBans;
		}

		public float getExpiredBanCountPercentage() {
			return (float) expiredBans / this.bans.size();
		}

		public int getExpiredBanCount() {
			return expiredBans;
		}

		public float getPermanentBanCountPercentage() {
			return (float) permanentBans / this.bans.size();
		}

		public int getTotalBanCount() {
			return this.bans.size();
		}

		public float getTotalBanCountPercentage() {
			return (float) this.bans.size() / total;
		}

	}
}
