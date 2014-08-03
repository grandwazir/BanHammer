/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 SimpleBanRecordFormatter.java is part of BanHammer.

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
import java.util.List;

import name.richardson.james.bukkit.utilities.time.ApproximateTimeFormatter;
import name.richardson.james.bukkit.utilities.time.PreciseDurationTimeFormatter;
import name.richardson.james.bukkit.utilities.time.TimeFormatter;

import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.banhammer.CommentRecord;
import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;

public class SimpleBanRecordFormatter implements BanRecordFormatter {

	private static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy HH:mm (z)");
	private final BanRecord ban;
	private final TimeFormatter durationFormatter = new PreciseDurationTimeFormatter();
	private final List<String> messages = new ArrayList<String>();
	private final TimeFormatter timeFormatter = new ApproximateTimeFormatter();

	public SimpleBanRecordFormatter(BanRecord ban) {
		this.ban = ban;
		messages.add(getHeader());
		messages.add(getReason());
		messages.add(getLength());
		if (ban.getType() != BanRecord.Type.PERMANENT && ban.getState() != BanRecord.State.PARDONED) messages.add(getExpiresAt());
		if (ban.getState() == BanRecord.State.PARDONED) {
			CommentRecord comment = ban.getComment(CommentRecord.Type.PARDON_REASON);
			messages.add(MESSAGES.banPardonedBy(comment.getCreator().getName(), comment.getComment()));
		}
	}

	@Override public final String getExpiresAt() {
		long time = ban.getExpiresAt().getTime();
		String duration = timeFormatter.getHumanReadableDuration(time);
		return MESSAGES.banExpiresAt(duration);
	}

	@Override public final String getHeader() {
		String date = DATE_FORMAT.format(ban.getCreatedAt());
		return MESSAGES.banSummary(ban.getPlayer().getName(), ban.getCreator().getName(), date);
	}

	@Override public final String getLength() {
		if (ban.getType() == BanRecord.Type.PERMANENT) {
			return MESSAGES.banLength(BanRecord.Type.PERMANENT.toString());
		} else {
			long length = ban.getExpiresAt().getTime() - ban.getCreatedAt().getTime();
			return MESSAGES.banLength(durationFormatter.getHumanReadableDuration(length));
		}
	}

	@Override public List<String> getMessages() {
		return messages;
	}

	@Override public final String getReason() {
		return MESSAGES.banReason(ban.getComment(CommentRecord.Type.BAN_REASON).getComment());
	}

}
