package name.richardson.james.bukkit.banhammer;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.alias.AliasHandler;
import name.richardson.james.bukkit.alias.persistence.PlayerNameRecord;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.listener.AbstractLocalisedListener;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundles;
import name.richardson.james.bukkit.utilities.logging.PluginLogger;

public class AliasPlayerListener extends AbstractLocalisedListener {

	private final Map<String, SoftReference<PlayerRecord>> players = new HashMap<String, SoftReference<PlayerRecord>>();

	private final AliasHandler alias;

	private final EbeanServer database;

	private final BanHandler banhammer;

	private final static Logger LOGGER = PluginLogger.getLogger(AliasPlayerListener.class);

	public AliasPlayerListener(final BanHammer plugin) {
		super(plugin, ResourceBundles.MESSAGES);
		this.database = plugin.getDatabase();
		this.alias = plugin.getAliasHandler();
		this.banhammer = plugin.getHandler();
		this.setPlayerRecords();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerLogin(final AsyncPlayerPreLoginEvent event) {
		LOGGER.log(Level.FINEST, "Dealing with AsyncPlayerPreLoginEvent");
		this.checkForAlias(event.getName());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerLogin(final PlayerLoginEvent event) {
		if (Bukkit.getServer().getOnlineMode()) { return; }
		LOGGER.log(Level.FINEST, "Dealing with PlayerLoginEvent");
		this.checkForAlias(event.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
		final String reason = this.getMessage("misc.alias-ban-reason", "");
		if (event.getRecord().getReason().contains(reason)) {
			final String alias = event.getRecord().getReason().replaceAll(reason, "");
			this.alias.deassociatePlayer(event.getPlayerName(), alias);
		}
	}

	private void checkForAlias(final String playerName) {
		LOGGER.log(Level.FINE, "Checking for possible alias: {0}", playerName);
		final Collection<PlayerNameRecord> possibleMatches = this.alias.getPlayersNames(playerName);
		if (possibleMatches.isEmpty()) { return; }
		LOGGER.log(Level.FINER, "Possible matches: {0}", possibleMatches.size());
		for (final PlayerNameRecord record : possibleMatches) {
			final PlayerRecord alias = this.getPlayerRecord(record.getPlayerName());
			if (alias.isBanned()) {
				LOGGER.log(Level.FINE, "Match found: {0}. Bannning player.", alias.getName());
				this.banhammer.banPlayer(playerName, alias.getActiveBan(), this.getMessage("misc.alias-ban-reason", alias.getName()), false);
			}
		}
	}

	private PlayerRecord getPlayerRecord(final String playerName) {
		if (this.players.get(playerName) == null) {
			LOGGER.log(Level.FINEST, "Attempting to cache PlayerRecord for {0}", playerName);
			final PlayerRecord playerRecord = PlayerRecord.find(this.database, playerName);
			this.players.put(playerName, new SoftReference<PlayerRecord>(playerRecord));
		}
		return this.players.get(playerName).get();
	}

	private void setPlayerRecords() {
		final List<PlayerRecord> records = this.database.find(PlayerRecord.class).findList();
		for (final PlayerRecord record : records) {
			this.players.put(record.getName(), null);
		}
	}

}
