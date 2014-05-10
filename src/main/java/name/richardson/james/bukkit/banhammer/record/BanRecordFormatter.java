package name.richardson.james.bukkit.banhammer.record;

import java.util.Collection;

/**
 * Created by james on 10/05/14.
 */
public interface BanRecordFormatter {

	String getExpiresAt();

	String getHeader();

	String getLength();

	Collection<String> getMessages();

	String getReason();
}
