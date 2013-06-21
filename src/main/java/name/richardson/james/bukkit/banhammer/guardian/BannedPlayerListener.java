package name.richardson.james.bukkit.banhammer.guardian;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;

import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.listener.AbstractListener;
import name.richardson.james.bukkit.utilities.localisation.PluginResourceBundle;
import name.richardson.james.bukkit.utilities.logging.LocalisedLogger;

public class BannedPlayerListener extends AbstractListener {

	private static final Logger LOGGER = LocalisedLogger.getLogger(BannedPlayerListener.class);

	private final ResourceBundle localisation = PluginResourceBundle.getBundle(this.getClass());
	private final PlayerRecordManager playerRecordManager;
	private final Server server;

	public BannedPlayerListener(final Plugin plugin, final PlayerRecordManager playerRecordManager) {
		super(plugin);
		this.server = plugin.getServer();
		this.playerRecordManager = playerRecordManager;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		LOGGER.log(Level.FINEST, "Received " + event.getEventName());
		final Player player = this.server.getPlayerExact(event.getPlayerName());
		if (player != null && player.isOnline()) {
			player.kickPlayer(this.getKickMessage(event.getRecord()));
		}
  }

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLogin(final AsyncPlayerPreLoginEvent event) {
		LOGGER.log(Level.FINEST, "Received " + event.getEventName());
		final String playerName = event.getName();
		if (this.isPlayerBanned(playerName)) {
			final BanRecord record = this.playerRecordManager.find(playerName).getActiveBan();
			final String message = this.getKickMessage(record);
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLogin(final PlayerLoginEvent event) {
		LOGGER.log(Level.FINEST, "Received " + event.getEventName());
		final String playerName = event.getPlayer().getName();
		if (this.isPlayerBanned(playerName)) {
			final BanRecord record = this.playerRecordManager.find(playerName).getActiveBan();
			final String message = this.getKickMessage(record);
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, message);
		}
	}

	protected String getKickMessage(BanRecord record) {
		switch (record.getType()) {
			case TEMPORARY: {
				Object[] params = {record.getReason(), record.getCreator().getName(), record.getExpiresAt()};
				String message = ColourFormatter.error(localisation.getString("banned-temporarily"));
				return MessageFormat.format(message, params);
			}
			default: {
				Object[] params = {record.getReason(), record.getCreator().getName()};
				String message = ColourFormatter.error(localisation.getString("banned-permanently"));
				return MessageFormat.format(message, params);
			}
		}
	}

	protected boolean isPlayerBanned(String playerName) {
		if (!this.playerRecordManager.exists(playerName)) return false;
		PlayerRecord record = this.playerRecordManager.find(playerName);
		return record.isBanned();
	}

}
