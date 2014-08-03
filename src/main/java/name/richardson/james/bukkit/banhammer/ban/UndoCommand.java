/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 UndoCommand.java is part of BanHammer.

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
package name.richardson.james.bukkit.banhammer.ban;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractSynchronousCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;

import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.PlayerRecord;
import name.richardson.james.bukkit.banhammer.PluginConfiguration;
import name.richardson.james.bukkit.banhammer.argument.PlayerNamePositionalArgument;

public class UndoCommand extends AbstractSynchronousCommand {

	public static final String PERMISSION_OWN = "banhammer.undo.own";
	public static final String PERMISSION_OTHERS = "banhammer.undo.others";
	public static final String PERMISSION_UNRESTRICTED = "banhammer.undo.unrestricted";
	private static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	private final PluginConfiguration configuration;
	private final Argument players;

	public UndoCommand(final Plugin plugin, final BukkitScheduler scheduler, PluginConfiguration configuration) {
		super(plugin, scheduler);
		this.configuration = configuration;
		this.players = PlayerNamePositionalArgument.getInstance(0, true, PlayerRecord.Status.BANNED);
		addArgument(players);
	}

	@Override public String getDescription() {
		return MESSAGES.undoCommandDescription();
	}

	@Override public String getName() {
		return MESSAGES.undoCommandName();
	}

	@Override public Set<String> getPermissions() {
		return new HashSet<>(Arrays.asList(PERMISSION_OWN, PERMISSION_OTHERS, PERMISSION_UNRESTRICTED));
	}

	@Override
	protected void execute() {
		final Collection<String> playerNames = this.players.getStrings();
		for (String playerName : playerNames) {
			PlayerRecord playerRecord = PlayerRecord.find(playerName);
			BanRecord ban = (playerRecord == null) ? null : playerRecord.getActiveBan();
			if (playerRecord == null) {
				addMessage(MESSAGES.playerNotBanned(playerName));
			} else if (ban == null) {
				addMessage(MESSAGES.playerNotBanned(playerName));
			} else if (!hasPermission(ban.getPlayer().getId())) {
				addMessage(MESSAGES.undoNotPermitted(ban.getCreator().getName()));
			} else if (!withinTimeLimit(ban.getCreatedAt().getTime())) {
				addMessage(MESSAGES.undoTimeExpired());
			} else {
				ban.delete();
				addMessage(MESSAGES.undoComplete(ban.getCreator().getName(), playerName));
			}
		}
	}

	private boolean hasPermission(final UUID playerUUID) {
		final boolean isSenderTargetingSelf = (getContext().getCommandSenderUUID().compareTo(playerUUID) == 0);
		return isAuthorised(PERMISSION_OWN) && isSenderTargetingSelf || isAuthorised(PERMISSION_OTHERS) && !isSenderTargetingSelf;
	}

	private boolean withinTimeLimit(final long time) {
		return isAuthorised(PERMISSION_UNRESTRICTED) || (System.currentTimeMillis() - time) <= configuration.getUndoTime();
	}

}
