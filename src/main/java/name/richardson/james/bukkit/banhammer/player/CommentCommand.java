/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 CommentCommand.java is part of BanHammer.

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
package name.richardson.james.bukkit.banhammer.player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractAsynchronousCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;

import name.richardson.james.bukkit.banhammer.CommentRecord;
import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.PlayerRecord;
import name.richardson.james.bukkit.banhammer.argument.PlayerNamePositionalArgument;
import name.richardson.james.bukkit.banhammer.argument.ReasonPositionalArgument;

public class CommentCommand extends AbstractAsynchronousCommand {

	public static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	private static final String PERMISSION_ALL = "banhammer.comment";
	private final Argument playerName;
	private final Argument reason;

	public CommentCommand(final Plugin plugin, final BukkitScheduler scheduler, Server server) {
		super(plugin, scheduler);
		playerName = PlayerNamePositionalArgument.getInstance(server, 0, true);
		reason = ReasonPositionalArgument.getInstance(1, true);
		addArgument(playerName);
		addArgument(reason);
	}

	@Override public String getDescription() {
		return MESSAGES.commentCommandDescription();
	}

	@Override public String getName() {
		return MESSAGES.commentCommandName();
	}

	@Override public Set<String> getPermissions() {
		return new HashSet<>(Arrays.asList(PERMISSION_ALL));
	}

	@Override protected void execute() {
		try {
			PlayerRecord creator = PlayerRecord.create(getContext().getCommandSender().getName());
			for (String playerName : this.playerName.getStrings()) {
				PlayerRecord player = PlayerRecord.create(playerName);
				CommentRecord comment = CommentRecord.create(creator, player, this.reason.getString());
				comment.setType(CommentRecord.Type.NORMAL);
				player.addComment(comment);
				player.save();
				addMessage(MESSAGES.commentAttached(playerName));
			}
		} catch (PlayerNotFoundException e) {
			addMessage(MESSAGES.playerLookupException());
		}
	}


}
