package name.richardson.james.bukkit.banhammer.api;

import java.util.Locale;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;
import name.richardson.james.bukkit.utilities.plugin.Localisable;
import name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin;

public class BanSummary implements Localisable {

  /** The plugin with the resource bundles */
  private final SkeletonPlugin plugin;

  /** The record we are linked with */
  private final BanRecord record;

  public BanSummary(final SkeletonPlugin plugin, final BanRecord ban) {
    this.record = ban;
    this.plugin = plugin;
  }

  public String getChoiceFormattedMessage(final String key, final Object[] arguments, final String[] formats, final double[] limits) {
    return this.plugin.getChoiceFormattedMessage(key, arguments, formats, limits);
  }

  public String getExpiresAt() {
    final String expiryDateString = BanHammer.DATE_FORMAT.format(record.getExpiresAt());
    return this.getSimpleFormattedMessage("expires", expiryDateString);
  }

  public String getHeader() {
    final String date = BanHammer.DATE_FORMAT.format(record.getCreatedAt());
    final Object[] arguments = { this.record.getPlayer().getName(), this.record.getCreater().getName(), date };
    return this.getSimpleFormattedMessage("header", arguments);
  }

  public String getLength() {
    if (this.record.getType() == BanRecord.Type.PERMENANT) {
      return this.getSimpleFormattedMessage("length", this.getMessage("permanent"));
    } else {
      long length = this.record.getExpiresAt().getTime() - this.record.getCreatedAt().getTime();
      return this.getSimpleFormattedMessage("length", TimeFormatter.millisToLongDHMS(length));
    }
  }

  public Locale getLocale() {
    return this.plugin.getLocale();
  }

  public String getMessage(final String key) {
    return this.plugin.getMessage(key);
  }

  public String getReason() {
    return this.getSimpleFormattedMessage("reason", this.record.getReason());
  }

  public String getSelfHeader() {
    final String date = BanHammer.DATE_FORMAT.format(record.getCreatedAt());
    final Object[] arguments = { this.record.getCreater().getName(), date };
    return this.getSimpleFormattedMessage("self-header", arguments);
  }

  public String getSimpleFormattedMessage(final String key, final Object argument) {
    final Object[] arguments = { argument };
    return this.plugin.getSimpleFormattedMessage(key, arguments);
  }

  public String getSimpleFormattedMessage(final String key, final Object[] arguments) {
    return this.plugin.getSimpleFormattedMessage(key, arguments);
  }

}
