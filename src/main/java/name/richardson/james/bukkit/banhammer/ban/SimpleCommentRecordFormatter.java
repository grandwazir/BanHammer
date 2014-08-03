package name.richardson.james.bukkit.banhammer.ban;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import name.richardson.james.bukkit.banhammer.CommentRecord;
import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;

public class SimpleCommentRecordFormatter implements CommentRecordFormatter {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM d");
	public static final Messages MESSAGES = MessagesFactory.getColouredMessages();

	private final Map<Long, CommentRecord> comments;

	public SimpleCommentRecordFormatter(Collection<CommentRecord> comments) {
		this.comments = new TreeMap<>();
		for (CommentRecord comment : comments) {
			this.comments.put(comment.getCreatedAt().getTime(), comment);
		}
	}

	@Override public void removeComments(CommentRecord.Type type) {
		Iterator<Map.Entry<Long, CommentRecord>> i = this.comments.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry<Long, CommentRecord> entry = i.next();
			if (entry.getValue().getType() == type) i.remove();
		}
	}

	@Override public final Collection<String> getMessages() {
		Collection<String> messages = new ArrayList<>();
		messages.add(MESSAGES.commentTotal(this.comments.size()));
		for (CommentRecord comment : this.comments.values()) {
			String date = DATE_FORMAT.format(comment.getCreatedAt());
			String creatorName = comment.getCreator().getName();
			String commentString = comment.getComment();
			messages.add(MESSAGES.commentDetails(date, creatorName, commentString));
		}
		return messages;
	}






}
