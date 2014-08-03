package name.richardson.james.bukkit.banhammer.ban;

import java.util.Collection;

import name.richardson.james.bukkit.banhammer.CommentRecord;

/**
 * Created by james on 03/08/14.
 */
public interface CommentRecordFormatter {

	Collection<String> getMessages();

	void removeComments(CommentRecord.Type type);
}
