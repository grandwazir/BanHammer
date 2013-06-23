package name.richardson.james.bukkit.banhammer.api;

import java.sql.Timestamp;
import java.util.List;

import org.bukkit.OfflinePlayer;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord;

/**
 * The Interface BanHandler which is responsible for defining how other plugins
 * can interact with BanHammer. Currently this is limited to banning and
 * pardoning players. It is also possible to get detail about any bans which
 * have been made.
 */
@Deprecated
public interface BanHandler {

	/**
	 * Ban a player using another ban as the template.
	 * 
	 * This is useful if you want to extend the same ban to another player; for
	 * example if they are logging in with another account.
	 * 
	 * @param playerName
	 *          the name of the player to ban
	 * @param sourceBan
	 *          the ban to use as a template
	 * @param reason
	 *          the reason for the ban
	 * @param notify
	 *          if true, broadcast a message to notify players
	 * @return true, if successful
	 */
	@Deprecated
	public abstract boolean banPlayer(String playerName, BanRecord sourceBan, String reason, boolean notify);

	/**
	 * Ban a player from the server.
	 * 
	 * This will ban a player immediately from the server until the ban expires.
	 * If the player is online at the time they will be kicked and notified of the
	 * ban. All players who have the banhammer.notify node, will receive a
	 * notification of the action unless silent is set to true.
	 * 
	 * @param playerName
	 *          the name of the player to ban.
	 * @param senderName
	 *          the name of player who banned them. In the case of a plugin this
	 *          should be the plugin's name.
	 * @param reason
	 *          the reason for the ban.
	 * @param banLength
	 *          the ban length of the ban in milliseconds.
	 * @param notify
	 *          whether or not to notify players of this ban
	 * @return true, if successful
	 */
	@Deprecated
	public abstract boolean banPlayer(String playerName, String senderName, String reason, long banLength, boolean notify);

	public abstract boolean banPlayer(String playerName, String senderName, String reason, Timestamp expires, boolean notify);

	/**
	 * Gets a list of all bans issued to a particular player.
	 * 
	 * This will return an empty list if the player is not known to BanHammer.
	 * 
	 * @param playerName
	 *          the name of the player to look up
	 * @return any bans which they have on record.
	 */
	public abstract List<BanRecord> getPlayerBans(String playerName);

	/**
	 * Checks to see if a player is banned.
	 * 
	 * Will return true if the player has any bans on record which are currently
	 * preventing them from logging in.
	 * 
	 * @param player
	 *          the player to look up (case insensitive)
	 * @return true, if is player currently banned
	 */
	public abstract boolean isPlayerBanned(OfflinePlayer player);

	/**
	 * Checks to see if a player is banned.
	 * 
	 * Will return true if the player has any bans on record which are currently
	 * preventing them from logging in.
	 * 
	 * @param playerName
	 *          the name of the player to look up (case insensitive)
	 * @return true, if is player currently banned
	 */
	public abstract boolean isPlayerBanned(String playerName);

	/**
	 * Pardon a player.
	 * 
	 * This will pardon any ban that is preventing them from logging into the
	 * server. This does not delete the ban from the database, instead it marks it
	 * as PARDONED.
	 * 
	 * @param playerName
	 *          the name of the player to pardon
	 * @param senderName
	 *          the name of player who is unbanning them. In the case of a plugin
	 *          this should be the plugin's name.
	 * @param notify
	 *          if true, broadcast a message to notify players
	 * @return true, if successful
	 */
	public abstract void pardonPlayer(String playerName, String senderName, boolean notify);

}