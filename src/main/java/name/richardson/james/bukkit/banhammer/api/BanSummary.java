/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * BanSummary.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.api;

import java.util.Locale;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;
import name.richardson.james.bukkit.utilities.plugin.Localisable;
import name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin;

public class BanSummary implements Localisable {

  /** The plugin with the resource bundles. */
  private final SkeletonPlugin plugin;

  /** The record we are linked with. */
  private final BanRecord record;

  /**
   * Instantiates a new ban summary.
   *
   * @param plugin the plugin containing the resource bundles
   * @param ban the ban record to summarize
   */
  public BanSummary(final SkeletonPlugin plugin, final BanRecord ban) {
    this.record = ban;
    this.plugin = plugin;
  }

  /* (non-Javadoc)
   * @see name.richardson.james.bukkit.utilities.plugin.Localisable#getChoiceFormattedMessage(java.lang.String, java.lang.Object[], java.lang.String[], double[])
   */
  public String getChoiceFormattedMessage(String key, final Object[] arguments, final String[] formats, final double[] limits) {
    key = this.getClass().getSimpleName().toLowerCase() + "." + key;
    return this.plugin.getChoiceFormattedMessage(key, arguments, formats, limits);
  }

  /**
   * Gets a human readable expiry date for this ban.
   * 
   * @return the expires at
   */
  public String getExpiresAt() {
    final String expiryDateString = BanHammer.DATE_FORMAT.format(this.record.getExpiresAt());
    return this.getSimpleFormattedMessage("expires", expiryDateString);
  }

  /**
   * Gets a header summary for this ban. This includes the name of the player, who banned them and when.
   *
   * @return the header
   */
  public String getHeader() {
    final String date = BanHammer.DATE_FORMAT.format(this.record.getCreatedAt());
    final Object[] arguments = { this.record.getPlayer().getName(), this.record.getCreator().getName(), date };
    return this.getSimpleFormattedMessage("header", arguments);
  }

  /**
   * Gets a human readable version of the ban length.
   *
   * @return the length
   */
  public String getLength() {
    if (this.record.getType() == BanRecord.Type.PERMANENT) {
      return this.getSimpleFormattedMessage("length", this.getMessage("permanent"));
    } else {
      final long length = this.record.getExpiresAt().getTime() - this.record.getCreatedAt().getTime();
      return this.getSimpleFormattedMessage("length", TimeFormatter.millisToLongDHMS(length));
    }
  }

  /* (non-Javadoc)
   * @see name.richardson.james.bukkit.utilities.plugin.Localisable#getLocale()
   */
  public Locale getLocale() {
    return this.plugin.getLocale();
  }

  /* (non-Javadoc)
   * @see name.richardson.james.bukkit.utilities.plugin.Localisable#getMessage(java.lang.String)
   */
  public String getMessage(String key) {
    key = this.getClass().getSimpleName().toLowerCase() + "." + key;
    return this.plugin.getMessage(key);
  }

  /**
   * Gets the reason for this ban.
   *
   * @return the reason
   */
  public String getReason() {
    return this.getSimpleFormattedMessage("reason", this.record.getReason());
  }

  /**
   * Gets the self header.
   *
   * @return the self header
   */
  public String getSelfHeader() {
    final String date = BanHammer.DATE_FORMAT.format(this.record.getCreatedAt());
    final Object[] arguments = { this.record.getCreator().getName(), date };
    return this.getSimpleFormattedMessage("self-header", arguments);
  }

  /* (non-Javadoc)
   * @see name.richardson.james.bukkit.utilities.plugin.Localisable#getSimpleFormattedMessage(java.lang.String, java.lang.Object)
   */
  public String getSimpleFormattedMessage(String key, final Object argument) {
    key = this.getClass().getSimpleName().toLowerCase() + "." + key;
    return this.plugin.getSimpleFormattedMessage(key, argument);
  }

  /* (non-Javadoc)
   * @see name.richardson.james.bukkit.utilities.plugin.Localisable#getSimpleFormattedMessage(java.lang.String, java.lang.Object[])
   */
  public String getSimpleFormattedMessage(String key, final Object[] arguments) {
    key = this.getClass().getSimpleName().toLowerCase() + "." + key;
    return this.plugin.getSimpleFormattedMessage(key, arguments);
  }

}
