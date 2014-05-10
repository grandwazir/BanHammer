/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * OldBanRecord.java is part of BanHammer.
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

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "banhammer_bans")
public class OldBanRecord {

	/**
	 * The valid states of a OldBanRecord
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
	 * The valid types of a OldBanRecord
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
	private OldPlayerRecord creator;
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
	private OldPlayerRecord player;
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
	 * Gets the creator.
	 *
	 * @return the creator
	 */
	@ManyToOne(targetEntity = OldPlayerRecord.class)
	public OldPlayerRecord getCreator() {
		return this.creator;
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
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Gets the player.
	 *
	 * @return the player
	 */
	public OldPlayerRecord getPlayer() {
		return this.player;
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
	 * Gets the state.
	 *
	 * @return the state
	 */
	public State getState() {
		if (this.state == State.NORMAL && this.hasExpired()) return State.EXPIRED;
		return this.state;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public OldBanRecord.Type getType() {
		return (this.expiresAt == null) ? OldBanRecord.Type.PERMANENT : OldBanRecord.Type.TEMPORARY;
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
	 * Sets the creator.
	 *
	 * @param creator the new creator
	 */
	public void setCreator(final OldPlayerRecord creator) {
		this.creator = creator;
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
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(final int id) {
		this.id = id;
	}

	/**
	 * Sets the player.
	 *
	 * @param player the new player
	 */
	public void setPlayer(final OldPlayerRecord player) {
		this.player = player;
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
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public void setState(final State state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "OldBanRecord{" +
		"createdAt=" + createdAt +
		", creator=" + creator +
		", expiresAt=" + expiresAt +
		", id=" + id +
		", player=" + player +
		", reason='" + reason + '\'' +
		", state=" + state +
		"} ";
	}

	private boolean hasExpired() {
		if (this.getType() == Type.TEMPORARY) {
			return ((this.expiresAt.getTime() - System.currentTimeMillis()) < 0);
		} else {
			return false;
		}
	}

}
