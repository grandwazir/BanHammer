package name.richardson.james.bukkit.banhammer.argument;

import name.richardson.james.bukkit.utilities.command.argument.ArgumentMetadata;
import name.richardson.james.bukkit.utilities.command.argument.SimpleArgumentMetadata;
import name.richardson.james.bukkit.utilities.command.argument.SimpleBooleanMarshaller;
import name.richardson.james.bukkit.utilities.command.argument.SwitchArgument;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;

public class ShowCommentSwitchArgument extends SimpleBooleanMarshaller {

	public static final Messages MESSAGES = MessagesFactory.getMessages();

	public static ShowCommentSwitchArgument getInstance() {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(MESSAGES.commentArgumentId(), MESSAGES.commentArgumentName(), MESSAGES.commentArgumentDescription());
		return new ShowCommentSwitchArgument(metadata);
	}

	private ShowCommentSwitchArgument(final ArgumentMetadata metadata) {
		super(new SwitchArgument(metadata));
	}


}
