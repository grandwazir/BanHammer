package name.richardson.james.bukkit.banhammer.ban.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.listener.AbstractListener;
import name.richardson.james.bukkit.utilities.localisation.FormattedLocalisation;
import name.richardson.james.bukkit.utilities.localisation.StrictResourceBundleLocalisation;
import name.richardson.james.bukkit.utilities.logging.PluginLoggerFactory;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation;

public class PlayerNotifier extends AbstractListener {

	private final FormattedLocalisation localisation = new StrictResourceBundleLocalisation();
	private final Logger logger = PluginLoggerFactory.getLogger(PlayerNotifier.class);
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
		String message = localisation.formatAsErrorMessage(BanHammerLocalisation.NOTIFY_PLAYER_BANNED, event.getPlayerName(), event.getRecord().getCreator().getName());
		server.broadcast(message, BanHammer.NOTIFY_PERMISSION_NAME);
		server.broadcast(formatter.getReason(), BanHammer.NOTIFY_PERMISSION_NAME);
		server.broadcast(formatter.getLength(), BanHammer.NOTIFY_PERMISSION_NAME);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
		logger.log(Level.FINER, "Received " + event.getEventName());
		if (event.isSilent()) return;
		String message = localisation.formatAsInfoMessage(BanHammerLocalisation.NOTIFY_PLAYER_PARDONED, event.getPlayerName(), event.getSender().getName());
		server.broadcast(message, BanHammer.NOTIFY_PERMISSION_NAME);
	}

}
