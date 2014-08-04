/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 PlayerRecordMatcher.java is part of BanHammer.

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

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import name.richardson.james.bukkit.utilities.command.argument.suggester.Suggester;

import name.richardson.james.bukkit.banhammer.model.PlayerRecord;

public class PlayerRecordMatcher implements Suggester {

	private static final int MINIMUM_ARGUMENT_LENGTH = 3;
	private final PlayerRecord.Status mode;

	public PlayerRecordMatcher(PlayerRecord.Status mode) {
		this.mode = mode;
	}

	@Override
	public Set<String> suggestValue(String argument) {
		if (argument.length() < MINIMUM_ARGUMENT_LENGTH) return Collections.emptySet();
		TreeSet<String> results = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		argument = argument.toLowerCase(Locale.ENGLISH);
		for (PlayerRecord playerRecord : PlayerRecord.find(mode, argument)) {
			if (results.size() == Suggester.MAX_MATCHES) break;
			if (!playerRecord.getName().toLowerCase(Locale.ENGLISH).startsWith(argument)) continue;
			results.add(playerRecord.getName());
		}
		return results;
	}

}
