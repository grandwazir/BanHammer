/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 SimpleTimeMarshaller.java is part of BanHammer.

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
package name.richardson.james.bukkit.banhammer.argument;

import java.util.Date;

import name.richardson.james.bukkit.utilities.command.argument.AbstractMarshaller;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.time.PreciseTimeFormatter;
import name.richardson.james.bukkit.utilities.time.TimeFormatter;

public class SimpleTimeMarshaller extends AbstractMarshaller implements TimeMarshaller {

	private final TimeFormatter formatter = new PreciseTimeFormatter();

	public SimpleTimeMarshaller(final Argument argument) {
		super(argument);
	}

	@Override public Date getDate() {
		return new Date(System.currentTimeMillis() + getTime());
	}

	@Override public long getTime() {
		return formatter.getDurationInMilliseconds(getString());
	}

}
