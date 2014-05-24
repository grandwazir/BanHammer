package name.richardson.james.bukkit.banhammer.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

import com.avaje.ebean.validation.NotNull;

@MappedSuperclass
public abstract class AbstractRecord {

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp createdAt;
	@Id
	@NotNull
	private UUID id;
	@Version
	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp modifiedAt;

	public final Timestamp getCreatedAt() {
		return createdAt;
	}

	public final UUID getId() {
		return id;
	}

	public final Timestamp getModifiedAt() {
		return modifiedAt;
	}

	public final void setCreatedAt(final Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	protected final void setId(final UUID id) {
		this.id = id;
	}

	protected final void setModifiedAt(final Timestamp modifiedAt) {
		this.modifiedAt = modifiedAt;
	}


}
