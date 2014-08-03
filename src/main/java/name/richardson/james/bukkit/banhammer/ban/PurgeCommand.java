/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * PurgeCommand.java is part of BanHammer.
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

import javax.net.ssl.SSLContext;
import java.util.*;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.command.AbstractAsynchronousCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.BooleanMarshaller;

import name.richardson.james.bukkit.banhammer.*;
import name.richardson.james.bukkit.banhammer.argument.DeleteCommentSwitchArgument;
import name.richardson.james.bukkit.banhammer.argument.PlayerNamePositionalArgument;

public class PurgeCommand extends AbstractAsynchronousCommand {

	private static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	private static final String PERMISSION_ALL = "banhammer.purge";
	private static final String PERMISSION_OWN = "banhammer.purge.own";
	private static final String PERMISSION_OTHERS = "banhammer.purge.others";
	private final EbeanServer database;
	private final BooleanMarshaller delete;
	private final Argument players;

	public PurgeCommand(final Plugin plugin, final BukkitScheduler scheduler, final EbeanServer database) {
		super(plugin, scheduler);
		this.delete = DeleteCommentSwitchArgument.getInstance();
		this.players = PlayerNamePositionalArgument.getInstance(0, true, PlayerRecord.Status.ANY);
		this.database = database;
		addArgument((Argument) delete);
		addArgument(players);
	}

	@Override public String getDescription() {
		return MESSAGES.purgeCommandDescription();
	}

	@Override public String getName() {
		return MESSAGES.purgeCommandName();
	}

	@Override public Set<String> getPermissions() {
		return new HashSet<>(Arrays.asList(PERMISSION_ALL, PERMISSION_OTHERS, PERMISSION_OWN));
	}

	@Override
	protected void execute() {
		Collection<String> playerNames = this.players.getStrings();
		Collection<BanRecord> bans = new ArrayList<>();
		Collection<CommentRecord> comments = new ArrayList<>();
		boolean own = getContext().isAuthorised(PERMISSION_OWN);
		boolean others = getContext().isAuthorised(PERMISSION_OTHERS);
		for (String playerName : playerNames) {
			PlayerRecord record = PlayerRecord.find(playerName);
			if (record != null) {
				for (BanRecord ban : record.getBans()) {
					final boolean banCreatedBySender = (ban.getCreator().getId().compareTo(getContext().getCommandSenderUUID()) == 0);
					if (banCreatedBySender && !own) continue;
					if (!banCreatedBySender && !others) continue;
					bans.add(ban);
				}
				if (this.delete.isSet()) {
					for (CommentRecord comment : record.getComments()) {
						final boolean banCreatedBySender = (comment.getCreator().getId().compareTo(getContext().getCommandSenderUUID()) == 0);
						if (banCreatedBySender && !own) continue;
						if (!banCreatedBySender && !others) continue;
						comments.add(comment);
					}
				}
			} else {
				String message = MESSAGES.playerNotBanned(playerName);
				addMessage(message);
			}
			deleteBans(bans);
			deleteComments(comments);
			addMessage(MESSAGES.bansPurged(bans.size()));
			addMessage(MESSAGES.commentsPurged(comments.size()));
		}
	}

	private void deleteBans(Collection<BanRecord> bans) {
		for (BanRecord ban : bans) {
			database.refresh(ban);
			database.delete(ban);
		}
	}

	private void deleteComments(Collection<CommentRecord> comments) {
		for (CommentRecord comment : comments) {
			database.refresh(comment);
			database.delete(comment);
		}
	}

}

