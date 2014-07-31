package name.richardson.james.bukkit.banhammer.argument;

import org.bukkit.Server;

import name.richardson.james.bukkit.utilities.command.argument.*;
import name.richardson.james.bukkit.utilities.command.argument.suggester.OnlinePlayerSuggester;
import name.richardson.james.bukkit.utilities.command.argument.suggester.Suggester;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.player.PlayerRecord;
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
