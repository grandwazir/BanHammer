package name.richardson.james.bukkit.banhammer.ban;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;
import name.richardson.james.bukkit.utilities.plugin.Localisable;
import name.richardson.james.bukkit.utilities.plugin.SimplePlugin;

public class BanSummary implements Localisable {

  private final SimplePlugin plugin;

  private final BanRecord record;

  private static final DateFormat dateFormatLength = new SimpleDateFormat("MMM d H:mm a ");

  private static final DateFormat dateFormatCreatedAt = new SimpleDateFormat("MMM d");

  private String tz;

  public BanSummary(final SimplePlugin plugin, final BanRecord record) {
    this.record = record;
    this.plugin = plugin;
    this.tz = Calendar.getInstance(this.getLocale()).getTimeZone().getID();
  }

  public String getChoiceFormattedMessage(final String key, final Object[] arguments, final String[] formats, final double[] limits) {
    return this.plugin.getChoiceFormattedMessage(key, arguments, formats, limits);
  }

  public String getExpiresAt() {
    final String expiryDateString = dateFormatLength.format(this.record.getExpiresAt()) + "(" + this.tz + ")";
    return this.getSimpleFormattedMessage("bansummary-expires", expiryDateString);
  }

  public String getHeader() {
    final String date = dateFormatCreatedAt.format(record.getCreatedAt());
    final Object[] arguments = { this.record.getPlayer(), this.record.getCreatedBy(), date };
    return this.getSimpleFormattedMessage("bansummary-header", arguments);
  }

  public String getLength() {
    if (this.record.getType() == BanRecord.Type.PERMENANT) {
      return this.getSimpleFormattedMessage("bansummary-length", this.getMessage("permanent"));
    } else {
      long length = this.record.getExpiresAt() - this.record.getCreatedAt();
      return this.getSimpleFormattedMessage("bansummary-length", TimeFormatter.millisToLongDHMS(length));
    }
  }

  public Locale getLocale() {
    return this.plugin.getLocale();
  }

  public String getMessage(final String key) {
    return this.plugin.getMessage(key);
  }

  public String getReason() {
    return this.getSimpleFormattedMessage("bansummary-reason", this.record.getReason());
  }

  public String getSelfHeader() {
    final String date = dateFormatCreatedAt.format(this.record.getCreatedAt());
    final Object[] arguments = { this.record.getCreatedBy(), date };
    return this.getSimpleFormattedMessage("bansummary-self-header", arguments);
  }

  public String getSimpleFormattedMessage(final String key, final Object argument) {
    final Object[] arguments = { argument };
    return this.plugin.getSimpleFormattedMessage(key, arguments);
  }

  public String getSimpleFormattedMessage(final String key, final Object[] arguments) {
    return this.plugin.getSimpleFormattedMessage(key, arguments);
  }

}
