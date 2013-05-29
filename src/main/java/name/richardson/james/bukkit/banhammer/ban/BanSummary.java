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

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;
import name.richardson.james.bukkit.utilities.localisation.Localisation;

public class BanSummary {

	private final Localisation localisation;

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
	public BanSummary(final Localisation localisation, final BanRecord ban) {
		this.localisation = localisation;
		this.record = ban;
	}

	/**
	 * Gets a human readable expiry date for this ban.
	 * 
	 * @return the expires at
	 */
	public String getExpiresAt() {
		final String expiryDateString = BanHammer.SHORT_DATE_FORMAT.format(this.record.getExpiresAt());
		return this.localisation.getMessage(this, "expires", expiryDateString);
	}

	/**
	 * Gets a header summary for this ban. This includes the name of the player,
	 * who banned them and when.
	 * 
	 * @return the header
	 */
	public String getHeader() {
		final String date = BanHammer.SHORT_DATE_FORMAT.format(this.record.getCreatedAt());
		return this.localisation.getMessage(this, "header", this.record.getPlayer().getName(), this.record.getCreator().getName(), date);
	}

	/**
	 * Gets a human readable version of the ban length.
	 * 
	 * @return the length
	 */
	public String getLength() {
		if (this.record.getType() == BanRecord.Type.PERMANENT) {
			return this.localisation.getMessage(this, "length", this.localisation.getMessage(this, "permanent"));
		} else {
			final long length = this.record.getExpiresAt().getTime() - this.record.getCreatedAt().getTime();
			return this.localisation.getMessage(this, "length", TimeFormatter.millisToLongDHMS(length));
		}
	}

	/**
	 * Gets the reason for this ban.
	 * 
	 * @return the reason
	 */
	public String getReason() {
		return this.localisation.getMessage(this, "reason", this.record.getReason());
	}

	/**
	 * Gets the self header.
	 * 
	 * @return the self header
	 */
	public String getSelfHeader() {
		final String date = BanHammer.SHORT_DATE_FORMAT.format(this.record.getCreatedAt());
		return this.localisation.getMessage(this, "self-header", this.record.getCreator().getName(), date);
	}

}
