package name.richardson.james.bukkit.banhammer.ban.event;

import java.util.Collection;

import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.alias.AliasHandler;
import name.richardson.james.bukkit.alias.persistence.PlayerNameRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

public class AliasBannedPlayerListener extends BannedPlayerListener {

	private final AliasHandler aliasHandler;
	private final PlayerRecordManager playerRecordManager;

	public AliasBannedPlayerListener(Plugin plugin, PluginManager pluginManager, Server server, PlayerRecordManager playerRecordManager, AliasHandler aliasHandler) {
		super(plugin, pluginManager, server, playerRecordManager);
		this.playerRecordManager = playerRecordManager;
		this.aliasHandler = aliasHandler;
	}

	@Override
	protected boolean isPlayerBanned(String playerName) {
		if (super.isPlayerBanned(playerName)) return true;
		Collection<PlayerNameRecord> alias = this.aliasHandler.getPlayersNames(playerName);
		for (PlayerNameRecord record : alias) {
			if (super.isPlayerBanned(record.getPlayerName())) {
				PlayerRecord playerRecord = this.playerRecordManager.find(record.getPlayerName());
				String reason = getMessage("alias-ban-reason", record.getPlayerName());
				playerRecordManager.new BannedPlayerBuilder().setPlayer(playerName).setCreator("AliasPlugin").setReason(reason).setExpiresAt(playerRecord.getActiveBan().getExpiresAt()).save();
				return true;
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerPardoned(BanHammerPlayerPardonedEvent event) {
		String alias = event.getRecord().getReason().replace(getMessage("alias-ban-reason"), "");
		this.aliasHandler.deassociatePlayer(event.getPlayerName(), alias);
	}

}
