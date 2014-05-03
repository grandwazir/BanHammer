/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * PlayerRecord.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.ban;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "banhammer_players")
public class OldPlayerRecord implements PlayerRecord {

	/** The bans. */
	@OneToMany(mappedBy = "player", targetEntity = OldBanRecord.class, cascade = { CascadeType.REMOVE })
	private List<BanRecord> bans;
	/** The created bans. */
	@OneToMany(mappedBy = "creator", targetEntity = OldBanRecord.class)
	private List<BanRecord> createdBans;
	/** The id. */
	@Id
	private int id;
	/** The name. */
	@NotNull
	private String name;

	@Override
	public BanRecord getActiveBan() {
		for (final BanRecord ban : this.getBans()) {
			if (ban.getState() == OldBanRecord.State.NORMAL) {
				return ban;
			}
		}
		return null;
	}

	@Override
	@OneToMany(targetEntity = OldBanRecord.class, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	public List<BanRecord> getBans() {
		return (this.bans == null) ? new LinkedList<BanRecord>() : this.bans;
	}

	@Override
	public void setBans(final List<BanRecord> records) {
		this.bans = records;
	}

	@Override
	@OneToMany(targetEntity = OldBanRecord.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	public List<BanRecord> getCreatedBans() {
		return (this.createdBans == null) ? new LinkedList<BanRecord>() : this.createdBans;
	}

	@Override
	public void setCreatedBans(final List<BanRecord> records) {
		this.createdBans = records;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(final int id) {
		this.id = id;
	}

	@Override
	public UUID getUUID() {
		return null;
	}

	@Override
	public void setUUID(final UUID uuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public boolean isBanned() {
		for (final BanRecord ban : this.getBans()) {
			if (ban.getState() == OldBanRecord.State.NORMAL) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "PlayerRecord{" +
		"bans=" + bans +
		", createdBans=" + createdBans +
		", id=" + id +
		", name='" + name + '\'' +
		'}';
	}

}
