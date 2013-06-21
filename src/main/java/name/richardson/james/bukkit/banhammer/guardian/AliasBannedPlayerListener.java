package name.richardson.james.bukkit.banhammer.guardian;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.ResourceBundle;

import org.bukkit.plugin.Plugin;

import name.richardson.james.bukkit.alias.AliasHandler;
import name.richardson.james.bukkit.alias.persistence.PlayerNameRecord;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;
import name.richardson.james.bukkit.utilities.localisation.PluginResourceBundle;

public class AliasBannedPlayerListener extends BannedPlayerListener {

	private final AliasHandler aliasHandler;
	private final PlayerRecordManager playerRecordManager;
	private final BanHandler banHandler;
	private final ResourceBundle localisation = PluginResourceBundle.getBundle(this.getClass());

	public AliasBannedPlayerListener(Plugin plugin, PlayerRecordManager playerRecordManager, AliasHandler aliasHandler, BanHandler banHandler) {
		super(plugin, playerRecordManager);
		this.aliasHandler = aliasHandler;
		this.playerRecordManager = playerRecordManager;
		this.banHandler = banHandler;
	}

	@Override
	protected boolean isPlayerBanned(String playerName) {
		if (super.isPlayerBanned(playerName)) return true;
		Collection<PlayerNameRecord> alias = this.aliasHandler.getPlayersNames(playerName);
		for (PlayerNameRecord record : alias) {
			if (super.isPlayerBanned(record.getPlayerName())) {
				PlayerRecord playerRecord = this.playerRecordManager.find(record.getPlayerName());
				String reason = MessageFormat.format(this.localisation.getString("alias-ban-reason"), record.getPlayerName());
				banHandler.banPlayer(playerName, "AliasPlugin", reason, playerRecord.getActiveBan().getExpiresAt(), true);
				return true;
			}
		}
		return false;
	}

}
