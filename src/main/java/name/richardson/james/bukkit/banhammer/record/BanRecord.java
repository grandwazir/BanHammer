package name.richardson.james.bukkit.banhammer.record;

import java.sql.Timestamp;

public interface BanRecord {

	public enum State {
		NORMAL,
		EXPIRED,
		PARDONED
	}

	public enum Type {
		PERMANENT,
		TEMPORARY
	}

	public PlayerRecord getCreator();

	public Timestamp getExpiresAt();

	public PlayerRecord getPlayer();

	public String getReason();

	public State getState();

	public Type getType();

	public void setCreator(PlayerRecord creator);

	public void setExpiresAt(Timestamp expiresAt);

	public void setPlayer(PlayerRecord player);

	public void setReason(String reason);

	public void setState(State state);

}
