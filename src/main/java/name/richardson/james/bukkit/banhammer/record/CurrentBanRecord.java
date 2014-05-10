package name.richardson.james.bukkit.banhammer.record;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;

@Entity
@Table(name = "banhammer_bans")
public class CurrentBanRecord extends SimpleRecord implements BanRecord {

	@ManyToOne(targetEntity = OldPlayerRecord.class, fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
	@PrimaryKeyJoinColumn(name = "creatorId", referencedColumnName = "id")
	private PlayerRecord creator;

	private Timestamp expiresAt;

	@ManyToOne(targetEntity = CurrentPlayerRecord.class, fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
	@PrimaryKeyJoinColumn(name = "playerId", referencedColumnName = "id")
	private PlayerRecord player;

	@NotNull
	private String reason;

	@NotNull
	private State state;

	/**
	 * Find all BanRecords that match a specific state
	 *
	 * @param database the database to use.
	 * @param state the state to match.
	 * @return the BanRecords that match.
	 */
	public static Collection<CurrentBanRecord> find(EbeanServer database, State state) {
		return database.find(CurrentBanRecord.class).where().eq("state", state.ordinal()).findList();
	}

	public static int count(EbeanServer database) {
		return database.find(CurrentBanRecord.class).findRowCount();
	}

	public static List<CurrentBanRecord> list(EbeanServer database) {
		return database.find(CurrentBanRecord.class).findList();
	}

	public static List<CurrentBanRecord> list(EbeanServer database, int count) {
		return database.find(CurrentBanRecord.class).setMaxRows(count).findList();
	}

	@Override public PlayerRecord getCreator() {
		return creator;
	}

	@Override public Timestamp getExpiresAt() {
		return expiresAt;
	}

	@Override public PlayerRecord getPlayer() {
		return player;
	}

	@Override public String getReason() {
		return reason;
	}

	@Override public State getState() {
		return (this.hasExpired()) ? State.EXPIRED : state;
	}

	@Override public Type getType() {
		return (this.expiresAt == null) ? Type.PERMANENT : Type.TEMPORARY;
	}

	/**
	 * Save a BanRecord
	 *
	 * @param database the database to use.
	 * @param record the record to save.
	 * @return true if the record was saved, false otherwise.
	 */
	public static boolean save(EbeanServer database, CurrentBanRecord record) {
		int count = database.save(Arrays.asList(record));
		return count != 0;
	}

	/**
	 * Save a collection of BanRecord
	 *
	 * @param database the database to use.
	 * @param records the records to save.
	 * @return the number of records saved successfully.
	 */
	public static int save(EbeanServer database, Collection<CurrentBanRecord> records) {
		return database.save(records);
	}

	/**
	 * Delete a BanRecord
	 *
	 * @param database the database to use.
	 * @param record the record to save.
	 * @return true if the record was deleted, false otherwise.
	 */
	public static boolean delete(EbeanServer database, CurrentBanRecord record) {
		int count = database.delete(Arrays.asList(record));
		return count != 0;
	}

	/**
	 * Delete a collection of BanRecord
	 *
	 * @param database the database to use.
	 * @param records the records to save.
	 * @return the number of records deleted successfully.
	 */
	public static int delete(EbeanServer database, Collection<CurrentBanRecord> records) {
		return database.delete(records);
	}


	@Override public void setCreator(final PlayerRecord creator) {
		this.creator = creator;
	}

	@Override public void setExpiresAt(final Timestamp expiresAt) {
		this.expiresAt = expiresAt;
	}

	@Override public void setPlayer(final PlayerRecord player) {
		this.player = player;
	}

	@Override public void setReason(final String reason) {
		this.reason = reason;
	}

	@Override public void setState(final State state) {
		this.state = state;
	}

	private boolean hasExpired() {
		return this.getType() == Type.TEMPORARY && ((this.expiresAt.getTime() - System.currentTimeMillis()) < 0);
	}

	public BanRecordFormatter getFormatter() {
		return new BanRecordFormatter(this);
	}

}
