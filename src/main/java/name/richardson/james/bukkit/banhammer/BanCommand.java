/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * BanCommand.java is part of BanHammer.
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

import java.util.Map;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.ApproximateTimeFormatter;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.DefaultColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;
import name.richardson.james.bukkit.utilities.localisation.Localisation;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundleByClassLocalisation;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.ban.event.BanHammerPlayerBannedEvent;

public class BanCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.ban";
	public static final String PERMISSION_PERMANENT = "banhammer.ban.permanent";

	private static final String PLAYER_BANNED_KEY = "player-banned";
	private static final String NO_PERMISSION_TO_BAN_THAT_LONG_KEY = "no-permission-to-ban-that-long";
	private static final String NO_PERMISSION_KEY = "no-permission";
	private static final String MUST_SPECIFY_PLAYER_KEY = "must-specify-player";
	private static final String PLAYER_IS_ALREADY_BANNED_KEY = "player-is-already-banned";
	private static final String MUST_SPECIFY_REASON_KEY = "must-specify-reason";

	private final ColourFormatter colourFormatter = new DefaultColourFormatter();
	private final Set<String> immunePlayers;
	private final Map<String, Long> limits;
	private final Localisation localisation = new ResourceBundleByClassLocalisation(BanCommand.class);
	private final PlayerRecordManager playerRecordManager;
	private final PluginManager pluginManager;
	private final TimeFormatter timeFormatter = new ApproximateTimeFormatter();

	private String playerName;
	private PlayerRecord playerRecord;
	private String reason;
	private long time;

	public BanCommand(PluginManager pluginManager, PlayerRecordManager playerRecordManager, Map<String, Long> limits, Set<String> immunePlayers) {
		this.playerRecordManager = playerRecordManager;
		this.limits = limits;
		this.immunePlayers = immunePlayers;
		this.pluginManager = pluginManager;
	}

	@Override
	public void execute(CommandContext context) {
		if (!setPlayerName(context)) return;
		if (!setReason(context)) return;
		if (!setPlayerRecord(context)) return;
		setExpiryTime(context);
		if (!hasPermission(context.getCommandSender())) return;
		PlayerRecordManager.BannedPlayerBuilder bannedPlayerBuilder = playerRecordManager.getBannedPlayerBuilder();
		bannedPlayerBuilder.setPlayer(playerName);
		bannedPlayerBuilder.setCreator(context.getCommandSender().getName());
		bannedPlayerBuilder.setExpiryTime(time);
		bannedPlayerBuilder.setReason(reason);
		bannedPlayerBuilder.save();
		context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(PLAYER_BANNED_KEY), ColourFormatter.FormatStyle.INFO, playerName));
		boolean silent = (context.hasFlag("s") || context.hasFlag("silent"));
		BanHammerPlayerBannedEvent event = new BanHammerPlayerBannedEvent(bannedPlayerBuilder.getRecord(), silent);
		pluginManager.callEvent(event);
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		if (permissible.hasPermission(PERMISSION_ALL)) return true;
		if (permissible.hasPermission(PERMISSION_PERMANENT)) return true;
		for (String limit : limits.keySet()) {
			final String node = BanCommand.PERMISSION_ALL + "." + limit;
			if (permissible.hasPermission(node)) return true;
		}
		return false;
	}

	private boolean hasPermission(final CommandSender sender) {
		boolean immunePlayerTargeted = this.immunePlayers.contains(this.playerName);
		if (sender.hasPermission(BanCommand.PERMISSION_ALL)) return true;
		if (!immunePlayerTargeted && sender.hasPermission(BanCommand.PERMISSION_PERMANENT)) return true;
		for (final String limitName : this.limits.keySet()) {
			final String node = BanCommand.PERMISSION_ALL + "." + limitName;
			if (!sender.hasPermission(node)) continue;
			if (!immunePlayerTargeted && (this.limits.get(limitName) >= this.time)) return true;
		}
		sender.sendMessage(colourFormatter.format(localisation.getMessage(NO_PERMISSION_KEY), ColourFormatter.FormatStyle.ERROR, playerName));
		return false;
	}

	private void setExpiryTime(CommandContext context) {
		if (context.hasFlag("t")) {
			String timeString = context.getFlag("t");
			if (limits.containsKey(timeString)) {
				time = limits.get(timeString);
			} else {
				time = timeFormatter.getDurationInMilliseconds(context.getFlag("t"));
			}
		} else {
			time = 0;
		}
	}

	private boolean setPlayerName(CommandContext context) {
		playerName = null;
		if (context.has(0)) playerName = context.getString(0);
		if (playerName == null) {
			context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(MUST_SPECIFY_PLAYER_KEY), ColourFormatter.FormatStyle.ERROR));
			return false;
		} else {
			return true;
		}
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(playerName);
		if (playerRecord != null && playerRecord.isBanned()) {
			context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(PLAYER_IS_ALREADY_BANNED_KEY), ColourFormatter.FormatStyle.ERROR, playerName));
			return false;
		} else {
			return true;
		}
	}

	private boolean setReason(CommandContext context) {
		if (context.has(1)) {
			reason = context.getJoinedArguments(1);
			return true;
		} else {
			context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(MUST_SPECIFY_REASON_KEY), ColourFormatter.FormatStyle.ERROR));
			return false;
		}
	}

	@Override
	public String toString() {
		return "BanCommand{" +
		"colourFormatter=" + colourFormatter +
		", immunePlayers=" + immunePlayers +
		", limits=" + limits +
		", localisation=" + localisation +
		", playerRecordManager=" + playerRecordManager +
		", pluginManager=" + pluginManager +
		", timeFormatter=" + timeFormatter +
		", playerName='" + playerName + '\'' +
		", playerRecord=" + playerRecord +
		", reason='" + reason + '\'' +
		", time=" + time +
		"} " + super.toString();
	}
}
