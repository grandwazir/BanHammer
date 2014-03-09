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
import name.richardson.james.bukkit.utilities.formatters.time.ApproximateTimeFormatter;
import name.richardson.james.bukkit.utilities.formatters.time.TimeFormatter;
import name.richardson.james.bukkit.utilities.localisation.PluginLocalisation;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.ban.event.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation;

public class BanCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.ban";
	public static final String PERMISSION_PERMANENT = "banhammer.ban.permanent";

	private final Set<String> immunePlayers;
	private final Map<String, Long> limits;
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
		String message = getLocalisation().formatAsInfoMessage(BanHammerLocalisation.PLAYER_BANNED, playerName);
		context.getCommandSender().sendMessage(message);
		boolean silent = (context.hasSwitch("s") || context.hasSwitch("silent"));
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
			if (time == 0) break;
			if (!sender.hasPermission(node)) continue;
			if (!immunePlayerTargeted && (this.limits.get(limitName) >= this.time)) return true;
		}
		String message = getLocalisation().formatAsErrorMessage(PluginLocalisation.COMMAND_NO_PERMISSION);
		sender.sendMessage(message);
		return false;
	}

	private void setExpiryTime(CommandContext context) {
		if (context.hasSwitch("t")) {
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
		if (context.hasArgument(0)) playerName = context.getString(0);
		if (playerName == null) {
			String message = getLocalisation().formatAsErrorMessage(PluginLocalisation.COMMAND_MUST_SPECIFY_PLAYER);
			context.getCommandSender().sendMessage(message);
			return false;
		} else {
			return true;
		}
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(playerName);
		if (playerRecord != null && playerRecord.isBanned()) {
			String message = getLocalisation().formatAsErrorMessage(BanHammerLocalisation.BAN_PLAYER_IS_ALREADY_BANNED, playerName);
			context.getCommandSender().sendMessage(message);
			return false;
		} else {
			return true;
		}
	}

	private boolean setReason(CommandContext context) {
		if (context.hasArgument(1)) {
			reason = context.getJoinedArguments(1);
			return true;
		} else {
			String message = getLocalisation().formatAsErrorMessage(BanHammerLocalisation.BAN_MUST_SPECIFY_REASON);
			context.getCommandSender().sendMessage(message);
			return false;
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("BanCommand{");
		sb.append("immunePlayers=").append(immunePlayers);
		sb.append(", limits=").append(limits);
		sb.append(", playerName='").append(playerName).append('\'');
		sb.append(", playerRecord=").append(playerRecord);
		sb.append(", playerRecordManager=").append(playerRecordManager);
		sb.append(", pluginManager=").append(pluginManager);
		sb.append(", reason='").append(reason).append('\'');
		sb.append(", time=").append(time);
		sb.append(", timeFormatter=").append(timeFormatter);
		sb.append(", ").append(super.toString());
		sb.append('}');
		return sb.toString();
	}

}
