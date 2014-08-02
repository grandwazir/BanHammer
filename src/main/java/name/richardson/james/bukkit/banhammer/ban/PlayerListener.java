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
	private final Plugin plugin;
	private final Server server;

	public PlayerListener(Plugin plugin, PluginManager pluginManager, Server server, EbeanServer database) {
		super(plugin, pluginManager);
		this.plugin = plugin;
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
		// TODO: Implement Sync task to ban people when banned from Async command
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLogin(final PlayerLoginEvent event) {
		final Player player = event.getPlayer();
		if (!server.getOnlineMode()) return;
		if (event.getResult() == PlayerLoginEvent.Result.KICK_BANNED) return;
		PlayerRecord playerRecord = PlayerRecord.find(player.getUniqueId());
		if (playerRecord != null) {
			if (playerRecord.isBanned()) {
				final BanRecord ban = playerRecord.getActiveBan();
				final String message = getKickMessage(ban);
				event.disallow(PlayerLoginEvent.Result.KICK_BANNED, message);
			}
			if (!playerRecord.getName().equalsIgnoreCase(player.getName())) {
				playerRecord.setName(player.getName());
				database.save(playerRecord);
			}
		}
	}

}
