package name.richardson.james.bukkit.banhammer.ban.event;

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
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.formatters.colours.CoreColourScheme;
import name.richardson.james.bukkit.utilities.formatters.localisation.Localised;
import name.richardson.james.bukkit.utilities.formatters.localisation.ResourceBundles;
import name.richardson.james.bukkit.utilities.listener.AbstractListener;
import name.richardson.james.bukkit.utilities.logging.PrefixedLogger;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

public class BannedPlayerListener extends AbstractListener implements Localised {

	private final ColourScheme COLOUR_SCHEME = new CoreColourScheme();
	private final Logger LOGGER = PrefixedLogger.getLogger(BannedPlayerListener.class);
	private final ResourceBundle MESSAGES_RESOURCE_BUNDLE = ResourceBundles.MESSAGES.getBundle();
	private final PlayerRecordManager playerRecordManager;
	private final Server server;

	public BannedPlayerListener(Plugin plugin, PluginManager pluginManager, Server server, PlayerRecordManager playerRecordManager) {
		super(plugin, pluginManager);
		this.server = server;
		this.playerRecordManager = playerRecordManager;
	}

	public final String getColouredMessage(ColourScheme.Style style, String key, Object... arguments) {
		String message = getResourceBundle().getString(key);
		return getColourScheme().format(style, message, arguments);
	}

	public final ColourScheme getColourScheme() {
		return COLOUR_SCHEME;
	}

	@Override
	public final ResourceBundle getResourceBundle() {
		return MESSAGES_RESOURCE_BUNDLE;
	}

	@Override
	public final String getMessage(String key, Object... arguments) {
		return MessageFormat.format(MESSAGES_RESOURCE_BUNDLE.getString(key), arguments);
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
				return getColouredMessage(ColourScheme.Style.ERROR, "banned-temporarily", record.getReason(), record.getCreator().getName(), record.getExpiresAt());
			}
			default: {
				return getColouredMessage(ColourScheme.Style.ERROR, "banned-permanently", record.getReason(), record.getCreator().getName());
			}
		}
	}

	protected boolean isPlayerBanned(String playerName) {
		if (!this.playerRecordManager.exists(playerName)) return false;
		PlayerRecord record = this.playerRecordManager.find(playerName);
		return record.isBanned();
	}

}
