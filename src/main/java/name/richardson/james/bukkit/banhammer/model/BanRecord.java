/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 BanRecord.java is part of BanHammer.

 BanHammer is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option) any
 later version.

 BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.utilities.persistence.AbstractRecord;

import name.richardson.james.bukkit.banhammer.ban.BanRecordFormatter;
import name.richardson.james.bukkit.banhammer.ban.CommentRecordFormatter;
import name.richardson.james.bukkit.banhammer.ban.SimpleBanRecordFormatter;
import name.richardson.james.bukkit.banhammer.ban.SimpleCommentRecordFormatter;

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

	public static int count() {
		return getRecordDatabase().find(BanRecord.class).findList().size();
	}

	public static BanRecord create(PlayerRecord creator, PlayerRecord target, String reason, Timestamp time) {
		BanRecord ban = new BanRecord();
		ban.setCreator(creator);
		ban.setPlayer(target);
		ban.setState(State.NORMAL);
		CommentRecord comment = CommentRecord.create(creator, ban, reason);
		comment.setType(CommentRecord.Type.BAN_REASON);
		ban.setComment(comment);
		if (time != null) ban.setExpiresAt(time);
		return ban;
	}

	public static BanRecord create(PlayerRecord creator, PlayerRecord target, String reason) {
		return create(creator, target, reason, null);
	}

	protected static EbeanServer getRecordDatabase() {
		return BanHammerDatabase.getDatabase();
	}

	public static List<BanRecord> list(int limit) {
		return getRecordDatabase().find(BanRecord.class).setMaxRows(limit).orderBy().desc("created_at").findList();
	}

	public static List<BanRecord> list() {
		return getRecordDatabase().find(BanRecord.class).orderBy().desc("created_at").findList();
	}

	public void addComment(CommentRecord record) {
		getComments().add(record);
		record.setBan(this);
	}

	public void delete() {
		getDatabase().delete(this);
	}

	public CommentRecord getComment(CommentRecord.Type type) {
		for (CommentRecord comment : getComments()) {
			if (comment.getType() == type) return comment;
		}
		return null;
	}

	public CommentRecordFormatter getCommentFormatter() {
		CommentRecordFormatter formatter = new SimpleCommentRecordFormatter(getComments());
		formatter.removeComments(CommentRecord.Type.BAN_REASON);
		return formatter;
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

	public void setComment(CommentRecord comment) {
		if (comment.getType() != CommentRecord.Type.NORMAL) {
			CommentRecord record = getComment(comment.getType());
			if (record != null) {
				getComments().remove(record);
				record.delete();
			}
		}
		getComments().add(comment);
	}

	public void setComments(final Set<CommentRecord> comments) {
		this.comments = comments;
	}

	public void setCreator(final PlayerRecord creator) {
		this.creator = creator;
	}

	public void setExpiresAt(final Timestamp expiresAt) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		this.expiresAt = expiresAt;
	}

	public void setExpiryDuration(final long duration) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		Timestamp expiry = new Timestamp(now.getTime() + duration);
		this.setCreatedAt(now);
		this.expiresAt = expiry;
	}

	public void setPlayer(final PlayerRecord player) {
		this.player = player;
	}

	public void setState(final State state) {
		this.state = state;
	}

	@Override protected EbeanServer getDatabase() {
		return getRecordDatabase();
	}
}
