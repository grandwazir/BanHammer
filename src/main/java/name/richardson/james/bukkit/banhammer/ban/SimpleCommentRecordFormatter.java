/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 SimpleCommentRecordFormatter.java is part of BanHammer.

 BanHammer is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option) any
 later version.

 BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.ban;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

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
