package name.richardson.james.bukkit.banhammer.ban.event;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.formatters.ApproximateTimeFormatter;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.DefaultColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;
import name.richardson.james.bukkit.utilities.listener.AbstractListener;
import name.richardson.james.bukkit.utilities.localisation.Localisation;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundleByClassLocalisation;
import name.richardson.james.bukkit.utilities.logging.PluginLoggerFactory;

import name.richardson.james.bukkit.alias.persistence.PlayerNameRecord;
import name.richardson.james.bukkit.alias.persistence.PlayerNameRecordManager;
import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

public final class AliasBannedPlayerListener extends AbstractListener {

	private static final String ALIAS_BAN_REASON_KEY = "alias-ban-reason";
	private static final String BAN_CREATOR_NAME = "AliasPlugin";
	private static final String BANNED_TEMPORARILY_KEY = "banned-temporarily";
	private static final String BANNED_PERMANENTLY_KEY = "banned-permanently";

	private final Logger logger = PluginLoggerFactory.getLogger(AliasBannedPlayerListener.class);
	private final ColourFormatter colourFormatter = new DefaultColourFormatter();
	private final Localisation localisation = new ResourceBundleByClassLocalisation(AliasBannedPlayerListener.class);
	private final PlayerNameRecordManager playerNameRecordManager;
	private final PlayerRecordManager playerRecordManager;
	private final TimeFormatter timeFormatter = new ApproximateTimeFormatter();

	public AliasBannedPlayerListener(Plugin plugin, PluginManager pluginManager, PlayerRecordManager playerRecordManager, PlayerNameRecordManager playerNameRecordManager) {
		super(plugin, pluginManager);
		this.playerRecordManager = playerRecordManager;
		this.playerNameRecordManager = playerNameRecordManager;
	}

	public boolean isPlayerBanned(String playerName) {
		PlayerNameRecord playerNameRecord = playerNameRecordManager.find(playerName);
		if (playerNameRecord == null) return false;
		Set<String> aliases = playerNameRecord.getAliases();
		for (String alias : aliases) {
			if (!this.playerRecordManager.exists(alias)) continue;
			PlayerRecord record = this.playerRecordManager.find(alias);
			if (record.isBanned()) {
				logger.log(Level.FINER, "Found an alias for {0}.", alias);
				PlayerRecord playerRecord = this.playerRecordManager.find(alias);
				String reason = localisation.getMessage(ALIAS_BAN_REASON_KEY, alias);
				PlayerRecordManager.BannedPlayerBuilder builder = playerRecordManager.getBannedPlayerBuilder();
				builder.setPlayer(playerName);
				builder.setCreator(BAN_CREATOR_NAME);
				builder.setReason(reason);
				builder.setExpiresAt(playerRecord.getActiveBan().getExpiresAt());
				builder.save();
				return true;
			}
		}
		return false;
	}

	public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.KICK_BANNED) return;
		logger.log(Level.FINER, "Received " + event.getEventName());
		final String playerName = event.getName();
		if (this.isPlayerBanned(playerName)) {
			final BanRecord record = this.playerRecordManager.find(playerName).getActiveBan();
			final String message = this.getKickMessage(record);
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message);
		}
	}

	private final String getKickMessage(BanRecord record) {
		switch (record.getType()) {
			case TEMPORARY: {
				String time = timeFormatter.getHumanReadableDuration(record.getExpiresAt().getTime());
				return colourFormatter.format(localisation.getMessage(BANNED_TEMPORARILY_KEY), ColourFormatter.FormatStyle.ERROR, record.getReason(), record.getCreator().getName(), time);
			}
			default: {
				return colourFormatter.format(localisation.getMessage(BANNED_PERMANENTLY_KEY), ColourFormatter.FormatStyle.ERROR, record.getReason(), record.getCreator().getName());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerPardoned(BanHammerPlayerPardonedEvent event) {
		String alias = event.getRecord().getReason().replace(localisation.getMessage(ALIAS_BAN_REASON_KEY), "");
		PlayerNameRecord playerNameRecord = playerNameRecordManager.find(event.getPlayerName());
		PlayerNameRecord aliasPlayerNameRecord = playerNameRecordManager.find(alias);
		if (playerNameRecord != null && aliasPlayerNameRecord != null) {
			playerNameRecord.removeAssociation(aliasPlayerNameRecord);
			playerNameRecordManager.save(playerNameRecord);
		}
	}

}
