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
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.DefaultColourFormatter;
import name.richardson.james.bukkit.utilities.localisation.Localisation;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundleByClassLocalisation;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.formatters.BanCountChoiceFormatter;

public class AuditCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.audit";
	public static final String PERMISSION_SELF = "banhammer.audit.self";
	public static final String PERMISSION_OTHERS = "banhammer.audit.others";

	private static final String PLAYER_HAS_MADE_NO_BANS_KEY = "player-has-made-no-bans";
	private static final String HEADER_KEY = "header";
	private static final String NO_PERMISSION_KEY = "no-permission";

	private final ChoiceFormatter choiceFormatter;
	private final PlayerRecordManager playerRecordManager;
	private final Localisation localisation = new ResourceBundleByClassLocalisation(AuditCommand.class);
	private final ColourFormatter colourFormatter = new DefaultColourFormatter();
	private final BanRecordManager banRecordManager;

	private String playerName;
	private PlayerRecord playerRecord;

	public AuditCommand(PlayerRecordManager playerRecordManager, BanRecordManager banRecordManager) {
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
		this.choiceFormatter = new BanCountChoiceFormatter();
		this.choiceFormatter.setMessage(colourFormatter.format(localisation.getMessage(HEADER_KEY), ColourFormatter.FormatStyle.HEADER));
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

	@Override
	public boolean isAuthorised(Permissible permissible) {
		if (permissible.hasPermission(PERMISSION_ALL)) return true;
		if (permissible.hasPermission(PERMISSION_OTHERS)) return true;
		if (permissible.hasPermission(PERMISSION_SELF)) return true;
		return false;
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(playerName);
		if (playerRecord != null && playerRecord.getCreatedBans().size() != 0 ) {
			return true;
		} else {
			String message = colourFormatter.format(localisation.getMessage(PLAYER_HAS_MADE_NO_BANS_KEY), ColourFormatter.FormatStyle.INFO, playerName);
			context.getCommandSender().sendMessage(message);
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
		sender.sendMessage(colourFormatter.format(localisation.getMessage(NO_PERMISSION_KEY), ColourFormatter.FormatStyle.ERROR, playerName));
		return false;
	}

	public final class AuditSummary {

		private static final String TYPE_SUMMARY_KEY = "type-summary";
		private static final String PERMANENT_BANS_PERCENTAGE_KEY = "permanent-bans-percentage";
		private static final String TEMPORARY_BANS_PERCENTAGE_KEY = "temporary-bans-percentage";
		private static final String STATUS_SUMMARY_KEY = "status-summary";
		private static final String ACTIVE_BANS_PERCENTAGE_KEY = "active-bans-percentage";
		private static final String EXPIRED_BANS_PERCENTAGE_KEY = "expired-bans-percentage";
		private static final String PARDONED_BANS_PERCENTAGE_KEY = "pardoned-bans-percentage";

		private final List<BanRecord> bans;
		private final Localisation localisation = new ResourceBundleByClassLocalisation(AuditSummary.class);
		private final ColourFormatter colourFormatter = new DefaultColourFormatter();

		private int permanentBans;
		private int temporaryBans;
		private int normalBans;
		private int pardonedBans;
		private int expiredBans;
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

		public String[] getMessages() {
			List<String> messages = new ArrayList<String>();
			messages.add(colourFormatter.format(localisation.getMessage(TYPE_SUMMARY_KEY), ColourFormatter.FormatStyle.HEADER));
			messages.add(colourFormatter.format(localisation.getMessage(PERMANENT_BANS_PERCENTAGE_KEY), ColourFormatter.FormatStyle.INFO, getPermanentBanCount(), getPermanentBanCountPercentage()));
			messages.add(colourFormatter.format(localisation.getMessage(TEMPORARY_BANS_PERCENTAGE_KEY), ColourFormatter.FormatStyle.INFO, getTemporaryBanCount(), getTemporaryBanCountPercentage()));
			messages.add(colourFormatter.format(localisation.getMessage(STATUS_SUMMARY_KEY), ColourFormatter.FormatStyle.HEADER));
			messages.add(colourFormatter.format(localisation.getMessage(ACTIVE_BANS_PERCENTAGE_KEY), ColourFormatter.FormatStyle.INFO, getNormalBanCount(), getNormalBanCountPercentage()));
			messages.add(colourFormatter.format(localisation.getMessage(EXPIRED_BANS_PERCENTAGE_KEY), ColourFormatter.FormatStyle.INFO, getExpiredBanCount(), getExpiredBanCountPercentage()));
			messages.add(colourFormatter.format(localisation.getMessage(PARDONED_BANS_PERCENTAGE_KEY), ColourFormatter.FormatStyle.INFO, getPardonedBanCount(), getPardonedBanCountPercentage()));
			return messages.toArray(new String[messages.size()]);
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
