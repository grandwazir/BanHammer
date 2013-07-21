package name.richardson.james.bukkit.banhammer.ban;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;
import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.formatters.colours.CoreColourScheme;
import name.richardson.james.bukkit.utilities.formatters.localisation.Localised;
import name.richardson.james.bukkit.utilities.formatters.localisation.ResourceBundles;


public class BanRecordFormatter implements Localised {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy HH:mm (z)");

	private final ResourceBundle MESSAGES_RESOURCE_BUNDLE = ResourceBundles.MESSAGES.getBundle();
	private final ColourScheme COLOUR_SCHEME = new CoreColourScheme();
	private final BanRecord ban;
	private final List<String> messages= new ArrayList<String>();

	public BanRecordFormatter(BanRecord ban) {
		this.ban = ban;
		messages.add(getHeader());
		messages.add(getReason());
		messages.add(getLength());
		if (ban.getType() != BanRecord.Type.PERMANENT) messages.add(getExpiresAt());
	}

	public String getHeader() {
		final String date = DATE_FORMAT.format(ban.getCreatedAt());
		return COLOUR_SCHEME.format(ColourScheme.Style.WARNING, "header-label", ban.getPlayer().getName(), ban.getCreator().getName(), date);
	}

	public final String getColouredMessage(ColourScheme.Style style, String key, Object... arguments) {
		String message = getResourceBundle().getString(key);
		return getColourScheme().format(style, message, arguments);
	}

	public final ColourScheme getColourScheme() {
		return COLOUR_SCHEME;
	}

	public final String getReason() {
		return getColouredMessage(ColourScheme.Style.INFO, "reason-label", ban.getReason());
	}

	public final String getLength() {
		if (ban.getType() == BanRecord.Type.PERMANENT) {
			return getColouredMessage(ColourScheme.Style.INFO, "length-label", getMessage("permanent"));
		} else {
			final long length = ban.getExpiresAt().getTime() - ban.getCreatedAt().getTime();
			return getColouredMessage(ColourScheme.Style.INFO, "length-label", TimeFormatter.millisToLongDHMS(length));
		}
	}

	public final String getExpiresAt() {
		final String expiryDateString = DATE_FORMAT.format(ban.getExpiresAt());
		return getColouredMessage(ColourScheme.Style.INFO, "expires-label", expiryDateString);
	}

	public final String[] getMessages() {
		return messages.toArray(new String[messages.size()]);
	}

	@Override
	public final ResourceBundle getResourceBundle() {
		return MESSAGES_RESOURCE_BUNDLE;
	}

	@Override
	public final String getMessage(String key, Object... arguments) {
		return MessageFormat.format(MESSAGES_RESOURCE_BUNDLE.getString(key), arguments);
	}
	
}
