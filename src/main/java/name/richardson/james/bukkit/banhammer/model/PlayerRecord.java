package name.richardson.james.bukkit.banhammer.model;

import javax.persistence.*;
import java.util.Set;

import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.banhammer.BanHammer;

@Entity
@Table(name = BanHammer.TABLE_PREFIX + "players")
public class PlayerRecord extends AbstractRecord {

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

}
