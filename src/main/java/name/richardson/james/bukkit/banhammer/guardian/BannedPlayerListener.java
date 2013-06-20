package name.richardson.james.bukkit.banhammer.guardian;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;
import name.richardson.james.bukkit.utilities.listener.AbstractListener;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;

import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.logging.PluginLogger;

public class BannedPlayerListener extends AbstractListener {

	private final static Logger LOGGER = PluginLogger.getLogger(AliasBannedPlayerListener.class);

	private final Server server;

	private final ResourceBundle LOCALISATION;

	private final PlayerRecordManager playerRecordManager;

	public BannedPlayerListener(final Plugin plugin, final PlayerRecordManager playerRecordManager) {
		super(plugin);
		this.server = plugin.getServer();
		this.playerRecordManager = playerRecordManager;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		final Player player = this.server.getPlayerExact(event.getPlayerName());
		if (player.isOnline()) {
			player.kickPlayer(this.getKickMessage(event.getRecord()));
		}
  }

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLogin(final AsyncPlayerPreLoginEvent event) {
		final String playerName = event.getName();
		if (this.isPlayerBanned(playerName)) {
			final BanRecord record = this.playerRecordManager.find(playerName).getActiveBan();
			final String message = this.getKickMessage(record);
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLogin(final PlayerLoginEvent event) {
		final String playerName = event.getPlayer().getName();
		if (this.isPlayerBanned(playerName)) {
			final BanRecord record = this.playerRecordManager.find(playerName).getActiveBan();
			final String message = this.getKickMessage(record);
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, message);
		}
	}

	protected String getKickMessage(BanRecord record) {
		switch (record.getType()) {
			case TEMPORARY: {
				Object[] params = {record.getReason(), record.getCreator().getName(), record.getExpiresAt()};
				return MessageFormat.format(this.LOCALISATION.getString("banned_permanently"), params);
			}
			default: {
				Object[] params = {record.getReason(), record.getCreator().getName()};
				return MessageFormat.format(this.LOCALISATION.getString("banned_temporarily"), params);
			}
		}
	}

	protected boolean isPlayerBanned(String playerName) {
		if (!this.playerRecordManager.exists(playerName)) return false;
		PlayerRecord record = this.playerRecordManager.find(playerName);
		return record.isBanned();
	}



}
