/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * BanRecord.java is part of BanHammer.
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

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.utilities.formatters.*;
import name.richardson.james.bukkit.utilities.localisation.Localisation;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundleByClassLocalisation;

@Entity()
@Table(name = "banhammer_bans")
public class BanRecord {

	/**
	 * The created at.
	 */
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp createdAt;
	/**
	 * The creator.
	 */
	@ManyToOne(targetEntity = PlayerRecord.class, fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
	@PrimaryKeyJoinColumn(name = "creatorId", referencedColumnName = "id")
	private PlayerRecord creator;
	/**
	 * The expires at.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp expiresAt;
	/**
	 * The id.
	 */
	@Id
	private int id;
	/**
	 * The player.
	 */
	@ManyToOne(targetEntity = PlayerRecord.class, fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
	@PrimaryKeyJoinColumn(name = "playerId", referencedColumnName = "id")
	private PlayerRecord player;
	/**
	 * The reason.
	 */
	@NotNull
	private String reason;
	/**
	 * The state.
	 */
	@NotNull
	private State state;

	/**
	 * Gets the created at.
	 *
	 * @return the created at
	 */
	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	/**
	 * Sets the created at.
	 *
	 * @param time the new created at
	 */
	public void setCreatedAt(final Timestamp time) {
		this.createdAt = time;
	}

	/**
	 * Gets the creator.
	 *
	 * @return the creator
	 */
	@ManyToOne(targetEntity = PlayerRecord.class)
	public PlayerRecord getCreator() {
		return this.creator;
	}

	/**
	 * Sets the creator.
	 *
	 * @param creator the new creator
	 */
	public void setCreator(final PlayerRecord creator) {
		this.creator = creator;
	}

	/**
	 * Gets the expires at.
	 *
	 * @return the expires at
	 */
	public Timestamp getExpiresAt() {
		return this.expiresAt;
	}

	/**
	 * Sets the expires at.
	 *
	 * @param expiresAt the new expires at
	 */
	public void setExpiresAt(final Timestamp expiresAt) {
		this.expiresAt = expiresAt;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(final int id) {
		this.id = id;
	}

	/**
	 * Gets the player.
	 *
	 * @return the player
	 */
	public PlayerRecord getPlayer() {
		return this.player;
	}

	/**
	 * Sets the player.
	 *
	 * @param player the new player
	 */
	public void setPlayer(final PlayerRecord player) {
		this.player = player;
	}

	/**
	 * Gets the reason.
	 *
	 * @return the reason
	 */
	public String getReason() {
		return this.reason;
	}

	/**
	 * Sets the reason.
	 *
	 * @param reason the new reason
	 */
	public void setReason(final String reason) {
		this.reason = reason;
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public State getState() {
		if (this.state == State.NORMAL && this.hasExpired()) return State.EXPIRED;
		return this.state;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public void setState(final State state) {
		this.state = state;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public BanRecord.Type getType() {
		return (this.expiresAt == null) ? BanRecord.Type.PERMANENT : BanRecord.Type.TEMPORARY;
	}

	private boolean hasExpired() {
		if (this.getType() == Type.TEMPORARY) {
			return ((this.expiresAt.getTime() - System.currentTimeMillis()) < 0);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "BanRecord{" +
		"createdAt=" + createdAt +
		", creator=" + creator +
		", expiresAt=" + expiresAt +
		", id=" + id +
		", player=" + player +
		", reason='" + reason + '\'' +
		", state=" + state +
		"} ";
	}

	public BanRecordFormatter getFormatter() {
		return new BanRecordFormatter(this);
	}

	/**
	 * The valid states of a BanRecord
	 */
	public enum State {

		/**
		 * If a ban is currently active.
		 */
		NORMAL,

		/**
		 * If a ban has expired.
		 */
		EXPIRED,

		/**
		 * If a ban has been pardoned.
		 */
		PARDONED
	}

	/**
	 * The valid types of a BanRecord
	 */
	public enum Type {

		/**
		 * A ban which will never expire.
		 */
		PERMANENT,

		/**
		 * A ban which will expire after a period of time.
		 */
		TEMPORARY
	}

	public static class BanRecordFormatter {

		private static final DateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy HH:mm (z)");

		private static final String HEADER_KEY = "header";
		private static final String PERMANENT_KEY = "permanent";
		private static final String REASON_KEY = "reason";
		private static final String LENGTH_KEY = "length";
		private static final String EXPIRES_KEY = "expires";

		private final ColourFormatter colourFormatter= new DefaultColourFormatter();
		private final Localisation localisation = new ResourceBundleByClassLocalisation(BanRecordFormatter.class);
		private final TimeFormatter timeFormatter = new ApproximateTimeFormatter();
		private final TimeFormatter durationFormatter = new PreciseDurationTimeFormatter();
		private final BanRecord ban;
		private final List<String> messages= new ArrayList<String>();

		private BanRecordFormatter(BanRecord ban) {
			this.ban = ban;
			messages.add(getHeader());
			messages.add(getReason());
			messages.add(getLength());
			if (ban.getType() != Type.PERMANENT) messages.add(getExpiresAt());
		}

		public String getHeader() {
			final String date = DATE_FORMAT.format(ban.getCreatedAt());
			return colourFormatter.format(localisation.getMessage(HEADER_KEY), ColourFormatter.FormatStyle.HEADER, ban.getPlayer().getName(), ban.getCreator().getName(), date);
		}

		public String getReason() {
			return colourFormatter.format(localisation.getMessage(REASON_KEY), ColourFormatter.FormatStyle.INFO, ban.getReason());
		}

		public String getLength() {
			if (ban.getType() == Type.PERMANENT) {
				return colourFormatter.format(localisation.getMessage(LENGTH_KEY), ColourFormatter.FormatStyle.INFO, localisation.getMessage(PERMANENT_KEY));
			} else {
				final long length = ban.getExpiresAt().getTime() - ban.getCreatedAt().getTime();
				return colourFormatter.format(localisation.getMessage(LENGTH_KEY), ColourFormatter.FormatStyle.INFO, durationFormatter.getHumanReadableDuration(length));
			}
		}

		public String getExpiresAt() {
			final long time = ban.getExpiresAt().getTime();
			return colourFormatter.format(localisation.getMessage(EXPIRES_KEY), ColourFormatter.FormatStyle.INFO, timeFormatter.getHumanReadableDuration(time));
		}

		public String[] getMessages() {
			return this.messages.toArray(new String[messages.size()]);
		}

	}
}
