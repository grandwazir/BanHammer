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

import java.util.*;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.argument.AllOptionArgument;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.BooleanMarshaller;
import name.richardson.james.bukkit.utilities.command.argument.PlayerNamePositionalArgument;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.localisation.BukkitUtilities;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.formatters.BanCountChoiceFormatter;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.*;

public final class AuditCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.audit";
	public static final String PERMISSION_SELF = "banhammer.audit.self";
	public static final String PERMISSION_OTHERS = "banhammer.audit.others";
	public static final String PERMISSION_AUDIT_ALL = "banhammer.audit.all";
	private final BooleanMarshaller all;
	private final BanRecordManager banRecordManager;
	private final ChoiceFormatter choiceFormatter;
	private final Argument playerName;
	private final PlayerRecordManager playerRecordManager;

	public AuditCommand(PlayerRecordManager playerRecordManager, BanRecordManager banRecordManager) {
		super(AUDIT_COMMAND_NAME, AUDIT_COMMAND_DESC);
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
		this.playerName = PlayerNamePositionalArgument.getInstance(playerRecordManager, 0, false, PlayerRecordManager.PlayerStatus.CREATOR);
		this.all = AllOptionArgument.getInstance();
		addArgument(all);
		addArgument(playerName);
		this.choiceFormatter = new BanCountChoiceFormatter();
		this.choiceFormatter.setMessage(AUDIT_COMMAND_HEADER.asHeaderMessage());
	}

	@Override
	public boolean isAsynchronousCommand() {
		return false;
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL) || permissible.hasPermission(PERMISSION_OTHERS) || permissible.hasPermission(PERMISSION_SELF);
	}

	@Override
	protected void execute() {
		String player = (this.playerName.getString() == null) ? getContext().getCommandSender().getName() : this.playerName.getString();
		Collection<String> playerNames = new ArrayList<String>(this.playerName.getStrings());
		if (playerNames.isEmpty()) playerNames.add(player);
		List<String> messages = new ArrayList<String>();
		for (String playerName : playerNames) {
			if (!hasPermission(getContext().getCommandSender(), player)) {
				messages.add(BukkitUtilities.INVOKER_NO_PERMISSION.asErrorMessage());
			} else {
				AuditSummary auditSummary = null;
				if (all.isSet()) {
					auditSummary = new AuditSummary(banRecordManager.list(), banRecordManager.count());
					playerName = "Everyone";
				} else if (playerRecordManager.exists(player)) {
					PlayerRecord record = playerRecordManager.find(player);
					auditSummary = new AuditSummary(record.getCreatedBans(), banRecordManager.count());
				}
				if (auditSummary != null) {
					choiceFormatter.setArguments(auditSummary.getTotalBanCount(), playerName, auditSummary.getTotalBanCountPercentage());
					messages.add(choiceFormatter.getMessage());
					messages.addAll(auditSummary.getMessages());
				} else {
					messages.add(PLAYER_HAS_NEVER_MADE_ANY_BANS.asInfoMessage(playerName));
				}
				if (all.isSet()) break;
			}
		}
		getContext().getCommandSender().sendMessage(messages.toArray(new String[messages.size()]));
	}

	private boolean hasPermission(final CommandSender sender, String targetName) {
		final boolean isSenderCheckingSelf = targetName.equalsIgnoreCase(sender.getName());
		return this.all.isSet() && sender.hasPermission(PERMISSION_AUDIT_ALL) || (!all.isSet() && sender.hasPermission(PERMISSION_SELF) && isSenderCheckingSelf) || (!all.isSet() && sender.hasPermission(PERMISSION_OTHERS) && !isSenderCheckingSelf);
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
}
