package name.richardson.james.bukkit.banhammer.ban;

import java.util.List;

public interface BanRecordFormatter {

	String getExpiresAt();

	String getHeader();

	String getLength();

	List<String> getMessages();

	String getReason();
}
