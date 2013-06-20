package name.richardson.james.bukkit.banhammer.guardian;

import java.lang.ref.SoftReference;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.alias.AliasHandler;
import name.richardson.james.bukkit.alias.persistence.PlayerNameRecord;
import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.listener.AbstractLocalisedListener;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundles;
import name.richardson.james.bukkit.utilities.logging.PluginLogger;
import org.bukkit.plugin.Plugin;

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
				handler.banPlayer(playerName, playerRecord.getActiveBan(), reason, true);
				return true;
			}
		}
		return false;
	}



}
