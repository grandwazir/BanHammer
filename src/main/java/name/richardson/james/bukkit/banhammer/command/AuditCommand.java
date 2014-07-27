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
package name.richardson.james.bukkit.banhammer.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.banhammer.command.argument.AllOptionArgument;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.BooleanMarshaller;
import name.richardson.james.bukkit.banhammer.command.argument.PlayerNamePositionalArgument;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.localisation.BukkitUtilities;

import name.richardson.james.bukkit.banhammer.model.PlayerRecord;
import name.richardson.james.bukkit.banhammer.record.*;
import name.richardson.james.bukkit.banhammer.utilities.formatters.BanCountChoiceFormatter;
import name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerMessages;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerMessages.*;

public final class AuditCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.audit";
	public static final String PERMISSION_SELF = "banhammer.audit.self";
	public static final String PERMISSION_OTHERS = "banhammer.audit.others";
	public static final String PERMISSION_AUDIT_ALL = "banhammer.audit.all";
	private final BooleanMarshaller all;
	private final ChoiceFormatter choiceFormatter;
	private final Argument playerName;
	private EbeanServer database;

	public AuditCommand(EbeanServer database) {
		super(AUDIT_COMMAND_NAME, AUDIT_COMMAND_DESC);
		this.database = database;
		this.playerName = PlayerNamePositionalArgument.getInstance(0, false, PlayerRecord.Status.CREATOR);
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
		final CommandSender commandSender = getContext().getCommandSender();
		final List<String> messages = new ArrayList<String>();
		if (all.isSet()) {
			if (hasPermission(commandSender, null)) {
				final String playerName = BanHammerMessages.AUDIT_ALL_NAME.asMessage();
				final Collection<BanRecord> bans = BanRecordFactory.list(database);
				messages.addAll(getResponse(playerName, bans, bans.size()));
			} else {
				messages.add(BukkitUtilities.INVOKER_NO_PERMISSION.asErrorMessage());
			}
		} else {
			Collection<String> playerNames = new ArrayList<String>(this.playerName.getStrings());
			if (playerNames.isEmpty()) {
				String playerName = (this.playerName.getString() == null) ? commandSender.getName() : this.playerName.getString();
				playerNames.add(playerName);
			}
			final int total = BanRecordFactory.count(database);
			for (String playerName : playerNames) {
				if (hasPermission(commandSender, playerName)) {
					PlayerRecord record = PlayerRecordFactory.find(database, playerName);
					if (record != null) {
						final List<? extends BanRecord> bans = record.getCreatedBans();
						messages.addAll(getResponse(playerName, bans, total));
					} else {
						messages.add(PLAYER_HAS_NEVER_MADE_ANY_BANS.asInfoMessage(playerName));
					}
				} else {
					messages.add(BukkitUtilities.INVOKER_NO_PERMISSION.asErrorMessage());
				}
			}
		}
		commandSender.sendMessage(messages.toArray(new String[messages.size()]));
	}

	private Collection<String> getResponse(String playerName, Collection<? extends BanRecord> bans, int count) {
		Collection<String> messages = new ArrayList<String>();
		AuditCommandSummary summary = new AuditCommandSummary(bans, count);
		choiceFormatter.setArguments(summary.getTotalBanCount(), playerName, summary.getTotalBanCountPercentage());
		messages.add(choiceFormatter.getMessage());
		messages.addAll(summary.getMessages());
		return messages;
	}

	private boolean hasPermission(final CommandSender sender, String targetName) {
		final boolean isSenderCheckingSelf = targetName.equalsIgnoreCase(sender.getName());
		return this.all.isSet() && sender.hasPermission(PERMISSION_AUDIT_ALL) || (!all.isSet() && sender.hasPermission(PERMISSION_SELF) && isSenderCheckingSelf) || (!all.isSet() && sender.hasPermission(PERMISSION_OTHERS) && !isSenderCheckingSelf);
	}

}
