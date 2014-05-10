package name.richardson.james.bukkit.banhammer.record;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import java.sql.Timestamp;

import com.avaje.ebean.validation.NotNull;

@MappedSuperclass
public abstract class SimpleRecord implements Record {

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp createdAt;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp updatedAt;

	@Override public Timestamp getCreatedAt() {
		return createdAt;
	}

	@Override public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	@Override public void setCreatedAt(final Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@Override public void setUpdatedAt(final Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}
