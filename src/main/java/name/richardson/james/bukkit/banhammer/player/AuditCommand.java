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
package name.richardson.james.bukkit.banhammer.player;

import java.util.*;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractAsynchronousCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.BooleanMarshaller;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.PlayerRecord;
import name.richardson.james.bukkit.banhammer.argument.AllOptionArgument;
import name.richardson.james.bukkit.banhammer.argument.PlayerNamePositionalArgument;
import name.richardson.james.bukkit.banhammer.BanRecord;

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
		this.playerName = PlayerNamePositionalArgument.getInstance(0, false, PlayerRecord.Status.CREATOR);
		this.all = AllOptionArgument.getInstance();
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
		final CommandSender commandSender = getContext().getCommandSender();
		if (all.isSet()) {
			if (hasPermission(commandSender, null)) {
				final String playerName = MESSAGES.auditSummaryAll();
				final Collection<BanRecord> bans = BanRecord.list();
				addMessages(getResponse(playerName, bans, bans.size()));
			} else {
				addMessage(MESSAGES.noPermissionToAuditAllBans());
			}
		} else {
			Collection<String> playerNames = new ArrayList<String>(this.playerName.getStrings());
			if (playerNames.isEmpty()) {
				String playerName = (this.playerName.getString() == null) ? commandSender.getName() : this.playerName.getString();
				playerNames.add(playerName);
			}
			final int total = BanRecord.count();
			for (String playerName : playerNames) {
				if (hasPermission(commandSender, playerName)) {
					PlayerRecord record = PlayerRecord.find(playerName);
					if (record != null) {
						final Set<BanRecord> bans = record.getCreatedBans();
						addMessages(getResponse(playerName, bans, total));
					} else {
						addMessage(MESSAGES.playerHasMadeNoBans());
					}
				} else {
					addMessage(MESSAGES.notAllowedToAuditThatPlayer(playerName));
				}
			}
		}
	}

	private Collection<String> getResponse(String playerName, Collection<BanRecord> bans, int count) {
		Collection<String> messages = new ArrayList<String>();
		AuditCommandSummary summary = new AuditCommandSummary(bans, count);
		messages.add(MESSAGES.auditSummary(summary.getTotalBanCount(), playerName, summary.getTotalBanCountPercentage()));
		messages.addAll(summary.getMessages());
		return messages;
	}

	private boolean hasPermission(final CommandSender sender, String targetName) {
		final boolean isSenderCheckingSelf = targetName.equalsIgnoreCase(sender.getName());
		return this.all.isSet() && getContext().isAuthorised(PERMISSION_AUDIT_ALL) || (!all.isSet() && getContext().isAuthorised(PERMISSION_SELF) && isSenderCheckingSelf) || (!all.isSet() && getContext().isAuthorised(PERMISSION_OTHERS) && !isSenderCheckingSelf);
	}

}
