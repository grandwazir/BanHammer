/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * HistoryCommand.java is part of BanHammer.
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
package name.richardson.james.bukkit.banhammer.ban;

import java.lang.ref.WeakReference;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.matchers.BannedPlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.argument.PlayerRecordArgument;

import name.richardson.james.bukkit.utilities.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArguments;
import name.richardson.james.bukkit.utilities.command.CommandMatchers;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.command.argument.InvalidArgumentException;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.localisation.LocalisedCommandSender;
import name.richardson.james.bukkit.utilities.localisation.LocalisedCoreColourScheme;

@ConsoleCommand
@CommandPermissions(permissions = { "banhammer.history", "banhammer.history.own", "banhammer.history.others" })
@CommandArguments(arguments = {PlayerRecordArgument.class})
public class HistoryCommand extends AbstractCommand {

	/** Reference to the BanHammer API */
	private final PlayerRecordManager playerRecordManager;
	private final ColourScheme colourScheme = new LocalisedCoreColourScheme(getResourceBundle());
	private final ChoiceFormatter formatter;

	private PlayerRecord playerRecord;
	private WeakReference<CommandSender> sender;

	public HistoryCommand(final PlayerRecordManager playerRecordManager) {
		super();
		this.playerRecordManager = playerRecordManager;
		this.formatter = new ChoiceFormatter(this.getClass());
		this.formatter.setLimits(0, 1, 2);
		this.formatter.setLocalisedMessage(ColourFormatter.header(this.getLocalisation().getString("header")));
		this.formatter.setFormats("no-bans", "one-ban", "many-bans");;
		Bukkit.getPluginManager().getPermission("banhammer.history.own").setDefault(PermissionDefault.TRUE);
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		this.sender = new WeakReference<CommandSender>(sender);
		if (!this.parseArguments(arguments)) return;
		if (this.hasPermission(sender)) {
			final List<BanRecord> bans = playerRecord.getBans();
			this.displayHistory(bans, sender);
		} else {
			sender.sendMessage(colourScheme.format(ColourScheme.Style.INFO, "cannot-view-history", arguments.get(0)));
		}
	}

	@Override
	protected boolean parseArguments(List<String> arguments) {
		try {
			super.parseArguments(arguments);
			playerRecord = (PlayerRecord) getArguments().get(0).getValue();
			return true;
		} catch (InvalidArgumentException e) {
			sender.get().sendMessage(colourScheme.format(ColourScheme.Style.ERROR, e.getMessage(), e.getArgument()));
			return true;
		} finally {
			if (playerRecord == null || playerRecord.getBans().size() == 0) {
				sender.get().sendMessage(colourScheme.format(ColourScheme.Style.INFO, "player-has-no-history", arguments.get(0)));
				return false;
			}
		}
	}

	@Override
	protected void setArguments() {
		super.setArguments();
		PlayerRecordArgument argument = (PlayerRecordArgument) getArguments().get(0);
		argument.setRequired(false);
		argument.setPlayerStatus(PlayerRecordManager.PlayerStatus.ANY);
		PlayerRecordArgument.setPlayerRecordManager(playerRecordManager);
	}

	private void displayHistory(final List<BanRecord> bans, final CommandSender sender) {
		this.formatter.setArguments(bans.size(), playerRecord.getName());
		sender.sendMessage(this.formatter.getMessage());
		for (final BanRecord ban : bans) {
			final BanSummary summary = new BanSummary(ban);
			sender.sendMessage(summary.getSelfHeader());
			sender.sendMessage(summary.getReason());
			sender.sendMessage(summary.getLength());
			if (ban.getType() == BanRecord.Type.TEMPORARY) {
				sender.sendMessage(summary.getExpiresAt());
			}
		}
	}

	private boolean hasPermission(final CommandSender sender) {
		final boolean isSenderTargetingSelf = (playerRecord.getName().equalsIgnoreCase(sender.getName())) ? true : false;
		if (sender.hasPermission("banhammer.history.own") && isSenderTargetingSelf) { return true; }
		if (sender.hasPermission("banhammer.history.others") && !isSenderTargetingSelf) { return true; }
		return false;
	}

}
