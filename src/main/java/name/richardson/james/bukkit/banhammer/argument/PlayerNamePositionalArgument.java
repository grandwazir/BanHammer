/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 PlayerNamePositionalArgument.java is part of BanHammer.

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

import name.richardson.james.bukkit.utilities.command.argument.*;
import name.richardson.james.bukkit.utilities.command.argument.suggester.OnlinePlayerSuggester;
import name.richardson.james.bukkit.utilities.command.argument.suggester.Suggester;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.PlayerRecord;
import name.richardson.james.bukkit.banhammer.player.PlayerRecordMatcher;

public class PlayerNamePositionalArgument {

	public static final Messages MESSAGES = MessagesFactory.getMessages();

	public static Argument getInstance(int position, boolean required, final PlayerRecord.Status playerStatus) {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(MESSAGES.playerNameArgumentId(), MESSAGES.playerNameArgumentName(), MESSAGES.playerNameArgumentDescription(), MESSAGES.playerNameArgumentError());
		Suggester suggester = new PlayerRecordMatcher(playerStatus);
		if (required) {
			return new RequiredPositionalArgument(metadata, suggester, position);
		} else {
			return new PositionalArgument(metadata, suggester, position);
		}
	}

	public static Argument getInstance(Server server, int position, boolean required) {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(MESSAGES.playerNameArgumentId(), MESSAGES.playerNameArgumentName(), MESSAGES.playerNameArgumentDescription(), MESSAGES.playerNameArgumentError());
		Suggester suggester = new OnlinePlayerSuggester(server);
		if (required) {
			return new RequiredPositionalArgument(metadata, suggester, position);
		} else {
			return new PositionalArgument(metadata, suggester, position);
		}
	}

}
