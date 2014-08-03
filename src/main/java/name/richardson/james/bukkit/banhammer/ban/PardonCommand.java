/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * PardonCommand.java is part of BanHammer.
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

import java.util.*;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractAsynchronousCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;

import name.richardson.james.bukkit.banhammer.*;
import name.richardson.james.bukkit.banhammer.argument.PlayerNamePositionalArgument;
import name.richardson.james.bukkit.banhammer.argument.ReasonPositionalArgument;
import name.richardson.james.bukkit.banhammer.argument.SilentSwitchArgument;

public class PardonCommand extends AbstractAsynchronousCommand {

	public static final String PERMISSION_ALL = "banhammer.pardon";
	public static final String PERMISSION_OWN = "banhammer.pardon.own";
	public static final String PERMISSION_OTHERS = "banhammer.pardon.others";
	private static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	private final Argument players;
	private final Argument reason;
	private final SilentSwitchArgument silent;

	public PardonCommand(final Plugin plugin, final BukkitScheduler scheduler) {
		super(plugin, scheduler);
		this.silent = SilentSwitchArgument.getInstance();
		this.players = PlayerNamePositionalArgument.getInstance(0, true, PlayerRecord.Status.BANNED);
		this.reason = ReasonPositionalArgument.getInstance(1, true);
		addArgument(silent);
		addArgument(players);
		addArgument(reason);
	}

	@Override public String getDescription() {
		return MESSAGES.pardonCommandDescription();
	}

	@Override public String getName() {
		return MESSAGES.pardonCommandName();	}

	@Override public Set<String> getPermissions() {
		return new HashSet<>(Arrays.asList(PERMISSION_ALL, PERMISSION_OTHERS, PERMISSION_OWN));
	}

	@Override
	protected void execute() {
		boolean silent = this.silent.isSet();
		final Collection<String> players = this.players.getStrings();
		final Collection<BanRecord> bans = new ArrayList<BanRecord>();
		for (String playerName : players) {
			final PlayerRecord playerRecord = PlayerRecord.find(playerName);
			if (playerRecord != null && playerRecord.getActiveBan() != null) {
				final BanRecord ban = playerRecord.getActiveBan();
				if (isAuthorised(ban.getCreator().getId())) {
					CommentRecord comment = CommentRecord.create(ban.getCreator(), ban, this.reason.getString());
					ban.setState(BanRecord.State.PARDONED);
					ban.setComment(comment);
					ban.save();
					bans.add(ban);
					if (silent) addMessage(MESSAGES.playerPardoned(playerName));
				} else {
					addMessage(MESSAGES.unableToPardonPlayer(playerName));
				}
			} else {
				addMessage(MESSAGES.playerNotBanned(playerName));
			}
		}
		new BanHammerPlayerPardonedEvent(bans, getContext().getCommandSender(), silent);
	}

	private boolean isAuthorised(UUID playerUUID) {
		final boolean isSenderTargetingSelf = (getContext().getCommandSenderUUID().compareTo(playerUUID) == 0);
		return getContext().isAuthorised(PERMISSION_OWN) && isSenderTargetingSelf || getContext().isAuthorised(PERMISSION_OTHERS) && !isSenderTargetingSelf;
	}

}
