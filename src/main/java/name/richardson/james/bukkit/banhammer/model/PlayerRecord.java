package name.richardson.james.bukkit.banhammer.model;

import javax.persistence.*;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.utilities.NameFetcher;
import name.richardson.james.bukkit.banhammer.utilities.UUIDFetcher;

@Entity
@Table(name = BanHammer.TABLE_PREFIX + "players")
public class PlayerRecord extends AbstractRecord {

	public enum Status {
		CREATOR,
		BANNED,
		ANY
	}

	private static EbeanServer getDatabase() {
		return Ebean.getServer("BanHammerVersion3");
	}

	public static PlayerRecord find(UUID uuid) {
		return getDatabase().find(PlayerRecord.class).where().eq("uuid", uuid).findUnique();
	}

	public static PlayerRecord find(String name) {
		return getDatabase().find(PlayerRecord.class).where().ieq("name", name).findUnique();
	}

	public static Set<PlayerRecord> find(Status status) {
		Set<PlayerRecord> records = getDatabase().find(PlayerRecord.class).findSet();
		return filterPlayers(records, status);
	}

	public static Set<PlayerRecord> find(Status status, String name) {
		Set<PlayerRecord> records = getDatabase().find(PlayerRecord.class).where().istartsWith("name", name).findSet();
		return filterPlayers(records, status);
	}

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
		PlayerRecord record = new PlayerRecord();
		record.setName(name);
		record.setId(uuid);
		record.save();
		return record;
	}

	private void save() {
		getDatabase().save(this);
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

	@OneToMany(mappedBy = "player", targetEntity = BanRecord.class)
	private Set<BanRecord> bans;
	@ManyToMany(targetEntity = CommentRecord.class)
	@JoinTable(name = BanHammer.TABLE_PREFIX + "players_has_comments")
	private Set<CommentRecord> comments;
	@OneToMany(mappedBy = "creator", targetEntity = BanRecord.class)
	private Set<BanRecord> createdBans;
	@OneToMany(mappedBy = "creator", targetEntity = CommentRecord.class)
	private Set<CommentRecord> createdComments;
	@NotNull
	private String name;

	public Set<BanRecord> getBans() {
		return bans;
	}

	public Set<CommentRecord> getComments() {
		return comments;
	}

	public Set<BanRecord> getCreatedBans() {
		return createdBans;
	}

	public Set<CommentRecord> getCreatedComments() {
		return createdComments;
	}

	public String getName() {
		return name;
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

	public boolean isBanned() {
		return true;
	}

}
