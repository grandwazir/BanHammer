package name.richardson.james.bukkit.banhammer.record;

import javax.persistence.*;
import java.sql.Timestamp;

import com.avaje.ebean.validation.NotNull;

@MappedSuperclass
public abstract class Record {

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp createdAt;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp updatedAt;

	public Timestamp getCreatedAt() {
		return createdAt;
	}


	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setCreatedAt(final Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public void setUpdatedAt(final Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}
