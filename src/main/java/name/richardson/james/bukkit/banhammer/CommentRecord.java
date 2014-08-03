/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 CommentRecord.java is part of BanHammer.

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
package name.richardson.james.bukkit.banhammer;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.utilities.persistence.AbstractRecord;

@Entity
@Table(name = "banhammer_" + "comments")
public class CommentRecord extends AbstractRecord {

	public enum Type {
		NORMAL,
		BAN_REASON,
		PARDON_REASON,
	}

	private static EbeanServer database;
	@ManyToOne
	@JoinColumn(name = "ban_id")
	private BanRecord ban;
	@NotNull
	private String comment;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_id")
	private PlayerRecord creator;
	@ManyToOne
	@JoinColumn(name = "player_id")
	private PlayerRecord player;
	private Type type;


	protected static EbeanServer getRecordDatabase() {
		return CommentRecord.database;
	}

	protected static void setRecordDatabase(final EbeanServer database) {
		CommentRecord.database = database;
	}

	public static CommentRecord create(PlayerRecord creator, PlayerRecord target, String comment) {
 		CommentRecord record = create(creator, comment);
		record.setPlayer(target);
		return record;
	}

	private static CommentRecord create(PlayerRecord creator, String comment) {
		CommentRecord record = new CommentRecord();
		record.setType(Type.NORMAL);
		record.setCreator(creator);
		record.setComment(comment);
		return record;
	}

	public static CommentRecord create(PlayerRecord creator, BanRecord target, String comment) {
	 	CommentRecord record = create(creator, comment);
		record.setBan(target);
		return record;
	}

	public void delete() {
		getDatabase().delete(this);
	}

	public BanRecord getBan() {
		return ban;
	}

	public String getComment() {
		return comment;
	}

	public PlayerRecord getCreator() {
		return creator;
	}

	public PlayerRecord getPlayer() {
		return player;
	}

	public Type getType() {
		return type;
	}

	public void setBan(final BanRecord ban) {
		this.ban = ban;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public void setCreator(final PlayerRecord creator) {
		this.creator = creator;
		if (player != null) this.creator.getComments().add(this);
	}

	@Override public String toString() {
		StringBuilder sb = new StringBuilder("CommentRecord{");
		if (ban != null) sb.append("ban=").append(ban.getState());
		sb.append(", comment='").append(comment).append('\'');
		sb.append(", creator=").append(creator.getName());
		if (player != null) sb.append(", player=").append(player.getName());
		sb.append(", type=").append(type);
		sb.append(", ").append(super.toString());
		sb.append('}');
		return sb.toString();
	}

	public void setPlayer(final PlayerRecord player) {
		this.player = player;
		if (player != null) this.player.getComments().add(this);
	}

	public void setType(final Type type) {
		this.type = type;
	}

	@Override protected EbeanServer getDatabase() {
		return getRecordDatabase();
	}


}
