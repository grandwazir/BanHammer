/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 AuditCommand.java is part of BanHammer.

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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractAsynchronousCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.BooleanMarshaller;

import name.richardson.james.bukkit.banhammer.model.BanRecord;
import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.model.PlayerRecord;
import name.richardson.james.bukkit.banhammer.argument.AllOptionArgument;
import name.richardson.james.bukkit.banhammer.argument.PlayerNamePositionalArgument;

public final class AuditCommand extends AbstractAsynchronousCommand {

	private static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	public static final String PERMISSION_ALL = "banhammer.audit";
	public static final String PERMISSION_SELF = "banhammer.audit.self";
	public static final String PERMISSION_OTHERS = "banhammer.audit.others";
	public static final String PERMISSION_AUDIT_ALL = "banhammer.audit.all";
	private final BooleanMarshaller all;
	private final Argument playerName;

	public AuditCommand(final Plugin plugin, final BukkitScheduler scheduler) {
		super(plugin, scheduler);
		playerName = PlayerNamePositionalArgument.getInstance(0, false, PlayerRecord.Status.CREATOR);
		all = AllOptionArgument.getInstance();
		addArgument((Argument) all);
		addArgument(playerName);
	}

	@Override public String getDescription() {
		return MESSAGES.auditCommandDescription();
	}

	@Override public String getName() {
		return MESSAGES.auditCommandName();
	}

	@Override public Set<String> getPermissions() {
		return new HashSet<>(Arrays.asList(PERMISSION_ALL, PERMISSION_AUDIT_ALL, PERMISSION_OTHERS, PERMISSION_SELF));
	}

	@Override
	protected void execute() {
		if (all.isSet()) {
			if (isAuthorised(PERMISSION_AUDIT_ALL)) {
				String playerName = MESSAGES.auditSummaryAll();
				Collection<BanRecord> bans = BanRecord.list();
				AuditCommandSummary summary = new AuditCommandSummary(bans);
				addMessage(MESSAGES.auditSummary(summary.getTotalBanCount(), playerName, summary.getTotalBanCountPercentage()));
				addMessages(summary.getMessages());
			} else {
				addMessage(MESSAGES.noPermissionToAuditAllBans());
			}
		} else {
			if (playerName.getStrings().isEmpty()) {
				String playerName = getContext().getCommandSender().getName();
				createAuditSummary(playerName);
			} else {
				for (String playerName : this.playerName.getStrings()) {
					createAuditSummary(playerName);
				}
			}
		}
	}

	private void createAuditSummary(String playerName) {
		PlayerRecord record = PlayerRecord.find(playerName);
		if (hasPermission(playerName)) {
			if (record != null) {
			Set<BanRecord> bans = record.getCreatedBans();
			AuditCommandSummary summary = new AuditCommandSummary(bans);
			addMessage(MESSAGES.auditSummary(summary.getTotalBanCount(), playerName, summary.getTotalBanCountPercentage()));
			addMessages(summary.getMessages());
		} else {
			addMessage(MESSAGES.playerHasMadeNoBans(playerName));
			}
		} else {
			addMessage(MESSAGES.notAllowedToAuditThatPlayer(playerName));
		}
	}

	private boolean hasPermission(String targetName) {
		boolean isSenderCheckingSelf = targetName.equalsIgnoreCase(getContext().getCommandSender().getName());
		return all.isSet() && getContext().isAuthorised(PERMISSION_AUDIT_ALL) || (!all.isSet() && getContext().isAuthorised(PERMISSION_SELF) && isSenderCheckingSelf) || (!all.isSet() && getContext().isAuthorised(PERMISSION_OTHERS) && !isSenderCheckingSelf);
	}

}
