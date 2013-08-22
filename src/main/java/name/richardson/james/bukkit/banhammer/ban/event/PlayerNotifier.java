package name.richardson.james.bukkit.banhammer.ban.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.DefaultColourFormatter;
import name.richardson.james.bukkit.utilities.listener.AbstractListener;
import name.richardson.james.bukkit.utilities.localisation.Localisation;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundleByClassLocalisation;
import name.richardson.james.bukkit.utilities.logging.PluginLoggerFactory;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.ban.BanRecord;

public class PlayerNotifier extends AbstractListener {

	private static final String PLAYER_BANNED_BY_KEY = "player-banned-by";
	private static final String PLAYER_PARDONED_KEY = "player-pardoned-by";

	private final Logger logger = PluginLoggerFactory.getLogger(PlayerNotifier.class);
	private final ColourFormatter colourFormatter = new DefaultColourFormatter();
	private final Localisation localisation = new ResourceBundleByClassLocalisation(PlayerNotifier.class);
	private final Server server;

	public PlayerNotifier(final Plugin plugin, final PluginManager pluginManager, final Server server) {
		super(plugin, pluginManager);
		this.server = server;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		logger.log(Level.FINER, "Received " + event.getEventName());
		if (event.isSilent()) return;
		BanRecord.BanRecordFormatter formatter = event.getRecord().getFormatter();
		server.broadcast(colourFormatter.format(localisation.getMessage(PLAYER_BANNED_BY_KEY), ColourFormatter.FormatStyle.ERROR, event.getPlayerName(), event.getRecord().getCreator().getName()), BanHammer.NOTIFY_PERMISSION_NAME);
		server.broadcast(formatter.getReason(), BanHammer.NOTIFY_PERMISSION_NAME);
		server.broadcast(formatter.getLength(), BanHammer.NOTIFY_PERMISSION_NAME);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
		logger.log(Level.FINER, "Received " + event.getEventName());
		if (event.isSilent()) return;
		server.broadcast(colourFormatter.format(localisation.getMessage(PLAYER_PARDONED_KEY), ColourFormatter.FormatStyle.INFO, event.getPlayerName(), event.getSender().getName()), BanHammer.NOTIFY_PERMISSION_NAME);
	}

}
