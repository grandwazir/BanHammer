package name.richardson.james.bukkit.banhammer;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import name.richardson.james.bukkit.utilities.listener.AbstractListener;
import name.richardson.james.bukkit.utilities.localisation.LocalisedCommandSender;
import name.richardson.james.bukkit.utilities.localisation.PluginResourceBundle;
import name.richardson.james.bukkit.utilities.logging.LocalisedLogger;

import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.ban.BanSummary;

public class PlayerNotifier extends AbstractListener {

	private static final Logger LOGGER = LocalisedLogger.getLogger(PlayerNotifier.class, null);
	private final ResourceBundle localisation = PluginResourceBundle.getBundle(this.getClass());
	private final Server server;

	public PlayerNotifier(final Plugin plugin, final Server server) {
		super(plugin);
		this.server = server;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		LOGGER.log(Level.FINEST, "Received " + event.getEventName());
		BanSummary banSummary = new BanSummary(event.getRecord());
		for (Player player : server.getOnlinePlayers()) {
			if (!player.hasPermission("banhammer.notify")) continue;
			player.sendMessage(banSummary.getAnnouncementHeader());
			player.sendMessage(banSummary.getReason());
			player.sendMessage(banSummary.getLength());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
		LOGGER.log(Level.FINEST, "Received " + event.getEventName());
		for (Player player : server.getOnlinePlayers()) {
			if (!player.hasPermission("banhammer.notify")) continue;
			LocalisedCommandSender localisedCommandSender = new LocalisedCommandSender(player, localisation);
			localisedCommandSender.info("player-pardoned", event.getPlayerName());
		}
	}

}
