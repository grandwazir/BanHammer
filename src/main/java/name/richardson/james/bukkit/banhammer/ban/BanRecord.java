package name.richardson.james.bukkit.banhammer.ban;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.UUID;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;

public class BanRecord extends Record {

	public enum State {
		NORMAL,
		EXPIRED,
		PARDONED
	}

	public enum Type {
		PERMANENT,
		TEMPORARY
	}

	@ManyToOne(targetEntity = OldPlayerRecord.class, fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
	@PrimaryKeyJoinColumn(name = "creatorId", referencedColumnName = "id")
	private PlayerRecord creator;

	private Timestamp expiresAt;

	@ManyToOne(targetEntity = PlayerRecord.class, fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
	@PrimaryKeyJoinColumn(name = "playerId", referencedColumnName = "id")
	private PlayerRecord player;

	@NotNull
	private String reason;

	@NotNull
	private State state;

	/**
	 * Find all BanRecords that match a specific state
	 *
	 * @param database the database to use.
	 * @param state the state to match.
	 * @return the BanRecords that match.
	 */
	public static Collection<BanRecord> find(EbeanServer database, State state) {
		return database.find(BanRecord.class).where().eq("state", state.ordinal()).findList();
	}

	public PlayerRecord getCreator() {
		return creator;
	}

	public Timestamp getExpiresAt() {
		return expiresAt;
	}

	public PlayerRecord getPlayer() {
		return player;
	}

	public String getReason() {
		return reason;
	}

	public State getState() {
		return (this.hasExpired()) ? State.EXPIRED : state;
	}

	public Type getType() {
		return (this.expiresAt == null) ? Type.PERMANENT : Type.TEMPORARY;
	}

	public void setCreator(final PlayerRecord creator) {
		this.creator = creator;
	}

	public void setExpiresAt(final Timestamp expiresAt) {
		this.expiresAt = expiresAt;
	}

	public void setPlayer(final PlayerRecord player) {
		this.player = player;
	}

	public void setReason(final String reason) {
		this.reason = reason;
	}

	public void setState(final State state) {
		this.state = state;
	}

	private boolean hasExpired() {
		return this.getType() == Type.TEMPORARY && ((this.expiresAt.getTime() - System.currentTimeMillis()) < 0);
	}

	public BanRecordFormatter getFormatter() {
		return new BanRecordFormatter(this);
	}

}
