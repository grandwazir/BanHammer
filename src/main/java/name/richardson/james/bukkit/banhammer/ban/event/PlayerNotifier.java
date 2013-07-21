package name.richardson.james.bukkit.banhammer.ban.event;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.listener.AbstractListener;
import name.richardson.james.bukkit.utilities.localisation.LocalisedCoreColourScheme;
import name.richardson.james.bukkit.utilities.localisation.PluginResourceBundle;
import name.richardson.james.bukkit.utilities.logging.PrefixedLogger;

public class PlayerNotifier extends AbstractListener {

	public static final String NOTIFY_PERMISSION_NAME = "banhammer.notify";

	private static final Logger logger = PrefixedLogger.getLogger(PlayerNotifier.class);
	private static final ResourceBundle localisation = PluginResourceBundle.getBundle(PlayerNotifier.class);

	private final ColourScheme colourScheme = new LocalisedCoreColourScheme(localisation);
	private final Server server;

	public PlayerNotifier(final Plugin plugin, final PluginManager pluginManager, final Server server) {
		super(plugin, pluginManager);
		this.server = server;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		logger.log(Level.FINEST, "Received " + event.getEventName());
		if (event.isSilent()) return;
		BanSummary banSummary = new BanSummary(event.getRecord());
		for (Player player : server.getOnlinePlayers()) {
			if (!player.hasPermission(NOTIFY_PERMISSION_NAME)) continue;
			logger.log(Level.FINER, "Notifying " + player.getName());
			player.sendMessage(banSummary.getAnnouncementHeader());
			player.sendMessage(banSummary.getReason());
			player.sendMessage(banSummary.getLength());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
		logger.log(Level.FINEST, "Received " + event.getEventName());
		if (event.isSilent()) return;
		for (Player player : server.getOnlinePlayers()) {
			if (!player.hasPermission(NOTIFY_PERMISSION_NAME)) continue;
			logger.log(Level.FINER, "Notifying " + player.getName());
			String message = colourScheme.format(ColourScheme.Style.INFO, "player-pardoned", event.getPlayerName());
			player.sendMessage(message);
		}
	}

}
