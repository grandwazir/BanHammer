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
package name.richardson.james.bukkit.banhammer.ban;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;
import name.richardson.james.bukkit.utilities.localisation.Localised;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundles;

public class BanSummary implements Localised {

	private final static ResourceBundle localisation = ResourceBundle.getBundle(ResourceBundles.MESSAGES.getBundleName());

	/** The record we are linked with. */
	private final BanRecord record;

	/**
	 * Instantiates a new ban summary.
	 * 
	 * @param plugin
	 *          the plugin containing the resource bundles
	 * @param ban
	 *          the ban record to summarize
	 */
	public BanSummary(final BanRecord ban) {
		this.record = ban;
	}

	/**
	 * Gets a human readable expiry date for this ban.
	 * 
	 * @return the expires at
	 */
	public String getExpiresAt() {
		final String expiryDateString = BanHammer.SHORT_DATE_FORMAT.format(this.record.getExpiresAt());
		return this.getMessage("shared.expires", expiryDateString);
	}

	/**
	 * Gets a header summary for this ban. This includes the name of the player,
	 * who banned them and when.
	 * 
	 * @return the header
	 */
	public String getHeader() {
		final String date = BanHammer.SHORT_DATE_FORMAT.format(this.record.getCreatedAt());
		return this.getMessage("shared.header", this.record.getPlayer().getName(), this.record.getCreator().getName(), date);
	}

	/**
	 * Gets a human readable version of the ban length.
	 * 
	 * @return the length
	 */
	public String getLength() {
		if (this.record.getType() == BanRecord.Type.PERMANENT) {
			return this.getMessage("shared.length", this.getMessage("shared.permanent"));
		} else {
			final long length = this.record.getExpiresAt().getTime() - this.record.getCreatedAt().getTime();
			return this.getMessage("shared.length", TimeFormatter.millisToLongDHMS(length));
		}
	}

	public String getMessage(final String key) {
		String message = BanSummary.localisation.getString(key);
		message = ColourFormatter.replace(message);
		return message;
	}

	public String getMessage(final String key, final Object... elements) {
		final MessageFormat formatter = new MessageFormat(BanSummary.localisation.getString(key));
		formatter.setLocale(Locale.getDefault());
		String message = formatter.format(elements);
		message = ColourFormatter.replace(message);
		return message;
	}

	/**
	 * Gets the reason for this ban.
	 * 
	 * @return the reason
	 */
	public String getReason() {
		return this.getMessage("shared.reason", this.record.getReason());
	}

	/**
	 * Gets the self header.
	 * 
	 * @return the self header
	 */
	public String getSelfHeader() {
		final String date = BanHammer.SHORT_DATE_FORMAT.format(this.record.getCreatedAt());
		return this.getMessage("shared.header-self", this.record.getCreator().getName(), date);
	}

}
