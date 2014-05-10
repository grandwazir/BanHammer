package name.richardson.james.bukkit.banhammer.record;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;

@Entity
@Table(name = "banhammer_bans")
public class CurrentBanRecord extends SimpleRecord implements BanRecord {

	@ManyToOne(targetEntity = CurrentPlayerRecord.class, fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
	@PrimaryKeyJoinColumn(name = "creatorId", referencedColumnName = "id")
	private CurrentPlayerRecord creator;
	private Timestamp expiresAt;
	@Id
	@NotNull
	@GeneratedValue
	private long id;
	@ManyToOne(targetEntity = CurrentPlayerRecord.class, fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
	@PrimaryKeyJoinColumn(name = "playerId", referencedColumnName = "id")
	private CurrentPlayerRecord player;
	@NotNull
	private String reason;
	@NotNull
	private State state;

	protected CurrentBanRecord() {}

	public CurrentBanRecord(final PlayerRecord player, final PlayerRecord creator, final String reason) {
		this.setPlayer(player);
		this.setCreator(creator);
		this.setReason(reason);
		this.setState(State.NORMAL);
		this.setCreatedAt(new Timestamp(System.currentTimeMillis()));
	}

	@Override public PlayerRecord getCreator() {
		return creator;
	}

	@Override public Timestamp getExpiresAt() {
		return expiresAt;
	}

	@Override public long getId() {
		return id;
	}

	@Override public PlayerRecord getPlayer() {
		return player;
	}

	@Override public String getReason() {
		return reason;
	}

	@Override public State getState() {
		return (this.hasExpired()) ? State.EXPIRED : state;
	}

	@Override public Type getType() {
		return (this.expiresAt == null) ? Type.PERMANENT : Type.TEMPORARY;
	}

	@Override public void setCreator(final PlayerRecord creator) {
		this.creator = (CurrentPlayerRecord) creator;
	}

	@Override public void setExpiresAt(final Timestamp expiresAt) {
		this.expiresAt = expiresAt;
	}

	@Override public void setId(final long id) {
		this.id = id;
	}

	@Override public void setPlayer(final PlayerRecord player) {
		this.player = (CurrentPlayerRecord) player;
	}

	@Override public void setReason(final String reason) {
		this.reason = reason;
	}

	@Override public void setState(final State state) {
		this.state = state;
	}

	@Override public void setExpiryTime(long time) {
		long now = System.currentTimeMillis();
		this.setCreatedAt(new Timestamp(now));
		if (time != 0) this.setExpiresAt(new Timestamp(now + time));
	}

	private boolean hasExpired() {
		return this.getType() == Type.TEMPORARY && ((this.expiresAt.getTime() - System.currentTimeMillis()) < 0);
	}

}
