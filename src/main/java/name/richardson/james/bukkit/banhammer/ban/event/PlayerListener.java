package name.richardson.james.bukkit.banhammer.ban.event;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.formatters.time.ApproximateTimeFormatter;
import name.richardson.james.bukkit.utilities.formatters.time.TimeFormatter;
import name.richardson.james.bukkit.utilities.listener.AbstractListener;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerMessages.LISTENER_PLAYER_BANNED_PERMANENTLY;
import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerMessages.LISTENER_PLAYER_BANNED_TEMPORARILY;


public final class PlayerListener extends AbstractListener {

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
		switch (record.getType()) {
			case TEMPORARY: {
				String time = TIME_FORMATTER.getHumanReadableDuration(record.getExpiresAt().getTime());
				return LISTENER_PLAYER_BANNED_TEMPORARILY.asErrorMessage(record.getReason(), record.getCreator().getLastKnownName(), time);
			}
			default: {
				return LISTENER_PLAYER_BANNED_PERMANENTLY.asErrorMessage(record.getReason(), record.getCreator().getLastKnownName());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		final KickPlayerTask kickPlayerTask = new KickPlayerTask(server, event.getRecords());
		server.getScheduler().runTask(plugin, kickPlayerTask);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLogin(final PlayerLoginEvent event) {
		final Player player = event.getPlayer();
		if (!server.getOnlineMode()) return;
		if (event.getResult() == PlayerLoginEvent.Result.KICK_BANNED) return;
		PlayerRecord playerRecord = PlayerRecord.find(database, player.getUniqueId());
		if (playerRecord != null) {
			if (playerRecord.isBanned()) {
				final BanRecord ban = playerRecord.getActiveBan();
				final String message = getKickMessage(ban);
				event.disallow(PlayerLoginEvent.Result.KICK_BANNED, message);
			}
			if (playerRecord.getLastKnownName().equalsIgnoreCase(player.getName())) {
				playerRecord.setLastKnownName(player.getName());
				PlayerRecord.save(database, playerRecord);
			}
		}
	}

}
