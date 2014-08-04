/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 CheckCommand.java is part of BanHammer.

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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractAsynchronousCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.SimpleBooleanMarshaller;

import name.richardson.james.bukkit.banhammer.BanHammerMessages;
import name.richardson.james.bukkit.banhammer.BanHammerMessagesCreator;
import name.richardson.james.bukkit.banhammer.model.BanRecord;
import name.richardson.james.bukkit.banhammer.model.PlayerRecord;
import name.richardson.james.bukkit.banhammer.argument.PlayerNamePositionalArgument;
import name.richardson.james.bukkit.banhammer.argument.ShowCommentSwitchArgument;
import name.richardson.james.bukkit.banhammer.model.BanRecordFormatter;
import name.richardson.james.bukkit.banhammer.model.CommentRecordFormatter;

public class CheckCommand extends AbstractAsynchronousCommand {

	public static final BanHammerMessages MESSAGES = BanHammerMessagesCreator.getColouredMessages();
	private static final String PERMISSION_ALL = "banhammer.check";
	private final Argument player;
	private final SimpleBooleanMarshaller showComments;

	public CheckCommand(final Plugin plugin, final BukkitScheduler scheduler) {
		super(plugin, scheduler);
		player = PlayerNamePositionalArgument.getInstance(0, true, PlayerRecord.Status.BANNED);
		showComments = ShowCommentSwitchArgument.getInstance();
		addArgument(showComments);
		addArgument(player);
	}

	@Override
	public String getName() {
		return MESSAGES.checkCommandName();
	}

	@Override public Set<String> getPermissions() {
		return new HashSet<>(Arrays.asList(PERMISSION_ALL));
	}

	@Override
	public String getDescription() {
		return MESSAGES.checkCommandDescription();
	}

	@Override
	protected void execute() {
		Collection<String> playerNames = player.getStrings();
		for (String playerName : playerNames) {
			PlayerRecord playerRecord = PlayerRecord.find(playerName);
			if (playerRecord != null) {
				BanRecord ban = playerRecord.getActiveBan();
				if (ban != null) {
					BanRecordFormatter formatter = ban.getFormatter();
					addMessages(formatter.getMessages());
				} else {
					String message = MESSAGES.playerNotBanned(playerName);
					addMessage(message);
				}
				if (showComments.isSet()) {
					CommentRecordFormatter formatter = playerRecord.getCommentFormatter();
					addMessages(formatter.getMessages());
				}
			} else {
				String message = MESSAGES.playerNotBanned(playerName);
				addMessage(message);
			}
		}
	}

}
