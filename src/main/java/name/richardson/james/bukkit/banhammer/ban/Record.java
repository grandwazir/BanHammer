package name.richardson.james.bukkit.banhammer.ban;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.sql.Timestamp;

import com.avaje.ebean.validation.NotNull;

@MappedSuperclass
public abstract class Record {

	@NotNull
	private Timestamp createdAt;
	@Id
	private long id;
	@Version
	private Timestamp updatedAt;

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public long getId() {
		return id;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setCreatedAt(final Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setUpdatedAt(final Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}
