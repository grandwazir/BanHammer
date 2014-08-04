/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 PlayerRecord.java is part of BanHammer.

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.utilities.persistence.AbstractRecord;

@Entity
@Table(name = "banhammer_" + "players")
public class PlayerRecord extends AbstractRecord {

	public enum Status {
		CREATOR,
		BANNED,
		ANY
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "player", targetEntity = BanRecord.class)
	private Set<BanRecord> bans;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "creator", targetEntity = CommentRecord.class)
	private Set<CommentRecord> comments;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "creator", targetEntity = BanRecord.class)
	private Set<BanRecord> createdBans;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "creator", targetEntity = CommentRecord.class)
	private Set<CommentRecord> createdComments;
	@NotNull
	private String name;

	public static PlayerRecord create(UUID uuid)
	throws PlayerNotFoundException {
		PlayerRecord record = find(uuid);
		if (record == null) {
			String playerName = NameFetcher.getNameOf(uuid);
			if (playerName == null) throw new PlayerNotFoundException();
			record = create(uuid, playerName);
		}
		return record;
	}

	public static PlayerRecord create(String playerName)
	throws PlayerNotFoundException {
		PlayerRecord record = find(playerName);
		if (record == null) {
			UUID uuid = UUIDFetcher.getUUIDOf(playerName);
			if (playerName == null) throw new PlayerNotFoundException();
			record = create(uuid, playerName);
		}
		return record;
	}

	public static PlayerRecord create(UUID uuid, String name) {
		PlayerRecord record = find(uuid);
		if (record == null) {
			record = new PlayerRecord();
			record.setName(name);
			record.setId(uuid);
			record.save();
		}
		return record;
	}

	private static Set<PlayerRecord> filterPlayers(Set<PlayerRecord> records, Status status) {
		Iterator<PlayerRecord> iterator = records.iterator();
		switch (status) {
			case BANNED:
				while (iterator.hasNext()) {
					PlayerRecord player = iterator.next();
					if (!player.isBanned()) {
						iterator.remove();
					}
				}
			case CREATOR:
				while (iterator.hasNext()) {
					PlayerRecord player = iterator.next();
					if (player.getCreatedBans().isEmpty()) {
						iterator.remove();
					}
				}
		}
		return records;
	}

	public static PlayerRecord find(UUID uuid) {
		return getRecordDatabase().find(PlayerRecord.class).where().eq("id", uuid).findUnique();
	}

	public static PlayerRecord find(String name) {
		return getRecordDatabase().find(PlayerRecord.class).where().ieq("name", name).findUnique();
	}

	public static Set<PlayerRecord> find(Status status) {
		Set<PlayerRecord> records = getRecordDatabase().find(PlayerRecord.class).findSet();
		return filterPlayers(records, status);
	}

	public static Set<PlayerRecord> find(Status status, String name) {
		Set<PlayerRecord> records = getRecordDatabase().find(PlayerRecord.class).where().istartsWith("name", name).findSet();
		return filterPlayers(records, status);
	}

	private static EbeanServer getRecordDatabase() {
		return BanHammerDatabase.getDatabase();
	}

	public void addComment(final CommentRecord comment) {
		getComments().add(comment);
	}

	public void addCreatedComment(final CommentRecord comment) {
		getCreatedComments().add(comment);
	}

	public void delete() {
		getDatabase().delete(this);
	}

	public BanRecord getActiveBan() {
		for (BanRecord ban : getBans()) {
			if (ban.getState() == BanRecord.State.NORMAL) return ban;
		}
		return null;
	}

	public Set<BanRecord> getBans() {
		if (bans == null) return new HashSet<>();
		return bans;
	}

	public CommentRecordFormatter getCommentFormatter() {
		return new SimpleCommentRecordFormatter(getComments());
	}

	public Set<CommentRecord> getComments() {
		if (comments == null) comments = new HashSet<>();
		return comments;
	}

	public Set<BanRecord> getCreatedBans() {
		if (createdBans == null) createdBans = new HashSet<>();
		return createdBans;
	}

	public Set<CommentRecord> getCreatedComments() {
		if (createdComments == null) createdComments = new HashSet<>();
		return createdComments;
	}

	public String getName() {
		return name;
	}

	public boolean isBanned() {
		for (BanRecord ban : getBans()) {
			if (ban.getState() == BanRecord.State.NORMAL) return true;
		}
		return false;
	}

	public void setBans(final Set<BanRecord> bans) {
		this.bans = bans;
	}

	public void setComments(final Set<CommentRecord> comments) {
		this.comments = comments;
	}

	public void setCreatedBans(final Set<BanRecord> createdBans) {
		this.createdBans = createdBans;
	}

	public void setCreatedComments(final Set<CommentRecord> createdComments) {
		this.createdComments = createdComments;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override public String toString() {
		StringBuilder sb = new StringBuilder("PlayerRecord{");
		sb.append("bans=").append(bans);
		sb.append(", comments=").append(comments);
		sb.append(", createdBans=").append(createdBans);
		sb.append(", createdComments=").append(createdComments);
		sb.append(", name='").append(name).append('\'');
		sb.append(", ").append(super.toString());
		sb.append('}');
		return sb.toString();
	}

	@Override protected EbeanServer getDatabase() {
		return getRecordDatabase();
	}

}
