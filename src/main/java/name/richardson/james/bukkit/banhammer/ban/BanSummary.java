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
  
  private final String tz = Calendar.getInstance(this.getLocale()).getTimeZone().toString();
  
  public BanSummary(SimplePlugin plugin, BanRecord record) {
    this.record = record;
    this.plugin = plugin;
  }
  
  public String getLength() {
    if (record.getType() == BanRecord.Type.PERMENANT) {
      return this.getSimpleFormattedMessage("bansummary-length", this.getMessage("permenant"));
    } else {
      return this.getSimpleFormattedMessage("bansummary-length", TimeFormatter.millisToLongDHMS(record.getExpiresAt()));
    }
  }
  
  public String getReason() {
    return this.getSimpleFormattedMessage("bansummary-reason", record.getReason());
  }
  
  public String getHeader() {
    final String date = dateFormatCreatedAt.format(dateFormatCreatedAt);
    final Object[] arguments = { record.getPlayer(), record.getCreatedBy(), date };
    return this.getSimpleFormattedMessage("bansummary-header", arguments);
  }
  
  public String getExpiresAt() {
    final String expiryDateString =  dateFormatLength.format(record.getExpiresAt()) + "(" + tz + ")";
    return this.getSimpleFormattedMessage("bansummary-expires", expiryDateString);
  }

  public String getChoiceFormattedMessage(final String key, final Object[] arguments, final String[] formats, final double[] limits) {
    return this.plugin.getChoiceFormattedMessage(key, arguments, formats, limits);
  }

  public Locale getLocale() {
    return plugin.getLocale();
  }

  public String getMessage(final String key) {
    return plugin.getMessage(key);
  }

  public String getSimpleFormattedMessage(final String key, final Object argument) {
    final Object[] arguments = { argument };
    return plugin.getSimpleFormattedMessage(key, arguments);
  }

  public String getSimpleFormattedMessage(final String key, final Object[] arguments) {
    return plugin.getSimpleFormattedMessage(key, arguments);
  }

  
}
