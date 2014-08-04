/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 PlayerPositionalArgument.java is part of BanHammer.

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

import org.bukkit.Server;

import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.ArgumentMetadata;
import name.richardson.james.bukkit.utilities.command.argument.PlayerMarshaller;
import name.richardson.james.bukkit.utilities.command.argument.PositionalArgument;
import name.richardson.james.bukkit.utilities.command.argument.RequiredPlayerMarshaller;
import name.richardson.james.bukkit.utilities.command.argument.RequiredPositionalArgument;
import name.richardson.james.bukkit.utilities.command.argument.SimpleArgumentMetadata;
import name.richardson.james.bukkit.utilities.command.argument.SimplePlayerMarshaller;
import name.richardson.james.bukkit.utilities.command.argument.suggester.OnlinePlayerSuggester;
import name.richardson.james.bukkit.utilities.command.argument.suggester.Suggester;

import name.richardson.james.bukkit.banhammer.BanHammerMessages;
import name.richardson.james.bukkit.banhammer.BanHammerMessagesCreator;

public final class PlayerPositionalArgument {

	public static final BanHammerMessages MESSAGES = BanHammerMessagesCreator.getMessages();

	public static PlayerMarshaller getInstance(Server server, int position, boolean required) {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(MESSAGES.playerArgumentId(), MESSAGES.playerArgumentName(), MESSAGES.playerArgumentDescription(), MESSAGES.playerArgumentInvalid());
		Suggester suggester = new OnlinePlayerSuggester(server);
		if (required) {
			Argument argument = new RequiredPositionalArgument(metadata, suggester, position);
			return new RequiredPlayerMarshaller(argument, server);
		} else {
			Argument argument = new PositionalArgument(metadata, suggester, position);
			return new SimplePlayerMarshaller(argument, server);
		}
	}

}
