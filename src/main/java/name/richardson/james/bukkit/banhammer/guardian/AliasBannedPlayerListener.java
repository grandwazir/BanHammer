package name.richardson.james.bukkit.banhammer.guardian;

import name.richardson.james.bukkit.alias.AliasHandler;
import name.richardson.james.bukkit.alias.persistence.PlayerNameRecord;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;
import org.bukkit.plugin.Plugin;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.ResourceBundle;

public class AliasBannedPlayerListener extends BannedPlayerListener {

	private final AliasHandler hander;
	private final PlayerRecordManager playerRecordManager;
	private final BanHandler handler;
	private final ResourceBundle localisation;

	public AliasBannedPlayerListener(Plugin plugin, PlayerRecordManager playerRecordManager, AliasHandler aliasHandler, BanHandler handler) {
		super(plugin, playerRecordManager);
		this.hander = aliasHandler;
		this.playerRecordManager = playerRecordManager;
		this.handler = handler;
	}

	@Override
	protected boolean isPlayerBanned(String playerName) {
		if (super.isPlayerBanned(playerName)) return true;
		Collection<PlayerNameRecord> alias = this.hander.getPlayersNames(playerName);
		for (PlayerNameRecord record : alias) {
			if (super.isPlayerBanned(record.getPlayerName())) {
				PlayerRecord playerRecord = this.playerRecordManager.find(record.getPlayerName());
				String reason = MessageFormat.format(this.localisation.getString("alias-ban-reason"), record.getPlayerName());
				handler.banPlayer(playerName, "AliasPlugin", reason, playerRecord.getActiveBan().getExpiresAt(), true);
				return true;
			}
		}
		return false;
	}



}
