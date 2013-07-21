package name.richardson.james.bukkit.banhammer.ban.event;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.formatters.colours.CoreColourScheme;
import name.richardson.james.bukkit.utilities.formatters.localisation.Localised;
import name.richardson.james.bukkit.utilities.formatters.localisation.ResourceBundles;
import name.richardson.james.bukkit.utilities.listener.AbstractListener;
import name.richardson.james.bukkit.utilities.logging.PrefixedLogger;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.ban.BanRecordFormatter;

public class PlayerNotifier extends AbstractListener implements Localised {

	private final Logger logger = PrefixedLogger.getLogger(PlayerNotifier.class);
	private final ColourScheme colourScheme = new CoreColourScheme();
	private final ResourceBundle resourceBundle = ResourceBundles.MESSAGES.getBundle();
	private final Server server;

	public PlayerNotifier(final Plugin plugin, final PluginManager pluginManager, final Server server) {
		super(plugin, pluginManager);
		this.server = server;
	}
	
	public final String getColouredMessage(ColourScheme.Style style, String key, Object... arguments) {
		String message = getResourceBundle().getString(key);
		return getColourScheme().format(style, message, arguments);
	}

	public final ColourScheme getColourScheme() {
		return colourScheme;
	}

	@Override
	public final ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	@Override
	public final String getMessage(String key, Object... arguments) {
		return MessageFormat.format(resourceBundle.getString(key), arguments);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		logger.log(Level.FINEST, "Received " + event.getEventName());
		if (event.isSilent()) return;
		BanRecordFormatter formatter = new BanRecordFormatter(event.getRecord());
		server.broadcast(formatter.getHeader(), BanHammer.NOTIFY_PERMISSION_NAME);
		server.broadcast(formatter.getReason(), BanHammer.NOTIFY_PERMISSION_NAME);
		server.broadcast(formatter.getLength(), BanHammer.NOTIFY_PERMISSION_NAME);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
		logger.log(Level.FINEST, "Received " + event.getEventName());
		if (event.isSilent()) return;
		server.broadcast(getColouredMessage(ColourScheme.Style.INFO, "player-pardoned", event.getPlayerName()), BanHammer.NOTIFY_PERMISSION_NAME);
	}

}
