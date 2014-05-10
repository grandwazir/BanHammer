package name.richardson.james.bukkit.banhammer.record;

import java.sql.Timestamp;

public interface Record {

	public Timestamp getCreatedAt();

	public long getId();

	public void setId(long id);

	public Timestamp getUpdatedAt();

	public void setCreatedAt(Timestamp time);

	public void setUpdatedAt(Timestamp time);

}
