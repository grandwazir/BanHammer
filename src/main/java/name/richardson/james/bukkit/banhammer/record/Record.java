package name.richardson.james.bukkit.banhammer.record;

import java.sql.Timestamp;

public interface Record {

	public Timestamp getCreatedAt();

	public Timestamp getUpdatedAt();

	public void setCreatedAt(Timestamp time);

	public void setUpdatedAt(Timestamp time);

}
