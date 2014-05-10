package name.richardson.james.bukkit.banhammer.record;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import name.richardson.james.bukkit.utilities.formatters.time.ApproximateTimeFormatter;
import name.richardson.james.bukkit.utilities.formatters.time.PreciseDurationTimeFormatter;
import name.richardson.james.bukkit.utilities.formatters.time.TimeFormatter;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerMessages.*;

public class SimpleBanRecordFormatter implements BanRecordFormatter {

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
		if (ban.getState() == BanRecord.State.PARDONED) messages.add(getPardoned());
	}

	private String getPardoned() {
		return BAN_WAS_PARDONED.asInfoMessage();
	}

	@Override public String getExpiresAt() {
		final long time = ban.getExpiresAt().getTime();
		return EXPIRES_AT.asInfoMessage(timeFormatter.getHumanReadableDuration(time));
	}

	@Override public String getHeader() {
		final String date = DATE_FORMAT.format(ban.getCreatedAt());
		return BAN_SUMMARY.asHeaderMessage(ban.getPlayer().getLastKnownName(), ban.getCreator().getLastKnownName(), date);
	}

	@Override public String getLength() {
		if (ban.getType() == BanRecord.Type.PERMANENT) {
			return LENGTH.asInfoMessage(PERMANENT.toString());
		} else {
			final long length = ban.getExpiresAt().getTime() - ban.getCreatedAt().getTime();
			return LENGTH.asInfoMessage(durationFormatter.getHumanReadableDuration(length));
		}
	}

	@Override public Collection<String> getMessages() {
		return Collections.unmodifiableCollection(messages);
	}

	@Override public String getReason() {
		return REASON.asInfoMessage(ban.getReason());
	}

}