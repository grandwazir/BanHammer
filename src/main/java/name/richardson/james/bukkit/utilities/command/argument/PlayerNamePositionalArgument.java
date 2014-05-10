package name.richardson.james.bukkit.utilities.command.argument;

import org.bukkit.Server;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.command.argument.suggester.OnlinePlayerSuggester;
import name.richardson.james.bukkit.utilities.command.argument.suggester.Suggester;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.utilities.command.matcher.PlayerRecordMatcher;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerMessages.*;

public class PlayerNamePositionalArgument {

	public static Argument getInstance(EbeanServer database, int position, boolean required, final PlayerRecord.PlayerStatus playerStatus) {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(ARGUMENT_PLAYER_ID, ARGUMENT_PLAYER_NAME, ARGUMENT_PLAYER_DESC, ARGUMENT_PLAYER_NAME_ERROR);
		Suggester suggester = new PlayerRecordMatcher(database, playerStatus);
		if (required) {
			return new RequiredPositionalArgument(metadata, suggester, position);
		} else {
			return new PositionalArgument(metadata, suggester, position);
		}
	}

	public static Argument getInstance(Server server, int position, boolean required) {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(ARGUMENT_PLAYER_ID, ARGUMENT_PLAYER_NAME, ARGUMENT_PLAYER_DESC, ARGUMENT_PLAYER_NAME_ERROR);
		Suggester suggester = new OnlinePlayerSuggester(server);
		if (required) {
			return new RequiredPositionalArgument(metadata, suggester, position);
		} else {
			return new PositionalArgument(metadata, suggester, position);
		}
	}

}
