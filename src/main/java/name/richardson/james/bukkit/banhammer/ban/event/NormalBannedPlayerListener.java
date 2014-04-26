package name.richardson.james.bukkit.banhammer.ban.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.formatters.time.ApproximateTimeFormatter;
import name.richardson.james.bukkit.utilities.formatters.time.TimeFormatter;
import name.richardson.james.bukkit.utilities.listener.AbstractListener;
import name.richardson.james.bukkit.utilities.logging.PluginLoggerFactory;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.LISTENER_PLAYER_BANNED_PERMANENTLY;
import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.LISTENER_PLAYER_BANNED_TEMPORARILY;


public final class NormalBannedPlayerListener extends AbstractListener {

	private final Logger logger = PluginLoggerFactory.getLogger(NormalBannedPlayerListener.class);
	private final PlayerRecordManager playerRecordManager;
	private final Server server;
	private final TimeFormatter timeFormatter = new ApproximateTimeFormatter();

	public NormalBannedPlayerListener(Plugin plugin, PluginManager pluginManager, Server server, PlayerRecordManager playerRecordManager) {
		super(plugin, pluginManager);
		this.server = server;
		this.playerRecordManager = playerRecordManager;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		logger.log(Level.FINER, "Received " + event.getEventName());
		final Player player = this.server.getPlayerExact(event.getPlayerName());
		if (player != null && player.isOnline()) {
			player.kickPlayer(this.getKickMessage(event.getRecord()));
		}
  }

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLogin(final PlayerLoginEvent event) {
		if (server.getOnlineMode()) return;
		if (event.getResult() == PlayerLoginEvent.Result.KICK_BANNED) return;
		logger.log(Level.FINER, "Received " + event.getEventName());
		final String playerName = event.getPlayer().getName();
		if (this.isPlayerBanned(playerName)) {
			final BanRecord record = this.playerRecordManager.find(playerName).getActiveBan();
			final String message = this.getKickMessage(record);
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, message);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLogin(final AsyncPlayerPreLoginEvent event) {
		if (!server.getOnlineMode()) return;
		if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.KICK_BANNED) return;
		logger.log(Level.FINER, "Received " + event.getEventName());
		final String playerName = event.getName();
		if (this.isPlayerBanned(playerName)) {
			final BanRecord record = this.playerRecordManager.find(playerName).getActiveBan();
			final String message = this.getKickMessage(record);
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message);
		}
	}

	private String getKickMessage(BanRecord record) {
		switch (record.getType()) {
			case TEMPORARY: {
				String time = timeFormatter.getHumanReadableDuration(record.getExpiresAt().getTime());
				return LISTENER_PLAYER_BANNED_TEMPORARILY.asErrorMessage(record.getReason(), record.getCreator().getName(), time);
			}
			default: {
				return LISTENER_PLAYER_BANNED_PERMANENTLY.asErrorMessage(record.getReason(), record.getCreator().getName());
			}
		}
	}

	private boolean isPlayerBanned(String playerName) {
		logger.log(Level.FINER, "Checking if {0} is banned.", playerName);
		if (!this.playerRecordManager.exists(playerName)) return false;
		PlayerRecord record = this.playerRecordManager.find(playerName);
		return record.isBanned();
	}

}
