/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * PlayerListener.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.ban;

import java.util.Collection;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.permissions.Permission;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.alias.AliasHandler;
import name.richardson.james.bukkit.alias.persistence.PlayerNameRecord;
import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.matchers.BannedPlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.matchers.CreatorPlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.matchers.PlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord.State;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord.Type;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.listener.AbstractLocalisedListener;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundles;
import name.richardson.james.bukkit.utilities.logging.Logger;

public class PlayerListener extends AbstractLocalisedListener {

	/**
	 * The types of message that can be broadcasted in response to events.
	 */
	public enum BroadcastMessageType {

		/** Player has been banned. */
		PLAYER_BANNED,

		/** Player has been pardoned. */
		PLAYER_PARDONED
	}

	/** The Alias API. */
	private final AliasHandler aliasHandler;
	private final EbeanServer database;
	/** The BanHammer API. */
	private final BanHandler handler;
	private final boolean onlineMode = Bukkit.getOnlineMode();
	private final Permission permission;
	private final Logger logger = new Logger(this);

	/**
	 * Instantiates a new player listener.
	 * 
	 * @param plugin
	 *          the plugin
	 */
	public PlayerListener(final BanHammer plugin) {
		super(plugin, ResourceBundles.MESSAGES);
		this.aliasHandler = plugin.getAliasHandler();
		this.handler = plugin.getHandler();
		this.permission = Bukkit.getPluginManager().getPermission("banhammer.notify");
		this.database = plugin.getDatabase();
		if (!this.onlineMode) {
			this.logger.log(Level.WARNING, "playerlistener.insecure-mode");
		}
	}

	/**
	 * When a player is banned, inform players.
	 * 
	 * @param event
	 *          the event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		// send event to matchers
		BannedPlayerRecordMatcher.onPlayerBanned(event);
		CreatorPlayerRecordMatcher.onPlayerBanned(event);
		PlayerRecordMatcher.onPlayerBanned(event);
		final Player player = Bukkit.getServer().getPlayer(event.getPlayerName());
		final BanRecord record = event.getRecord();
		// don't double kick a player who has been kicked by alias
		if ((player != null) && !record.getReason().contains(this.getMessage("playerlistener.alias-ban-reason"))) {
			player.kickPlayer(this.getBanMessage(record));
		}
		if (!event.isSilent()) {
			this.broadcast(record, BroadcastMessageType.PLAYER_BANNED);
		}
	}

	/**
	 * When a player logins, check to see if they are banned.
	 * 
	 * @param event
	 *          the event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLogin(final AsyncPlayerPreLoginEvent event) {
		if (this.isPlayerBanned(event.getName(), event.getAddress().toString())) {
			final BanRecord ban = PlayerRecord.find(this.database, event.getName()).getActiveBan();
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, this.getBanMessage(ban));
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerLogin(final PlayerLoginEvent event) {
		if (this.onlineMode) { return; }
		if (this.isPlayerBanned(event.getPlayer().getName(), event.getAddress().toString())) {
			final BanRecord ban = PlayerRecord.find(this.database, event.getPlayer().getName()).getActiveBan();
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, this.getBanMessage(ban));
		}
	}

	/**
	 * When a player is pardoned, remove them from the cache and notify players.
	 * 
	 * @param event
	 *          the event
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
		// send event to the matchers
		BannedPlayerRecordMatcher.onPlayerPardoned(event);
		CreatorPlayerRecordMatcher.onPlayerPardoned(event);
		PlayerRecordMatcher.onPlayerPardoned(event);
		if (!event.isSilent()) {
			this.broadcast(event.getRecord(), BroadcastMessageType.PLAYER_PARDONED);
		}
		if (this.aliasHandler != null) {
			final String reason = this.getMessage("playerlistener.alias-ban-reason", "");
			if (event.getRecord().getReason().contains(reason)) {
				final String alias = event.getRecord().getReason().replaceAll(reason, "");
				this.aliasHandler.deassociatePlayer(event.getPlayerName(), alias);
			}
		}
	}

	/**
	 * Broadcast notification message to the server.
	 * 
	 * @param ban
	 *          the ban
	 * @param type
	 *          the type
	 */
	private void broadcast(final BanRecord ban, final BroadcastMessageType type) {
		final Server server = Bukkit.getServer();
		final Object[] arguments = { ban.getPlayer().getName(), ban.getCreator().getName() };
		switch (type) {
			case PLAYER_BANNED:
				final BanSummary summary = new BanSummary(ban);
				server.broadcast(this.getMessage("playerlistener.ban-broadcast", arguments), this.permission.getName());
				server.broadcast(summary.getReason(), this.permission.getName());
				server.broadcast(summary.getLength(), this.permission.getName());
				break;
			case PLAYER_PARDONED:
				server.broadcast(this.getMessage("playerlistener.pardon-broadcast", arguments), this.permission.getName());
				break;
		}
	}

	private String getBanMessage(final BanRecord ban) {
		String message;
		switch (ban.getType()) {
			case TEMPORARY:
				message =
					this.getMessage("playerlistener.temporarily-banned", ban.getReason(), ban.getCreator().getName(),
						BanHammer.LONG_DATE_FORMAT.format(ban.getExpiresAt()));
			default:
				message = this.getMessage("playerlistener.permanently-banned", ban.getReason(), ban.getCreator().getName());
		}
		return message;
	}

	private boolean hasBanExpired(final BanRecord ban) {
		if (ban == null) { return true; }
		if (ban.getType() == Type.PERMANENT) { return false; }
		if (((System.currentTimeMillis() - ban.getExpiresAt().getTime()) < 0) && (ban.getState() == State.NORMAL)) {
			ban.setState(BanRecord.State.EXPIRED);
			this.database.save(ban);
			return true;
		} else {
			return false;
		}
	}

	private boolean isPlayerBanned(final String playerName, final String address) {
		final PlayerRecord player = PlayerRecord.find(this.database, playerName);
		boolean localBan = false;
		if (player != null) {
			final BanRecord ban = player.getActiveBan();
			localBan = !this.hasBanExpired(ban);
		} else {
			if (this.aliasHandler != null) {
				final Collection<PlayerNameRecord> aliases = this.aliasHandler.getPlayersNames(address);
				for (final PlayerNameRecord alias : aliases) {
					if (PlayerRecord.exists(this.database, alias.getPlayerName())) {
						final PlayerRecord record = PlayerRecord.find(this.database, alias.getPlayerName());
						if (!this.hasBanExpired(record.getActiveBan())) {
							// the player has an alias who is banned.
							final String reason = this.getMessage("playerlistener.alias-ban-reason", record.getName());
							this.handler.banPlayer(playerName, record.getActiveBan(), reason, true);
							return true;
						}
					}
				}
			}
		}
		return localBan;
	}

}