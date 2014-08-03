/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 PlayerListener.java is part of BanHammer.

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
package name.richardson.james.bukkit.banhammer.ban;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.listener.AbstractListener;
import name.richardson.james.bukkit.utilities.time.ApproximateTimeFormatter;
import name.richardson.james.bukkit.utilities.time.TimeFormatter;

import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.PlayerRecord;

public final class PlayerListener extends AbstractListener {

	private static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	private static final TimeFormatter TIME_FORMATTER = new ApproximateTimeFormatter();
	private final EbeanServer database;
	private final Server server;

	public PlayerListener(Plugin plugin, PluginManager pluginManager, Server server, EbeanServer database) {
		super(plugin, pluginManager);
		this.server = server;
		this.database = database;
	}

	protected static String getKickMessage(BanRecord record) {
		String message = "";
		switch (record.getType()) {
			case PERMANENT:
				message = MESSAGES.playerBannedPermanently(record.getReason().getComment(), record.getCreator().getName());
				break;
			case TEMPORARY: {
				String time = TIME_FORMATTER.getHumanReadableDuration(record.getExpiresAt().getTime());
				message = MESSAGES.playerBannedTemporarily(record.getReason().getComment(), record.getCreator().getName(), time);
				break;
			}
		}
		return message;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		for (BanRecord ban : event.getRecords()) {
			String message = getKickMessage(ban);
			Player player = server.getPlayer(ban.getPlayer().getId());
			if (player != null && player.isOnline()) player.kickPlayer(message);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLogin(final PlayerLoginEvent event) {
		Player player = event.getPlayer();
		if (!server.getOnlineMode()) return;
		if (event.getResult() == PlayerLoginEvent.Result.KICK_BANNED) return;
		PlayerRecord playerRecord = PlayerRecord.find(player.getUniqueId());
		if (playerRecord != null) {
			if (playerRecord.isBanned()) {
				BanRecord ban = playerRecord.getActiveBan();
				String message = getKickMessage(ban);
				event.disallow(PlayerLoginEvent.Result.KICK_BANNED, message);
			}
			if (!playerRecord.getName().equalsIgnoreCase(player.getName())) {
				playerRecord.setName(player.getName());
				database.save(playerRecord);
			}
		}
	}

}
