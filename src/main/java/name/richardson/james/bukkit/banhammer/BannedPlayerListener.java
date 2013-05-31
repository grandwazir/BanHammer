package name.richardson.james.bukkit.banhammer;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.ban.BanSummary;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord.State;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.listener.AbstractLocalisedListener;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundles;
import name.richardson.james.bukkit.utilities.logging.PluginLogger;

public class BannedPlayerListener extends AbstractLocalisedListener {

	public static final String NOTIFY_PERMISSION = "banhammer.notify";

	private final Map<String, SoftReference<BanRecord>> bannedPlayers = new HashMap<String, SoftReference<BanRecord>>();

	private final EbeanServer database;

	private final Server server;

	private final static Logger LOGGER = PluginLogger.getLogger(AliasPlayerListener.class);

	public BannedPlayerListener(final Plugin plugin) {
		super(plugin, ResourceBundles.MESSAGES);
		this.database = plugin.getDatabase();
		this.server = plugin.getServer();
		this.setBanRecords();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		this.bannedPlayers.put(event.getPlayerName(), new SoftReference<BanRecord>(event.getRecord()));
		this.broadcast(event);
		final Player player = this.server.getPlayerExact(event.getPlayerName());
		if (!event.isKicked() && (player != null)) {
			player.kickPlayer(this.getBanMessage(event.getRecord()));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLogin(final AsyncPlayerPreLoginEvent event) {
		LOGGER.log(Level.FINEST, "Dealing with AsyncPlayerPreLoginEvent");
		if (this.isBanned(event.getName())) {
			final BanRecord ban = this.getBan(event.getName());
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, this.getBanMessage(ban));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLogin(final PlayerLoginEvent event) {
		if (Bukkit.getServer().getOnlineMode()) { return; }
		LOGGER.log(Level.FINEST, "Dealing with PlayerLoginEvent");
		if (this.isBanned(event.getPlayer().getName())) {
			final BanRecord ban = this.getBan(event.getPlayer().getName());
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, this.getBanMessage(ban));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
		this.bannedPlayers.remove(event.getPlayerName());
		this.broadcast(event);
	}

	private void broadcast(final BanHammerPlayerBannedEvent event) {
		if (event.isSilent()) { return; }
		final BanSummary summary = new BanSummary(event.getRecord());
		this.server.broadcast(this.getMessage("notice.ban-broadcast", event.getPlayerName(), event.getRecord().getCreator().getName()), NOTIFY_PERMISSION);
		this.server.broadcast(summary.getReason(), NOTIFY_PERMISSION);
		this.server.broadcast(summary.getLength(), NOTIFY_PERMISSION);
	}

	private void broadcast(final BanHammerPlayerPardonedEvent event) {
		if (event.isSilent()) { return; }
		this.server.broadcast(this.getMessage("notice.pardon-broadcast", event.getPlayerName()), NOTIFY_PERMISSION);
	}

	private BanRecord getBan(final String playerName) {
		if (this.bannedPlayers.get(playerName) == null) {
			LOGGER.log(Level.FINEST, "Attempting to cache BanRecord for {0}", playerName);
			final PlayerRecord playerRecord = PlayerRecord.find(this.database, playerName);
			this.bannedPlayers.put(playerName, new SoftReference<BanRecord>(playerRecord.getActiveBan()));
		}
		return this.bannedPlayers.get(playerName).get();
	}

	private String getBanMessage(final BanRecord ban) {
		switch (ban.getType()) {
			case TEMPORARY:
				return this.getMessage("ui.temporarily-banned", ban.getReason(), BanHammer.LONG_DATE_FORMAT.format(ban.getExpiresAt()));
			default:
				return this.getMessage("ui.permanently-banned", ban.getReason());
		}
	}

	private boolean isBanned(final String playerName) {
		LOGGER.log(Level.FINE, "Checking to see if player is banned: {0}", playerName);
		if (this.bannedPlayers.containsKey(playerName)) {
			final BanRecord ban = this.getBan(playerName);
			LOGGER.log(Level.FINER, "Checking to see if ban has expired.");
			if (ban.hasExpired() && (ban.getState() == State.NORMAL)) {
				LOGGER.log(Level.FINEST, "Ban is present but has expired.");
				ban.setState(State.EXPIRED);
				this.database.update(ban);
				this.bannedPlayers.remove(playerName);
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	private void setBanRecords() {
		final List<PlayerRecord> records = this.database.find(PlayerRecord.class).findList();
		for (final PlayerRecord record : records) {
			if (record.isBanned()) {
				this.bannedPlayers.put(record.getName(), null);
			}
		}
	}

}
