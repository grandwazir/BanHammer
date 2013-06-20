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
import java.util.*;

import name.richardson.james.bukkit.banhammer.persistence.BanRecordManager;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;
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
public class SimpleBanHandler implements BanHandler {

	/* Localisation messages for BanHammer */
	private final ResourceBundle localisation = ResourceBundle.getBundle(ResourceBundles.MESSAGES.getBundleName());

	private final PlayerRecordManager playerRecordManager;
	private final BanRecordManager banRecordManager;

	public SimpleBanHandler(final PlayerRecordManager playerRecordManager, final BanRecordManager banRecordManager) {
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
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
		return this.banPlayer(playerName, sourceBan.getCreator().getName(), reason, sourceBan.getExpiresAt(), notify);
	}

	public boolean banPlayer(String playerName, String senderName, String reason, Timestamp expires, boolean notify) {
		final PlayerRecord player = this.playerRecordManager.create(playerName);
		if (player.isBanned()) return false;
		final PlayerRecord creator = this.playerRecordManager.create(senderName);
		final BanRecord ban = new BanRecord();
		ban.setPlayer(player);
		ban.setCreator(creator);
		ban.setReason(reason);
		ban.setState(BanRecord.State.NORMAL);
		ban.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		if (expires != null) ban.setExpiresAt(expires);
		player.getBans().add(ban);
		this.banRecordManager.save(ban);
		BanHammerPlayerBannedEvent event = new BanHammerPlayerBannedEvent(ban, !notify);
		Bukkit.getServer().getPluginManager().callEvent(event);
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
		return this.banPlayer(playerName, senderName, reason, new Timestamp(System.currentTimeMillis() + banLength), notify);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * name.richardson.james.bukkit.banhammer.api.BanHandler#getPlayerBans(java
	 * .lang.String)
	 */
	public List<BanRecord> getPlayerBans(final String playerName) {
		if (this.playerRecordManager.exists(playerName)) {
			return this.playerRecordManager.find(playerName).getBans();
		} else {
			return new ArrayList<BanRecord>();
		}
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
		if (this.playerRecordManager.exists(playerName)) {
			return this.playerRecordManager.find(playerName).isBanned();
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * name.richardson.james.bukkit.banhammer.api.BanHandler#pardonPlayer(java
	 * .lang.String, java.lang.String, boolean)
	 */
	public void pardonPlayer(final String playerName, final String senderName, final boolean notify) {
		if (this.playerRecordManager.exists(playerName)) {
			final PlayerRecord player = this.playerRecordManager.find(playerName);
			if (!player.isBanned()) return;
			player.getActiveBan().setState(State.PARDONED);
			this.banRecordManager.update(player.getActiveBan());
			final BanHammerPlayerPardonedEvent event = new BanHammerPlayerPardonedEvent(player.getActiveBan(), !notify);
			Bukkit.getServer().getPluginManager().callEvent(event);
		}
		return;
	}

}
