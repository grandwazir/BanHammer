package name.richardson.james.bukkit.banhammer.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.banhammer.BanHammer;

@Entity
@Table(name = BanHammer.TABLE_PREFIX + "bans")
public class BanRecord extends AbstractRecord {

	public enum State {
		NORMAL,
		EXPIRED,
		PARDONED
	}

	public enum Type {
		PERMANENT,
		TEMPORARY
	}

	@ManyToMany(targetEntity = CommentRecord.class)
	@JoinTable(name = BanHammer.TABLE_PREFIX + "bans_has_comments")
	private Set<CommentRecord> comments;

	@ManyToOne(optional = false)
	@JoinColumn(name = "creator_id", referencedColumnName = "id")
	private PlayerRecord creator;

	private Timestamp expiresAt;

	@ManyToOne(optional = false)
	@JoinColumn(name = "player_id", referencedColumnName = "id")
	private PlayerRecord player;

	public State getState() {
		return state;
	}

	public void setState(final State state) {
		this.state = state;
	}

	public Set<CommentRecord> getComments() {
		return comments;
	}

	public void setComments(final Set<CommentRecord> comments) {
		this.comments = comments;
	}

	public PlayerRecord getCreator() {
		return creator;
	}

	public void setCreator(final PlayerRecord creator) {
		this.creator = creator;
	}

	public Timestamp getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(final Timestamp expiresAt) {
		this.expiresAt = expiresAt;
	}

	public PlayerRecord getPlayer() {
		return player;
	}

	public void setPlayer(final PlayerRecord player) {
		this.player = player;
	}

	@NotNull
	private State state;

}
