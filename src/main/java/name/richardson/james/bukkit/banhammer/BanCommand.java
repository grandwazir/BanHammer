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

import java.util.*;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.argument.*;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.ban.event.BanHammerPlayerBannedEvent;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.*;
import static name.richardson.james.bukkit.utilities.localisation.BukkitUtilities.INVOKER_NO_PERMISSION;

public class BanCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.ban";
	public static final String PERMISSION_PERMANENT = "banhammer.ban.permanent";
	private final Set<String> immunePlayers;
	private final Map<String, Long> limits;
	private final Argument player;
	private final PlayerRecordManager playerRecordManager;
	private final PluginManager pluginManager;
	private final Argument reason;
	private final SilentSwitchArgument silent;
	private final TimeMarshaller time;

	public BanCommand(Server server, PluginManager pluginManager, PlayerRecordManager playerRecordManager, Map<String, Long> limits, Set<String> immunePlayers) {
		super(BANCOMMAND_NAME, BANCOMMAND_DESC);
		this.playerRecordManager = playerRecordManager;
		this.limits = limits;
		this.immunePlayers = immunePlayers;
		this.pluginManager = pluginManager;
		player = PlayerNamePositionalArgument.getInstance(server, 0, true);
		time = TimeOptionArgument.getInstance();
		silent = SilentSwitchArgument.getInstance();
		reason = ReasonPositionalArgument.getInstance(1, true);
		addArgument(silent);
		addArgument(time);
		addArgument(player);
		addArgument(reason);
	}

	@Override
	public void execute() {
		CommandSender sender = getContext().getCommandSender();
		List<String> messages = new ArrayList<String>();
		List<BanRecord> records = new ArrayList<BanRecord>();
		Collection<String> players = player.getStrings();
		boolean silent = this.silent.isSet();
		long time = this.time.getDuration();
		for (String player : players) {
			if (!hasPermission(sender, player)) {
				messages.add(INVOKER_NO_PERMISSION.asErrorMessage());
			} else if (playerRecordManager.exists(player) && playerRecordManager.find(player).getActiveBan() != null) {
				messages.add(PLAYER_IS_ALREADY_BANNED.asWarningMessage(player));
			} else {
				PlayerRecordManager.BannedPlayerBuilder bannedPlayerBuilder = playerRecordManager.getBannedPlayerBuilder();
				bannedPlayerBuilder.setPlayer(player);
				bannedPlayerBuilder.setCreator(sender.getName());
				bannedPlayerBuilder.setReason(reason.getString());
				if (time > 0) bannedPlayerBuilder.setExpiryTime(time);
				bannedPlayerBuilder.save();
				if (silent) messages.add(PLAYER_BANNED.asInfoMessage(player));
				BanHammerPlayerBannedEvent event = new BanHammerPlayerBannedEvent(bannedPlayerBuilder.getRecord(), silent);
				pluginManager.callEvent(event);
			}
		}
		sender.sendMessage(messages.toArray(new String[messages.size()]));
	}

	@Override
	public boolean isAsynchronousCommand() {
		return false;
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

	private boolean hasPermission(final CommandSender sender, String playerName) {
		boolean immunePlayerTargeted = this.immunePlayers.contains(playerName);
		if (sender.hasPermission(PERMISSION_ALL)) return true;
		if (!immunePlayerTargeted && sender.hasPermission(PERMISSION_PERMANENT)) return true;
		for (final String limitName : this.limits.keySet()) {
			final String node = PERMISSION_ALL + "." + limitName;
			if (time.getDuration() == 0) break;
			if (!sender.hasPermission(node)) continue;
			if (!immunePlayerTargeted && (this.limits.get(limitName) >= this.time.getDuration())) return true;
		}
		return false;
	}

}
