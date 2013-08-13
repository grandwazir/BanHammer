package name.richardson.james.bukkit.banhammer.ban.event;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.logging.PrefixedLogger;

import name.richardson.james.bukkit.alias.persistence.PlayerNameRecord;
import name.richardson.james.bukkit.alias.persistence.PlayerNameRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

public class AliasBannedPlayerListener extends BannedPlayerListener {

	public static final String BAN_AS = "AliasPlugin";

	private static final Logger LOGGER = PrefixedLogger.getLogger(AliasBannedPlayerListener.class);

	private final PlayerRecordManager playerRecordManager;
	private final PlayerNameRecordManager playerNameRecordManager;

	public AliasBannedPlayerListener(Plugin plugin, PluginManager pluginManager, Server server, PlayerRecordManager playerRecordManager, PlayerNameRecordManager playerNameRecordManager) {
		super(plugin, pluginManager, server, playerRecordManager);
		this.playerRecordManager = playerRecordManager;
		this.playerNameRecordManager = playerNameRecordManager;
	}

	@Override
	protected boolean isPlayerBanned(String playerName) {
		if (super.isPlayerBanned(playerName)) return true;
		PlayerNameRecord playerNameRecord = playerNameRecordManager.find(playerName);
		if (playerNameRecord == null) return false;
		Set<String> aliases = playerNameRecord.getAliases();
		for (String alias : aliases) {
			if (super.isPlayerBanned(alias)) {
				LOGGER.log(Level.FINER, "Found an alias for {0}.", alias);
				PlayerRecord playerRecord = this.playerRecordManager.find(alias);
				String reason = getMessage("alias-ban-reason", alias);
				playerRecordManager.new BannedPlayerBuilder().setPlayer(playerName).setCreator(BAN_AS).setReason(reason).setExpiresAt(playerRecord.getActiveBan().getExpiresAt()).save();
				return true;
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerPardoned(BanHammerPlayerPardonedEvent event) {
		String alias = event.getRecord().getReason().replace(getMessage("alias-ban-reason"), "");
		PlayerNameRecord playerNameRecord = playerNameRecordManager.find(event.getPlayerName());
		PlayerNameRecord aliasPlayerNameRecord = playerNameRecordManager.find(alias);
		if (playerNameRecord != null && aliasPlayerNameRecord != null) {
			playerNameRecord.removeAssociation(aliasPlayerNameRecord);
			playerNameRecordManager.save(playerNameRecord);
		}
	}

}
