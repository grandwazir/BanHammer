/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * OldPlayerRecord.java is part of BanHammer.
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
package name.richardson.james.bukkit.banhammer.legacy;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "banhammer_players")
public class OldPlayerRecord {

	/** The bans. */
	@OneToMany(mappedBy = "player", targetEntity = OldBanRecord.class, cascade = { CascadeType.REMOVE })
	private List<OldBanRecord> bans;
	/** The created bans. */
	@OneToMany(mappedBy = "creator", targetEntity = OldBanRecord.class)
	private List<OldBanRecord> createdBans;
	/** The id. */
	@Id
	private int id;
	/** The name. */
	@NotNull
	private String name;

	public OldBanRecord getActiveBan() {
		for (final OldBanRecord ban : this.getBans()) {
			if (ban.getState() == OldBanRecord.State.NORMAL) {
				return ban;
			}
		}
		return null;
	}

	@OneToMany(targetEntity = OldBanRecord.class, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	public List<OldBanRecord> getBans() {
		return (this.bans == null) ? new LinkedList<OldBanRecord>() : this.bans;
	}

	public void setBans(final List<OldBanRecord> records) {
		this.bans = records;
	}

	@OneToMany(targetEntity = OldBanRecord.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	public List<OldBanRecord> getCreatedBans() {
		return (this.createdBans == null) ? new LinkedList<OldBanRecord>() : this.createdBans;
	}

	public void setCreatedBans(final List<OldBanRecord> records) {
		this.createdBans = records;
	}

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public boolean isBanned() {
		for (final OldBanRecord ban : this.getBans()) {
			if (ban.getState() == OldBanRecord.State.NORMAL) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "OldPlayerRecord{" +
		"bans=" + bans +
		", createdBans=" + createdBans +
		", id=" + id +
		", name='" + name + '\'' +
		'}';
	}

}
