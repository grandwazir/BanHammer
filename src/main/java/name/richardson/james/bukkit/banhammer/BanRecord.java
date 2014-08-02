package name.richardson.james.bukkit.banhammer;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.utilities.persistence.AbstractRecord;

import name.richardson.james.bukkit.banhammer.ban.BanRecordFormatter;
import name.richardson.james.bukkit.banhammer.ban.SimpleBanRecordFormatter;

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
	private static EbeanServer database;
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

	public static int count() {
		return getRecordDatabase().find(BanRecord.class).findList().size();
	}

	public static BanRecord create(PlayerRecord creator, PlayerRecord target, String reason, Timestamp time) {
		BanRecord record = new BanRecord();
		record.setCreator(creator);
		record.setPlayer(target);
		CommentRecord comment = new CommentRecord();
		comment.setCreator(creator);
		comment.setComment(reason);
		record.setReason(comment);
		record.setState(State.NORMAL);
		if (time != null) record.setExpiresAt(time);
		return record;
	}

	public static BanRecord create(PlayerRecord creator, PlayerRecord target, String reason) {
		return create(creator, target, reason, null);
	}

	protected static EbeanServer getRecordDatabase() {
		return BanRecord.database;
	}

	public static List<BanRecord> list(int limit) {
		return getRecordDatabase().find(BanRecord.class).setMaxRows(limit).orderBy().desc("created_at").findList();
	}

	public static List<BanRecord> list() {
		return getRecordDatabase().find(BanRecord.class).orderBy().desc("created_at").findList();
	}

	protected static void setRecordDatabase(final EbeanServer database) {
		BanRecord.database = database;
	}

	public void addComment(CommentRecord record) {
		getComments().add(record);
		record.setBan(this);
	}

	public void delete() {
		getDatabase().delete(this);
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

	public BanRecordFormatter getFormatter() {
		return new SimpleBanRecordFormatter(this);
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
		Timestamp now = new Timestamp(System.currentTimeMillis());
		if (this.expiresAt != null && this.expiresAt.before(now) && this.state == State.NORMAL) {
			this.state = State.EXPIRED;
		}
		return state;
	}

	public Type getType() {
		if (this.expiresAt != null) return Type.TEMPORARY;
		return Type.PERMANENT;
	}

	public void setComments(final Set<CommentRecord> comments) {
		this.comments = comments;
	}

	public void setCreator(final PlayerRecord creator) {
		this.creator = creator;
	}

	public void setExpiryDuration(final long duration) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		Timestamp expiry = new Timestamp(now.getTime() + duration);
		this.setCreatedAt(now);
		this.expiresAt = expiry;
	}

	public void setExpiresAt(final Timestamp expiresAt) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		this.expiresAt = expiresAt;
	}

	public void setPlayer(final PlayerRecord player) {
		this.player = player;
	}

	public void setPardonReason(CommentRecord record) {
		record.setType(CommentRecord.Type.PARDON_REASON);
		CommentRecord reason = getPardonReason();
		if (reason != null) getComments().remove(reason);
		addComment(record);
	}

	public CommentRecord getPardonReason() {
		CommentRecord record = null;
		for (CommentRecord comment : getComments()) {
			if (comment.getType() == CommentRecord.Type.PARDON_REASON) {
				record = comment;
				break;
			}
		}
		return record;
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
		return getRecordDatabase();
	}
}
