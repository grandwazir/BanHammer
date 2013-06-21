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
import java.util.ResourceBundle;

import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.localisation.PluginResourceBundle;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;

public class BanSummary {

	private final ResourceBundle localisation = PluginResourceBundle.getBundle(this.getClass());
	/**
	 * The record we are linked with.
	 */
	private final BanRecord record;

	/**
	 * Instantiates a new ban summary.
	 *
	 * @param ban    the ban record to summarize
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
		return MessageFormat.format(ColourFormatter.info(this.localisation.getString("expires")), expiryDateString);
	}

	/**
	 * Gets a header summary for this ban. This includes the name of the player,
	 * who banned them and when.
	 *
	 * @return the header
	 */
	public String getHeader() {
		final String date = BanHammer.SHORT_DATE_FORMAT.format(this.record.getCreatedAt());
		return MessageFormat.format(ColourFormatter.header(this.localisation.getString("header")), this.record.getPlayer().getName(), this.record.getCreator().getName(), date);
	}

	/**
	 * Gets a human readable version of the ban length.
	 *
	 * @return the length
	 */
	public String getLength() {
		if (this.record.getType() == BanRecord.Type.PERMANENT) {
			return MessageFormat.format(ColourFormatter.info(this.localisation.getString("length")), this.localisation.getString("permanent"));
		} else {
			final long length = this.record.getExpiresAt().getTime() - this.record.getCreatedAt().getTime();
			return MessageFormat.format(ColourFormatter.info(this.localisation.getString("length")), length);
		}
	}

	/**
	 * Gets the reason for this ban.
	 *
	 * @return the reason
	 */
	public String getReason() {
		return MessageFormat.format(ColourFormatter.info(this.localisation.getString("reason")), this.record.getReason());
	}

	/**
	 * Gets the self header.
	 *
	 * @return the self header
	 */
	public String getSelfHeader() {
		final String date = BanHammer.SHORT_DATE_FORMAT.format(this.record.getCreatedAt());
		return MessageFormat.format(ColourFormatter.info(this.localisation.getString("header-self")), this.record.getCreator().getName(), date);
	}

}
