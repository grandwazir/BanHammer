package name.richardson.james.bukkit.banhammer.model;

import javax.persistence.*;
import java.util.Set;

import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.banhammer.BanHammer;

@Entity
@Table(name = BanHammer.TABLE_PREFIX + "comments")
public class CommentRecord extends AbstractRecord {

	public enum Type {
		NORMAL,
		BAN_REASON,
		PARDON_REASON,
	}
	@ManyToMany(mappedBy = "comments")
	private Set<BanRecord> bans;
	@NotNull
	private String comment;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_id")
	private PlayerRecord creator;
	@ManyToMany(mappedBy = "comments")
	private Set<PlayerRecord> players;
	private Type type;

	public Set<BanRecord> getBans() {
		return bans;
	}

	public String getComment() {
		return comment;
	}

	public PlayerRecord getCreator() {
		return creator;
	}

	public Set<PlayerRecord> getPlayers() {
		return players;
	}

	public Type getType() {
		return type;
	}

	public void setBans(final Set<BanRecord> bans) {
		this.bans = bans;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public void setCreator(final PlayerRecord creator) {
		this.creator = creator;
	}

	public void setPlayers(final Set<PlayerRecord> players) {
		this.players = players;
	}

	public void setType(final Type type) {
		this.type = type;
	}

}
