package name.richardson.james.bukkit.banhammer.ban;

import javax.persistence.ManyToOne;
import java.sql.Timestamp;

/**
 * Created by james on 04/05/14.
 */
public interface BanRecord {

	/**
	 * The valid states of a BanRecord
	 */
	public enum State {

		/**
		 * If a ban is currently active.
		 */
		NORMAL,

		/**
		 * If a ban has expired.
		 */
		EXPIRED,

		/**
		 * If a ban has been pardoned.
		 */
		PARDONED
	}

	/**
	 * The valid types of a BanRecord
	 */
	public enum Type {

		/**
		 * A ban which will never expire.
		 */
		PERMANENT,

		/**
		 * A ban which will expire after a period of time.
		 */
		TEMPORARY
	}

	Timestamp getCreatedAt();

	@ManyToOne(targetEntity = OldPlayerRecord.class)
	PlayerRecord getCreator();

	Timestamp getExpiresAt();

	BanRecordFormatter getFormatter();

	int getId();

	PlayerRecord getPlayer();

	String getReason();

	State getState();

	Type getType();

	boolean hasExpired();

	void setCreatedAt(Timestamp time);

	void setCreator(PlayerRecord creator);

	void setExpiresAt(Timestamp expiresAt);

	void setId(int id);

	void setPlayer(PlayerRecord player);

	void setReason(String reason);

	void setState(State state);
}
