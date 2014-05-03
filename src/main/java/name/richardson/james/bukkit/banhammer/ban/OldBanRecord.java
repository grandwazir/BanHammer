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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.utilities.formatters.time.ApproximateTimeFormatter;
import name.richardson.james.bukkit.utilities.formatters.time.PreciseDurationTimeFormatter;
import name.richardson.james.bukkit.utilities.formatters.time.TimeFormatter;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.*;

@Entity()
@Table(name = "banhammer_bans")
public class OldBanRecord implements BanRecord {

	/**
	 * The created at.
	 */
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp createdAt;
	/**
	 * The creator.
	 */
	@ManyToOne(targetEntity = OldPlayerRecord.class, fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
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
	@ManyToOne(targetEntity = OldPlayerRecord.class, fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
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
	@Override
	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	/**
	 * Gets the creator.
	 *
	 * @return the creator
	 */
	@Override
	@ManyToOne(targetEntity = OldPlayerRecord.class)
	public PlayerRecord getCreator() {
		return this.creator;
	}

	/**
	 * Gets the expires at.
	 *
	 * @return the expires at
	 */
	@Override
	public Timestamp getExpiresAt() {
		return this.expiresAt;
	}

	@Override
	public name.richardson.james.bukkit.banhammer.ban.BanRecordFormatter getFormatter() {
		return new BanRecordFormatter(this);
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	@Override
	public int getId() {
		return this.id;
	}

	/**
	 * Gets the player.
	 *
	 * @return the player
	 */
	@Override
	public PlayerRecord getPlayer() {
		return this.player;
	}

	/**
	 * Gets the reason.
	 *
	 * @return the reason
	 */
	@Override
	public String getReason() {
		return this.reason;
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	@Override
	public State getState() {
		if (this.state == State.NORMAL && this.hasExpired()) return State.EXPIRED;
		return this.state;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	@Override
	public OldBanRecord.Type getType() {
		return (this.expiresAt == null) ? OldBanRecord.Type.PERMANENT : OldBanRecord.Type.TEMPORARY;
	}

	/**
	 * Sets the created at.
	 *
	 * @param time the new created at
	 */
	@Override
	public void setCreatedAt(final Timestamp time) {
		this.createdAt = time;
	}

	/**
	 * Sets the creator.
	 *
	 * @param creator the new creator
	 */
	@Override
	public void setCreator(final PlayerRecord creator) {
		this.creator = creator;
	}

	/**
	 * Sets the expires at.
	 *
	 * @param expiresAt the new expires at
	 */
	@Override
	public void setExpiresAt(final Timestamp expiresAt) {
		this.expiresAt = expiresAt;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	@Override
	public void setId(final int id) {
		this.id = id;
	}

	/**
	 * Sets the player.
	 *
	 * @param player the new player
	 */
	@Override
	public void setPlayer(final PlayerRecord player) {
		this.player = player;
	}

	/**
	 * Sets the reason.
	 *
	 * @param reason the new reason
	 */
	@Override
	public void setReason(final String reason) {
		this.reason = reason;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	@Override
	public void setState(final State state) {
		this.state = state;
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

	@Override
	public boolean hasExpired() {
		if (this.getType() == Type.TEMPORARY) {
			return ((this.expiresAt.getTime() - System.currentTimeMillis()) < 0);
		} else {
			return false;
		}
	}

	public static class BanRecordFormatter implements name.richardson.james.bukkit.banhammer.ban.BanRecordFormatter {

		private static final DateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy HH:mm (z)");
		private final BanRecord ban;
		private final TimeFormatter durationFormatter = new PreciseDurationTimeFormatter();
		private final List<String> messages = new ArrayList<String>();
		private final TimeFormatter timeFormatter = new ApproximateTimeFormatter();

		private BanRecordFormatter(BanRecord ban) {
			this.ban = ban;
			messages.add(getHeader());
			messages.add(getReason());
			messages.add(getLength());
			if (ban.getType() != Type.PERMANENT && ban.getState() != State.PARDONED) messages.add(getExpiresAt());
			if (ban.getState() == State.PARDONED) messages.add(getPardoned());
		}

		private String getPardoned() {
			return BAN_WAS_PARDONED.asInfoMessage();
		}

		@Override
		public String getExpiresAt() {
			final long time = ban.getExpiresAt().getTime();
			return EXPIRES_AT.asInfoMessage(timeFormatter.getHumanReadableDuration(time));
		}

		@Override
		public String getHeader() {
			final String date = DATE_FORMAT.format(ban.getCreatedAt());
			return BAN_SUMMARY.asHeaderMessage(ban.getPlayer().getName(), ban.getCreator().getName(), date);
		}

		@Override
		public String getLength() {
			if (ban.getType() == Type.PERMANENT) {
				return LENGTH.asInfoMessage(PERMANENT.toString());
			} else {
				final long length = ban.getExpiresAt().getTime() - ban.getCreatedAt().getTime();
				return LENGTH.asInfoMessage(durationFormatter.getHumanReadableDuration(length));
			}
		}

		@Override
		public Collection<String> getMessages() {
			return Collections.unmodifiableCollection(messages);
		}

		@Override
		public String getReason() {
			return REASON.asInfoMessage(ban.getReason());
		}

	}
}
