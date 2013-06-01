/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * BanHandler.java is part of BanHammer.
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
package name.richardson.james.bukkit.banhammer.api;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord.State;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord.Type;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.localisation.Localised;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundles;

/*
 * A simple implementation of the BanHandler interace.
 */
public class SimpleBanHandler implements Localised, BanHandler {

	/* The database used by this handler */
	private final EbeanServer database;

	/* Localisation messages for BanHammer */
	private final ResourceBundle localisation = ResourceBundle.getBundle(ResourceBundles.MESSAGES.getBundleName());

	/**
	 * Instantiates a new ban handler.
	 * 
	 * @param plugin
	 *          the plugin
	 */
	public SimpleBanHandler(final BanHammer plugin) {
		this.database = plugin.getDatabase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * name.richardson.james.bukkit.banhammer.api.BanHandler#banPlayer(java.lang
	 * .String, name.richardson.james.bukkit.banhammer.persistence.BanRecord,
	 * java.lang.String, boolean)
	 */
	public boolean banPlayer(final String playerName, final BanRecord sourceBan, final String reason, final boolean notify) {
		final PlayerRecord player = PlayerRecord.find(this.database, playerName);
		if (player.isBanned()) { return false; }
		final PlayerRecord creator = sourceBan.getCreator();
		final BanRecord ban = new BanRecord();
		ban.setPlayer(player);
		ban.setCreator(creator);
		ban.setReason(reason);
		ban.setState(BanRecord.State.NORMAL);
		ban.setCreatedAt(sourceBan.getCreatedAt());
		if (sourceBan.getType() == Type.TEMPORARY) {
			ban.setExpiresAt(sourceBan.getExpiresAt());
		}
		new BanHammerPlayerBannedEvent(ban, !notify, true);
		this.database.save(ban);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * name.richardson.james.bukkit.banhammer.api.BanHandler#banPlayer(java.lang
	 * .String, java.lang.String, java.lang.String, long, boolean)
	 */
	public boolean banPlayer(final String playerName, final String senderName, final String reason, final long banLength, final boolean notify) {
		final PlayerRecord player = PlayerRecord.find(this.database, playerName);
		if (player.isBanned()) { return false; }
		final PlayerRecord creator = PlayerRecord.find(this.database, senderName);
		final BanRecord ban = new BanRecord();
		final long now = System.currentTimeMillis();
		ban.setPlayer(player);
		ban.setCreator(creator);
		ban.setReason(reason);
		ban.setState(BanRecord.State.NORMAL);
		ban.setCreatedAt(new Timestamp(now));
		if (banLength != 0) {
			ban.setExpiresAt(new Timestamp(now + banLength));
		}
		this.database.save(ban);
		final BanHammerPlayerBannedEvent event = new BanHammerPlayerBannedEvent(ban, !notify, false);
		Bukkit.getServer().getPluginManager().callEvent(event);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * name.richardson.james.bukkit.utilities.localisation.Localised#getMessage
	 * (java.lang.String)
	 */
	public String getMessage(final String key) {
		String message = this.localisation.getString(key);
		message = ColourFormatter.replace(message);
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * name.richardson.james.bukkit.utilities.localisation.Localised#getMessage
	 * (java.lang.String, java.lang.Object[])
	 */
	public String getMessage(final String key, final Object... elements) {
		final MessageFormat formatter = new MessageFormat(this.localisation.getString(key));
		formatter.setLocale(Locale.getDefault());
		String message = formatter.format(elements);
		message = ColourFormatter.replace(message);
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * name.richardson.james.bukkit.banhammer.api.BanHandler#getPlayerBans(java
	 * .lang.String)
	 */
	public List<BanRecord> getPlayerBans(final String playerName) {
		final PlayerRecord playerRecord = PlayerRecord.find(this.database, playerName);
		return (playerRecord == null) ? new LinkedList<BanRecord>() : playerRecord.getBans();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * name.richardson.james.bukkit.banhammer.api.BanHandler#isPlayerBanned(org
	 * .bukkit.OfflinePlayer)
	 */
	public boolean isPlayerBanned(final OfflinePlayer player) {
		return this.isPlayerBanned(player.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * name.richardson.james.bukkit.banhammer.api.BanHandler#isPlayerBanned(java
	 * .lang.String)
	 */
	public boolean isPlayerBanned(final String playerName) {
		final PlayerRecord playerRecord = PlayerRecord.find(this.database, playerName);
		return (playerRecord == null) ? false : playerRecord.isBanned();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * name.richardson.james.bukkit.banhammer.api.BanHandler#pardonPlayer(java
	 * .lang.String, java.lang.String, boolean)
	 */
	public boolean pardonPlayer(final String playerName, final String senderName, final boolean notify) {
		final PlayerRecord playerRecord = PlayerRecord.find(this.database, playerName);
		if ((playerRecord != null) && playerRecord.isBanned()) {
			final BanRecord banRecord = playerRecord.getActiveBan();
			final BanHammerPlayerPardonedEvent event = new BanHammerPlayerPardonedEvent(banRecord, !notify);
			banRecord.setState(State.PARDONED);
			this.database.update(banRecord);
			Bukkit.getServer().getPluginManager().callEvent(event);
			return true;
		} else {
			return false;
		}

	}
}
