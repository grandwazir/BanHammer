package name.richardson.james.bukkit.banhammer.ban.event;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.listener.AbstractListener;
import name.richardson.james.bukkit.utilities.logging.PluginLoggerFactory;

import name.richardson.james.bukkit.alias.persistence.PlayerNameRecord;
import name.richardson.james.bukkit.alias.persistence.PlayerNameRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.ALIAS_BAN_REASON;

public final class AliasBannedPlayerListener extends AbstractListener {

	private static final String BAN_CREATOR_NAME = "AliasPlugin";
	private final Logger logger = PluginLoggerFactory.getLogger(AliasBannedPlayerListener.class);
	private final PlayerNameRecordManager playerNameRecordManager;
	private final PlayerRecordManager playerRecordManager;

	public AliasBannedPlayerListener(Plugin plugin, PluginManager pluginManager, PlayerRecordManager playerRecordManager, PlayerNameRecordManager playerNameRecordManager) {
		super(plugin, pluginManager);
		this.playerRecordManager = playerRecordManager;
		this.playerNameRecordManager = playerNameRecordManager;
	}

	public void createBanIfPlayerHasBannedAlias(String playerName) {
		PlayerNameRecord playerNameRecord = playerNameRecordManager.find(playerName);
		if (playerNameRecord == null) return;
		Set<String> aliases = playerNameRecord.getAliases();
		for (String alias : aliases) {
			if (!this.playerRecordManager.exists(alias)) continue;
			PlayerRecord record = this.playerRecordManager.find(alias);
			if (record.isBanned()) {
				logger.log(Level.FINER, "Found an alias for {0}.", alias);
				PlayerRecord playerRecord = this.playerRecordManager.find(alias);
				String reason = ALIAS_BAN_REASON.asMessage(alias);
				PlayerRecordManager.BannedPlayerBuilder builder = playerRecordManager.getBannedPlayerBuilder();
				builder.setPlayer(playerName);
				builder.setCreator(BAN_CREATOR_NAME);
				builder.setReason(reason);
				builder.setExpiresAt(playerRecord.getActiveBan().getExpiresAt());
				builder.save();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.KICK_BANNED) return;
		logger.log(Level.FINER, "Received " + event.getEventName());
		final String playerName = event.getName();
		createBanIfPlayerHasBannedAlias(playerName);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerPardoned(BanHammerPlayerPardonedEvent event) {
		String alias = event.getRecord().getReason().replace(ALIAS_BAN_REASON.asMessage(), "");
		PlayerNameRecord playerNameRecord = playerNameRecordManager.find(event.getPlayerName());
		PlayerNameRecord aliasPlayerNameRecord = playerNameRecordManager.find(alias);
		if (playerNameRecord != null && aliasPlayerNameRecord != null) {
			playerNameRecord.removeAssociation(aliasPlayerNameRecord);
			playerNameRecordManager.save(playerNameRecord);
		}
	}

}
