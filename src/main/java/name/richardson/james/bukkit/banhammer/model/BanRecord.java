package name.richardson.james.bukkit.banhammer.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.utilities.persistence.AbstractRecord;

@Entity
@Table(name = "banhammer_" + "bans")
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
	@OneToMany(cascade = CascadeType.ALL, targetEntity = CommentRecord.class)
	@JoinColumn(name = "ban_id", referencedColumnName = "id")
	private Set<CommentRecord> comments;
	@ManyToOne(optional = false)
	@JoinColumn(name = "creator_id", referencedColumnName = "id")
	private PlayerRecord creator;
	private Timestamp expiresAt;
	@ManyToOne(optional = false)
	@JoinColumn(name = "player_id", referencedColumnName = "id")
	private PlayerRecord player;
	@NotNull
	private State state;

	public static BanRecord create(PlayerRecord creator, PlayerRecord target, String reason) {
		return create(creator, target, reason, null);
	}

	public static BanRecord create(PlayerRecord creator, PlayerRecord target, String reason, Timestamp time) {
		BanRecord record = new BanRecord();
		record.setCreator(creator);
		record.setPlayer(target);
		CommentRecord comment = new CommentRecord();
		comment.setCreator(creator);
		comment.setComment(reason);
		record.setReason(comment);
		if (time != null) record.setExpiresAt(time);
		return record;
	}

	public void addComment(CommentRecord record) {
		getComments().add(record);
		record.setBan(this);
	}

	public Set<CommentRecord> getComments() {
		if (comments == null) this.comments = new HashSet<CommentRecord>();
		return comments;
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

	public CommentRecord getReason() {
		CommentRecord record = null;
		for (CommentRecord comment : getComments()) {
			if (comment.getType() == CommentRecord.Type.BAN_REASON) {
				record = comment;
				break;
			}
		}
		return record;
	}

	public State getState() {
		return state;
	}

	public void setComments(final Set<CommentRecord> comments) {
		this.comments = comments;
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

	public void setReason(CommentRecord record) {
		record.setType(CommentRecord.Type.BAN_REASON);
		CommentRecord reason = getReason();
		if (reason != null) getComments().remove(reason);
		addComment(record);
	}

	public void setState(final State state) {
		this.state = state;
	}

	@Override protected EbeanServer getDatabase() {
		return Ebean.getServer("BanHammer");
	}
}
